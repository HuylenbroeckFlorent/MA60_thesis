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
		w = new PrefixTree(alphabetSize);

		// Counter-examples with infinite language
		ArrayList<CounterExample> infiniteUni = new ArrayList(); 
		ArrayList<CounterExample> infiniteEx = new ArrayList(); 


		// Add all u from pos in prefixtree and set them as to be accepted
		for(String pos : s.getPos()){
			w.addWord(pos, map, 1, null, null);
		}

		// Add all u from pos in prefixtree and set them as to be rejected
		for(String neg : s.getNeg()){
			w.addWord(neg, map, -1, null, null);
		}

		// Add all u antecedent from uni in prefixtree, and if language is finite, add all word from langage too (less computation of y variables)
		for(CounterExample cu : s.getUni()){
			Automaton a = cu.getA();
			if(a.isFinite()){
				w.addWord(cu.getU(), map, 0, a, null);
			}
			else{
				System.out.println("/!\\ /!\\ infinite universal counter-example : \n"+cu.toString());
				infiniteUni.add(cu);
			}
		}

		// Add all u antecedent from ex in prefixtree, and if language is finite, add all word from langage too (less computation of z variables)
		for(CounterExample ce : s.getEx()){
			Automaton a = ce.getA();
			if(a.isFinite()){
				w.addWord(ce.getU(), map, 0, null, a);
			}
			else{
				System.out.println("/!\\ /!\\ infinite existential counter-example : \n"+ce.toString());
				infiniteEx.add(ce);
			}
		}

		System.out.println("Prefix tree size = "+w.size);

		// Declare boolean var arrays
		f = new BoolExpr[n];
		d = new BoolExpr[n][alphabetSize][n];
		x = new BoolExpr[w.size][n];

		// Init f variables
		for(int q=0; q<n; q++){
			//f[q]= context.mkBoolConst(Integer.toString(varID++));
			f[q] = context.mkBoolConst("f"+Integer.toString(q));
		}

		// Init d variables
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){
				for(int q=0; q<n; q++){
					//d[p][a][q]=context.mkBoolConst(Integer.toString(varID++));
					d[p][a][q]=context.mkBoolConst("d "+Integer.toString(p)+" "+invertedMap.get(a)+" "+Integer.toString(q));
				}
			}
		}

		// Init x variables
		for(int u=0; u<w.size; u++){
			for(int q=0; q<n; q++){
				//x[u][q]=context.mkBoolConst(Integer.toString(varID++));
				x[u][q]=context.mkBoolConst("x "+Integer.toString(u)+" "+Integer.toString(q));
			}
		}

		// at least one f
		BoolExpr at_least_one_f = context.mkFalse();
		for(int q=0; q<n; q++){
			at_least_one_f = context.mkOr(at_least_one_f, f[q]);
		}
		//solver.add(at_least_one_f);

		

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

			// prefix has univseral consequences
			if(prefix.uni!=null){

				BoolExpr uni_ante = context.mkFalse();
				for(int p=0; p<n; p++){
					uni_ante = context.mkOr(context.mkAnd(x[prefix.id][p], f[p]));
				}

				BoolExpr uni_cons = context.mkTrue();
				for(Node u : prefix.uni){
					for(int q=0; q<n; q++){
						uni_cons = context.mkAnd(uni_cons, context.mkImplies(x[u.id][q], f[q]));
					}
				}
				solver.add(context.mkImplies(uni_ante, uni_cons));
			}

			// prefix has existential consequences
			if(prefix.ex!=null){

				BoolExpr ex_ante = context.mkFalse();
				for(int p=0; p<n; p++){
					ex_ante = context.mkOr(context.mkAnd(x[prefix.id][p], f[p]));
				}

				BoolExpr ex_cons = context.mkFalse();
				for(Node e : prefix.uni){
					for(int q=0; q<n; q++){
						ex_cons = context.mkOr(ex_cons, context.mkImplies(x[e.id][q], f[q]));
					}
				}
				solver.add(context.mkImplies(ex_ante, ex_cons));
			}

			// accept or reject
			if(prefix.accept>0){
				for(int q=0; q<n; q++){
					solver.add(context.mkAnd(x[prefix.id][q], f[q]));
				}
			}
			else if(prefix.accept<0){
				for(int q=0; q<n; q++){
					solver.add(context.mkAnd(x[prefix.id][q], context.mkNot(f[q])));
				}
			}


		}
		return solver;

	}

	private Automaton translateModel(Model model){

		System.out.println("  == MODEL ==");
		System.out.println(model);
		System.out.println();

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