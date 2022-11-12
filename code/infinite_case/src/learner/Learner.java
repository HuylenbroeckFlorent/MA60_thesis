package learner;

import java.util.*;
import java.util.stream.*;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.*;

import transducer.*;
import teacher.*;

/**
* Learner for safety games. This object can be used, paired with a teacher for safety games,
* to find a winning set for a safety game.
*
* @author HUYLENBROECK Florent
*/
public class Learner{

	/**
	* The current number of state in the (to be) conjectured DFA.
	*/
	public int n;

	/**
	* Mapping between the original alphabet and the numbering of its characters.
	*/
	public Map<Character, Integer> map;

	/**
	* Mapping between the numbering of the original alphabet and its characters.
	*/
	public Map<Integer, Character> invertedMap;

	/**
	* Alphabet used in the safety game.
	*/
	public char[] alphabet;

	/**
	* size of the alphabet used in the safety game.
	*/
	public int alphabetSize;

	/**
	* Sample used to store the counter-examples told by the teacher.
	*/ 
	public Sample s;

	/**
	* Prefix tree containing all the words in the sample and their acceptance behaviour for the learning.
	*/
	private PrefixTree w;

	/**
	* Array containing the f-boolean variables.
	*/
	private BoolExpr[] f;

	/**
	* Array containing the d-boolean variables.
	*/
	private BoolExpr[][][] d;

	/**
	* Array containing the x-boolean variables.
	*/
	private BoolExpr[][] x;

	/**
	* Array containing the y-boolean variables.
	*/
	private BoolExpr[][][] y;

	/**
	* Array containing the z-boolean variables.
	*/
	private BoolExpr[][][][] z;

	/**
	* Boolean to enable debug mode.
	*/
	private boolean debug;

	/**
	* Boolean to enable verbose mode.
	*/
	private boolean verbose;

	/**
	* Constructor. Once constructed, the learner should be used for the whole process og the learning, since it holds a sample
	* for the counter-examples returned by the teacher, and the current number of state for the DFA
	*
	* @param alphabet char[] alphabet used in the safety game.
	* @param verbose boolean. Setting this to true enable verbose mode.
	* @param verbose boolean. Setting this to true enable debug mode.
	*/
	public Learner(char[] alphabet, boolean verbose, boolean debug){
		n=1;
		this.alphabet=alphabet;
		alphabetSize=alphabet.length;
		s = new Sample();
		map = new HashMap();
		invertedMap = new HashMap();
		for(int i=0; i<alphabetSize; i++){
			map.put(alphabet[i], i);
			invertedMap.put(i, alphabet[i]);
		}

		this.verbose=verbose;
		this.debug=debug;
	}

	/**
	* Conjectures a DFA with n states and coherent with the sample.  
	*
	* @return Automaton accepting the winning set for player 0.
	*/
	public Automaton conjecture(){

		// Start back form previous n (this will be incremented upon entering the loop).
		n--;

		// Initialize Z3 for the SAT solving.
		Context context = new Context();
		Status status = Status.UNSATISFIABLE;
		Solver solver = context.mkSolver();
		Model model;

		// Begin the learning
		do{
			n++;
			if(verbose)
				System.out.println("n = "+n);

			// Construnt the SAT problem.
			solver = constructSAT(context, s, n);

			// Check for satisfiability
			status = solver.check();

			if(debug){
				System.out.println(solver);
				if(status == Status.UNSATISFIABLE){
					BoolExpr[] unsat = solver.getUnsatCore();
					System.out.println(unsat.length);
					for(BoolExpr be : unsat){
						System.out.println("UNSAT : "+be);
					}
				}
				System.out.println("DEBUG - press ENTER to continue.");
				String line = new Scanner(System.in).nextLine();
			}
			
		}while(status!=Status.SATISFIABLE); // If problem is satisfiable, exit loop. Else, n will be incremented.

		// Retrieve learned model.
		model = solver.getModel();
		if(debug){
			System.out.println(model);
			System.out.println("DEBUG - press ENTER to continue.");
			String line = new Scanner(System.in).nextLine();
		}

		// Translate the model to a DFA and return it.
		return translateModel(model);
	}

	/**
	* Construct a SAT problem which, upon satisfiability, will be converted to a DFA coherent with the sample.
	* To do so, several boolean variables are created then constrained.
	* 
	* f-variables are used to constraint the accepting states of the DFA. f_q = true means state q will be accepting.
	* d-variables are used to describe the transitions in the DFA. d_p,a,q = true means there is a transition from state p to state q using character a.
	* x-variables are used to run words on the DFA. x_u,q = true means state q is reached upon reading word u from initial state.
	* y-variables are used to keep track of parallel runs of the DFA and any NFA being the consequence of a universal implication counter-example.
	*				y_q,q' = true means pair of states (q,q') x Q, Q' is reached upon reading a word from the langage shared by both automatas.
	* z-variables are a more robust version of y variables. In addition to keep the pair of states reached, they also keep track of the length of the word read.
	*				z_q,q',l = true means pair of states (q,q') x Q, Q' is reached upon reading a word with |word|= l.
	*
	* @param context Context used by Z3 solver to create variables and constraints.
	* @param s Sample used to store the counter-examples.
	* @param n int. The number of state in the target DFA.
	* @return Solver. The Solver object used by Z3 to satisfy the problem.
	*/
	private Solver constructSAT(Context context, Sample s, int n){

		// Solver
		Solver solver = context.mkSolver();

		// Number to be mapped to each variable, to retrieve from model. Since we do not use SAT4J anymore, we can start at 0
		int varID=0;

		// Prefix tree for x variables (TODO find a way to not initialize everytime)
		w = new PrefixTree(alphabetSize, map);

		// Add all u from pos in prefixtree and set them as to be accepted
		for(String pos : s.getPos()){
			w.addWord(pos, 1);
		}

		// Add all u from pos in prefixtree and set them as to be rejected
		for(String neg : s.getNeg()){
			w.addWord(neg, -1);
		}

		// Add all universal implication counter-examples antecedent to the prefix tree and do not set their acceptance behaviour.
		for(CounterExample cu : s.getUni()){
			w.addWord(cu.getU(), 0);
		}

		// Add all existential implication counter-examples antecedent to the prefix tree and do not set their acceptance behaviour.
		for(CounterExample ce : s.getEx()){
			w.addWord(ce.getU(), 0);
		}

		if(verbose)
			System.out.println("Prefix tree size = "+w.size);

		// Declare boolean var arrays
		f = new BoolExpr[n];
		d = new BoolExpr[n][alphabetSize][n];
		x = new BoolExpr[w.size][n];
		y = new BoolExpr[s.getUni().size()][][];  // 3rd dimension length is initilized individually for each counter-example.
		z = new BoolExpr[s.getEx().size()][][][];  // 3rd and 4th dimension length is initilized individually for each counter-example.


		// Init f variables
		for(int q=0; q<n; q++){
			if(!debug)
				f[q]= context.mkBoolConst(Integer.toString(varID++));
			else{
				varID++;
				f[q] = context.mkBoolConst("f"+Integer.toString(q));
			}
		}


		// Init d variables
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){
				for(int q=0; q<n; q++){
					if(!debug)
						d[p][a][q]=context.mkBoolConst(Integer.toString(varID++));
					else{
						varID++;
						d[p][a][q]=context.mkBoolConst("d "+Integer.toString(p)+" "+invertedMap.get(a)+" "+Integer.toString(q));
					}
				}
			}
		}

		// Init x variables
		for(int u=0; u<w.size; u++){
			for(int q=0; q<n; q++){
				if(!debug)
					x[u][q]=context.mkBoolConst(Integer.toString(varID++));
				else{
					varID++;
					x[u][q]=context.mkBoolConst("x "+w.getWord(u)+" "+Integer.toString(q));
				}
			}
		}

		// Init and constraint y variables
		if (s.getUni().size()>0){
			ArrayList<CounterExample> cus = s.getUni(); 
			for(int u=0; u<cus.size(); u++){
				CounterExample cu = cus.get(u);
				Automaton cua = cu.getA();

				// init y for this counter-example
				int na = cua.getNumberOfStates();
				y[u] = new BoolExpr[n][na];
				for(int p=0; p<n; p++){
					for(int p2=0; p2<na; p2++){
						if(!debug)
							y[u][p][p2] = context.mkBoolConst(Integer.toString(varID++));
						else{
							varID++;
							y[u][p][p2] = context.mkBoolConst("y "+Integer.toString(p)+" "+Integer.toString(p2));
						}
					}
				}

				// find a way to deterministically access states by number between 0 and nstates-1 in A
				Set<State> cuas = cua.getStates();
				Automaton.setStateNumbers(cuas); // This calls setStateNumbers so states should now be numbered from 0 to na
				
				// constraint initial
				solver.add(y[u][0][cua.initial.number]);

				// constraint transitions
				for(State state : cuas){ // p_A in q_A
					Set<Transition> transitions = state.getTransitions(); 
					for(Transition transition : transitions){ // d_pA,a,qA 
						int a = map.get(transition.getMin()); // a
						for(int p=0; p<n; p++){ // p in Q
							for(int q=0; q<n; q++){ // q in Q
								solver.add(context.mkImplies(context.mkAnd(y[u][p][state.number], d[p][a][q]), y[u][q][transition.getDest().number]));
							}
						}
					}
				}

				// accept uni
				BoolExpr accept_uni_ante = context.mkFalse();
				BoolExpr accept_uni_cons = context.mkTrue();
				int uante = w.getNode(cu.getU()).id;
				for(int q=0; q<n; q++){
					accept_uni_ante = context.mkOr(accept_uni_ante, context.mkAnd(x[uante][q], f[q]));
					for(State fa : cua.getAcceptStates()){
						accept_uni_cons = context.mkAnd(accept_uni_cons, context.mkImplies(y[u][q][fa.number], f[q]));
					}
				}
				solver.add(context.mkImplies(accept_uni_ante, accept_uni_cons));
			}
		}

		// init and constraint z variables
		if (s.getEx().size()>0){
			ArrayList<CounterExample> ces = s.getEx(); 
			for(int e=0; e<ces.size(); e++){
				CounterExample ce = ces.get(e);
				Automaton cea = ce.getA();

				// init y for this counter-example
				int na = cea.getNumberOfStates();
				int l_max = (n*na)-1;
				z[e] = new BoolExpr[n][na][l_max];
				for(int p=0; p<n; p++){
					for(int p2=0; p2<na; p2++){
						for(int l=0; l<l_max; l++){
							if(!debug)
								z[e][p][p2][l] = context.mkBoolConst(Integer.toString(varID++));
							else{
								varID++;
								z[e][p][p2][l] = context.mkBoolConst("z "+Integer.toString(p)+" "+Integer.toString(p2)+" "+Integer.toString(l));
							}
						}
					}
				}
				// find a way to deterministically access states by number between 0 and nstates-1 in A
				Set<State> ceas = cea.getStates();
				Automaton.setStateNumbers(ceas); // This calls setStateNumbers so states should now be numbered from 0 to nstates-1 

				// constraint initial
				solver.add(z[e][0][cea.initial.number][0]); // (q_O,q_A0) only valid run for |word| = 0
				for(State state : ceas){

					for(int q=0; q<n; q++){
						if(state.number == cea.initial.number && q==0){
							continue;
						}
						solver.add(context.mkNot(z[e][q][state.number][0])); // Every other (q,q_A) not valid run for |word|=0
					}
				}

				for(State state : ceas){
					// constraint transitions
					for(Transition transition : state.getTransitions()){ // dA(pA,a,qA)
						int a = map.get(transition.getMin());
						for(int p=0; p<n; p++){ // p in Q
							for(int q=0; q<n; q++){ // q in Q
								for(int l=0; l<l_max-1; l++){ // l 
									solver.add(context.mkImplies(context.mkAnd(z[e][p][state.number][l], d[p][a][q]), z[e][q][transition.getDest().number][l+1]));
								}
							}
						}
					}

					// constraint reverse-transitions
					for(int q=0; q<n; q++){ // q in Q
						for(int l=1; l<l_max; l++){ // l in 1,...,k
							BoolExpr reverse_transition = context.mkFalse();
							Boolean hadtransitions = false;
							for(State state2 : ceas){ // p_A in Q_A
								for(Transition transition : state2.getTransitions()){
									if(transition.getDest().number == state.number){
										hadtransitions = true;
										int a = map.get(transition.getMin()); // a
										for(int p=0; p<n; p++){ // p in Q
											reverse_transition = context.mkOr(reverse_transition, context.mkAnd(d[p][a][q], z[e][p][state2.number][l-1]));
										}
									}
								}

							}
							if(hadtransitions){
								solver.add(context.mkImplies(z[e][q][state.number][l], reverse_transition));
							}
							else{
								// This one wasn't in Neider's paper. In addition to the 'reverse-transition' constraint, if no transition to a state exist, set its z-variable to false.
							 	solver.add(context.mkNot(z[e][q][state.number][l]));
							}
						}
					}
				}

				// If antecedent u is accepted, all consequent words v in L(A) should be accepted 
				BoolExpr accept_ex_ante = context.mkFalse();
				BoolExpr accept_ex_cons = context.mkFalse();
				int eante = w.getNode(ce.getU()).id;
				for(int q=0; q<n; q++){ // q in Q
					accept_ex_ante = context.mkOr(accept_ex_ante, context.mkAnd(x[eante][q], f[q]));
					for(State fa : cea.getAcceptStates()){ // qA in F_A
						for(int l=0; l<l_max; l++){ // l in 0, ... ,k
							accept_ex_cons = context.mkOr(accept_ex_cons, context.mkAnd(z[e][q][fa.number][l], f[q]));
						}
					}
				}
				solver.add(context.mkImplies(accept_ex_ante, accept_ex_cons));
			}
		}

		if(verbose)
			System.out.println("nVar="+varID);

		// Setting constraints on d
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){
				
				// total function
				BoolExpr total = context.mkFalse();
				for(int q=0; q<n; q++){
					total = context.mkOr(total, d[p][a][q]);
					for(int q2=q+1; q2<n; q2++){
						solver.add(context.mkNot(context.mkAnd(d[p][a][q], d[p][a][q2])));
					}
				}
				solver.add(total);
			}
		}

		// initial state
		solver.add(x[w.initial.id][0]);
		for(int q=1; q<n; q++){
			solver.add(context.mkNot(x[w.initial.id][q]));
		}

		// Constraint x for each prefix in the tree.
		ArrayList<Node> prefixes = new ArrayList();
		prefixes.add(w.initial);
		while(!prefixes.isEmpty()){
			Node prefix = prefixes.get(0);
			prefixes.remove(0);

			// at most one state reached upon reading the same word.
			for(int q1=0; q1<n; q1++){
				for(int q2=q1+1; q2<n; q2++){
					solver.add(context.mkNot(context.mkAnd(x[prefix.id][q1], x[prefix.id][q2])));
				}
			}

			// transitions
			for(int a=0; a<alphabetSize; a++){
				if(prefix.children[a]!=null){
					for(int p=0; p<n; p++){
						for(int q=0; q<n; q++){
							solver.add(context.mkImplies(context.mkAnd(x[prefix.id][p], d[p][a][q]), x[prefix.children[a].id][q]));
						}
					}

					// Since we're at it, add children node to prefixes list.
					prefixes.add(prefix.children[a]);
				}
			}

			// acceptance behaviour
			if(prefix.accept>0){
				for(int q=0; q<n; q++){
					solver.add(context.mkImplies(x[prefix.id][q], f[q]));
				}
			}
			else if(prefix.accept<0){
				for(int q=0; q<n; q++){
					solver.add(context.mkImplies(x[prefix.id][q], context.mkNot(f[q])));
				}
			}
		}
		return solver;

	}

	/**
	* Translates a model returned by Z3 SAT solver satisfying the learner formulas to a DFA (Q, alphabet, q_0, transition_function, F)
	* With:
	*  		Q = {0, ... ,n-1}
	*		alphabet is already known
	* 		q_0 = state 0
	*		transition_function : d_p,a,q = true means there's a transition from state p to state q unsing character a
	*		F = {q in Q | f_q = true}
	*
	* @param model Model returned by Z3 SAT solver.
	* @return Automaton translated from model.
	*/
	private Automaton translateModel(Model model){

		// Create DFA.
		Automaton conjectured = new Automaton();

		// Initializes the n states and set their acceptance behaviour.
		Map<Integer, State> states = new HashMap();
		for(int i=0; i<n; i++){
			State tmp = new State();
			if(model.eval(f[i], true).getBoolValue().equals(Z3_lbool.Z3_L_TRUE)){
			 	tmp.setAccept(true);
			}
			else{
				tmp.setAccept(false);
			}
			tmp.number=i;
			states.put(i, tmp);
		}

		// Set initial state.
		conjectured.setInitialState(states.get(0));

		// Create transitions.
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){
				for(int q=0; q<n; q++){
					if(model.eval(d[p][a][q],true).getBoolValue().equals(Z3_lbool.Z3_L_TRUE)){
						Transition tmp = new Transition(invertedMap.get(a), states.get(q));
						states.get(p).addTransition(tmp);
					}
				}
			}
		}

		// Minimize the DFA
		if(!debug)
			conjectured.minimize();

		return conjectured;
	}
}