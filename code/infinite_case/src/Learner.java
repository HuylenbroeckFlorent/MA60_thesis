import java.util.*;
import java.util.stream.*;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.*;

import transducer.*;

public class Learner{

	public int n;
	public Map<Character, Integer> map;
	public Map<Integer, Character> invertedMap;
	public char[] alphabet;
	public int alphabetSize;

	public Sample s;

	private PrefixTree w;
	private BoolExpr[] f;
	private BoolExpr[][][] d;
	private BoolExpr[][] x;
	private BoolExpr[][][] y;
	private BoolExpr[][][][] z;

	public Learner(char[] alphabet){
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
	}

	public Automaton conjecture(){

		n--;
		Context context = new Context();
		Status status = Status.UNSATISFIABLE;
		Solver solver = context.mkSolver();
		Model model;
		do{
			n++;
			System.out.println("n = "+n);
			solver = constructSAT(context, s, n);
			//System.out.println(solver);
			status = solver.check();
			// if(status == Status.UNSATISFIABLE){
			// 	BoolExpr[] unsat = solver.getUnsatCore();
			// 	System.out.println(unsat.length);
			// 	for(BoolExpr be : unsat){
			// 		System.out.println("UNSAT : "+be);
			// 	}
			// }
		}while(status!=Status.SATISFIABLE);

		model = solver.getModel();
		return translateModel(model);
	}

	public Solver constructSAT(Context context, Sample s, int n){

		// Solver
		Solver solver = context.mkSolver();

		// Number to be mapped to each variable, to retrieve from model. Since we do not use SAT4J anymore, we can start at 0
		int varID=0;

		// Prefix tree for x variables (TODO find a way to not initialize everytime)
		w = new PrefixTree(alphabetSize, map);

		// Counter-examples with infinite language
		ArrayList<CounterExample> infiniteUni = new ArrayList(); 
		ArrayList<CounterExample> infiniteEx = new ArrayList(); 


		// Add all u from pos in prefixtree and set them as to be accepted
		for(String pos : s.getPos()){
			w.addWord(pos, 1, null, null);
		}

		// Add all u from pos in prefixtree and set them as to be rejected
		for(String neg : s.getNeg()){
			w.addWord(neg, -1, null, null);
		}

		// Add all u antecedent from uni in prefixtree, and if language is finite, add all word from langage too (less computation of y variables)
		// for(CounterExample cu : s.getUni()){
		// 	Automaton a = cu.getA();
		// 	if(a.isFinite()){
		// 		w.addWord(cu.getU(), map, 0, a, null);
		// 	}
		// 	else{
		// 		System.out.println("/!\\ /!\\ infinite universal counter-example : \n"+cu.toString());
		// 		infiniteUni.add(cu);
		// 	}
		// }

		for(CounterExample cu : s.getUni()){
			w.addWord(cu.getU(), 0, null, null);
		}

		// Add all u antecedent from ex in prefixtree, and if language is finite, add all word from langage too (less computation of z variables)
		// for(CounterExample ce : s.getEx()){
		// 	Automaton a = ce.getA();
		// 	if(a.isFinite()){
		// 		w.addWord(ce.getU(), 0, null, a);
		// 	}
		// 	else{
		// 		System.out.println("/!\\ /!\\ infinite existential counter-example : \n"+ce.toString());
		// 		infiniteEx.add(ce);
		// 	}
		// }

		for(CounterExample ce : s.getEx()){
			w.addWord(ce.getU(), 0, null, null);
		}

		System.out.println("Prefix tree size = "+w.size);

		// Declare boolean var arrays
		f = new BoolExpr[n];
		d = new BoolExpr[n][alphabetSize][n];
		x = new BoolExpr[w.size][n];
		y = new BoolExpr[s.getUni().size()][][];  // 3rd dimension length is initilized per-y
		z = new BoolExpr[s.getEx().size()][][][];  // 3rd and 4th dimension length is initilized per-z


		// Init f variables
		for(int q=0; q<n; q++){
			f[q]= context.mkBoolConst(Integer.toString(varID++));
			//f[q] = context.mkBoolConst("f"+Integer.toString(q));
		}


		// Init d variables
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){
				for(int q=0; q<n; q++){
					d[p][a][q]=context.mkBoolConst(Integer.toString(varID++));
					//d[p][a][q]=context.mkBoolConst("d "+Integer.toString(p)+" "+invertedMap.get(a)+" "+Integer.toString(q));
				}
			}
		}

		// Init x variables
		for(int u=0; u<w.size; u++){
			for(int q=0; q<n; q++){
				x[u][q]=context.mkBoolConst(Integer.toString(varID++));
				//x[u][q]=context.mkBoolConst("x "+Integer.toString(u)+" "+Integer.toString(q));
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
						y[u][p][p2] = context.mkBoolConst(Integer.toString(varID++));
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
				int uante = w.getWord(cu.getU()).id;
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
							z[e][p][p2][l] = context.mkBoolConst(Integer.toString(varID++));
						}
					}
				}
				// find a way to deterministically access states by number between 0 and nstates-1 in A
				Set<State> ceas = cea.getStates();
				Automaton.setStateNumbers(ceas); // This calls setStateNumbers so states should now be numbered from 0 to nstates-1 

				// constraint initial
				solver.add(z[e][0][cea.initial.number][0]); // (q_O,q_A0) only valid run for |word| = 0
				for(State state : ceas){
					if(state.number == cea.initial.number){
						continue;
					}
					for(int q=1; q<n; q++){
						solver.add(context.mkNot(z[e][q][state.number][0])); // Every other (q,q_A) not valid run for |word|=0
					}
				}

				for(State state : ceas){

					// constraint transitions
					Set<Transition> transitions = state.getTransitions();

					for(Transition transition : transitions){ // dA(pA,a,qA)

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

							for(State state2 : ceas){ // p_A in Q_A

								for(Transition transition : state2.getTransitions()){

									if(transition.getDest().equals(state)){

										int a = map.get(transition.getMin()); // a

										for(int p=0; p<n; p++){ // p in Q

											reverse_transition = context.mkOr(reverse_transition, context.mkAnd(d[p][a][q], z[e][p][state2.number][l-1]));
										}
									}
								}

							}
							solver.add(context.mkImplies(z[e][q][state.number][l], reverse_transition));
						}
					}
				}

				// accept ex
				BoolExpr accept_ex_ante = context.mkFalse();
				BoolExpr accept_ex_cons = context.mkFalse();
				int eante = w.getWord(ce.getU()).id;
				for(int q=0; q<n; q++){ // q in Q
					accept_ex_ante = context.mkOr(accept_ex_ante, context.mkAnd(x[eante][q], f[q]));
					for(State fa : cea.getAcceptStates()){ // qA in F_A
						for(int l=0; l<l_max; l++){ // l
							accept_ex_cons = context.mkOr(accept_ex_cons, context.mkAnd(z[e][q][fa.number][l], f[q]));
						}
					}
				}
				solver.add(context.mkImplies(accept_ex_ante, accept_ex_cons));
			}
		}

		System.out.println("nVar="+varID);

		// // at least one f (DO NOT SEEM NECESSARY)
		// BoolExpr at_least_one_f = context.mkFalse();
		// for(int q=0; q<n; q++){
		// 	at_least_one_f = context.mkOr(at_least_one_f, f[q]);
		// }
		// //solver.add(at_least_one_f);


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


		// Setting constraints on x and f

		// initial state
		solver.add(x[w.initial.id][0]);
		for(int q=1; q<n; q++){
			solver.add(context.mkNot(x[w.initial.id][q]));
		}

		ArrayList<Node> prefixes = new ArrayList();
		prefixes.add(w.initial);

		while(!prefixes.isEmpty()){

			Node prefix = prefixes.get(0);
			prefixes.remove(0);

			// determinism-ish 
			if(prefix.id>0){
				for(int q1=0; q1<n; q1++){
					for(int q2=q1+1; q2<n; q2++){
						solver.add(context.mkNot(context.mkAnd(x[prefix.id][q1], x[prefix.id][q2])));
					}
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

					// Since we're at it, add children node to prefixes list
					prefixes.add(prefix.children[a]);
				}
			}

			// accept or reject
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

	private Automaton translateModel(Model model){

		// System.out.println("MODEL :");
		// System.out.println(model);
		// System.out.println();

		Map<Integer, State> states = new HashMap();

		for(int i=0; i<n; i++){
			State tmp = new State();
			if(model.eval(f[i], true).getBoolValue().equals(Z3_lbool.Z3_L_TRUE)){
			 	tmp.setAccept(true);
			}
			else{
				tmp.setAccept(false);
			}
			states.put(i, tmp);
		}

		Automaton conjectured = new Automaton();
		conjectured.setInitialState(states.get(0));

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

		conjectured.minimize();

		return conjectured;
	}
}