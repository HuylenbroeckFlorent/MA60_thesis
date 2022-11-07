import java.util.*;
import java.util.stream.*;

import org.sat4j.*;
import org.sat4j.specs.*;
import org.sat4j.core.*;
import org.sat4j.minisat.*;

import transducer.*;

public class Learner{

	public int n;
	public Map<Character, Integer> map;
	public Map<Integer, Character> invertedMap;
	public char[] alphabet;
	public int alphabetSize;

	public Sample s;

	private PrefixTree w;
	private int[] f;
	private int[][][] d;
	private int[][] x;

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
		ISolver solver = SolverFactory.newDefault();
		IProblem problem = solver;
		int[] model;
		boolean satis = false;
		do{
			n++;
			System.out.println("n = "+n);
			solver = SolverFactory.newDefault();
			if(!constructSAT(solver, s, n)){
				continue;
			}
			solver.setTimeout(3600);
			problem = solver;
			try{
				satis = problem.isSatisfiable();
			}catch(TimeoutException te){
				System.out.println("ERROR Timeout");
			}
		}while(!satis);


		model = problem.model();
		return translateModel(model);
	}

	public boolean constructSAT(ISolver solver, Sample s, int n){

		// Number to be mapped to each variable, to retrieve from model.
		int varID=1;

		// Prefix tree for x variables
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
		f = new int[n];
		d = new int [n][alphabetSize][n];
		x = new int[w.size][n];

		// Compute max var for solver
		final int MAXVAR = n+(n*alphabetSize*n)+(w.size*n);

		// Init solver
		solver.newVar(MAXVAR);

		// Init f variables
		for(int q=0; q<n; q++){
			f[q]=varID++;
		}

		// Init d variables
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){
				for(int q=0; q<n; q++){
					d[p][a][q]=varID++;
				}
			}
		}

		// Init x variables
		for(int u=0; u<w.size; u++){
			for(int q=0; q<n; q++){
				x[u][q]=varID++;
			}
		}

		// Setting constraints on f

		// at least one f
		try{
			solver.addClause(new VecInt(f));
		}catch(ContradictionException e){
			System.out.println("ERROR on constraint f at least one");
			return false;
		}

		// Setting constraints on d
		for(int p=0; p<n; p++){
			for(int a=0; a<alphabetSize; a++){

				// Total
				try{
					solver.addClause(new VecInt(d[p][a]));
				}catch(ContradictionException e){
					System.out.println("ERROR on constraint d total");
					return false;
				}
				

				// Deterministic
				for(int q1=0; q1<n; q1++){
					for(int q2=q1+1; q2<n; q2++){
						try{
							solver.addClause(new VecInt(new int[] {-d[p][a][q1], -d[p][a][q2]}));
						}catch(ContradictionException e){
							System.out.println("ERROR on constraint d deterministic");
							return false;
						}
					}
				}
			}
		}

		// Setting constraints on x and f

		// initial state
		try{
			solver.addClause(new VecInt(new int[] {x[w.initial.id][0]}));
		}catch(ContradictionException e){
			System.out.println("ERROR on constraint x initial");
			return false;
		}

		ArrayList<Node> prefixes = new ArrayList();
		prefixes.add(w.initial);

		while(!prefixes.isEmpty()){

			Node prefix = prefixes.get(0);
			prefixes.remove(0);

			// determinism-ish 
			for(int q1=0; q1<n; q1++){
				for(int q2=q1+1; q2<n; q2++){
					try{
						solver.addClause(new VecInt(new int[] {-x[prefix.id][q1], -x[prefix.id][q2]}));
					}catch(ContradictionException e){
						System.out.println("ERROR on constraint x deterministic-ish");
						return false;
					}
				}
			}

			// transitions
			for(int a=0; a<alphabetSize; a++){

				if(prefix.children[a]!=null){

					for(int p=0; p<n; p++){
						for(int q=0; q<n; q++){
							try{
								solver.addClause(new VecInt(new int[] {-x[prefix.id][p], -d[p][a][q], x[prefix.children[a].id][q]}));
							}catch(ContradictionException e){
								System.out.println("ERROR on constraint x transitions");
								return false;
							}
						}
					}

					// Since we're at it, add children node to prefixes list
					prefixes.add(prefix.children[a]);
				}
			}

			// accept or reject
			if(prefix.accept>0){
				for(int q=0; q<n; q++){
					try{
						solver.addClause(new VecInt(new int[] {-x[prefix.id][q], f[q]}));
					}catch(ContradictionException e){
						System.out.println("ERROR on constraint f accepted");
						return false;
					}
				}
			}
			else if(prefix.accept<0){
				for(int q=0; q<n; q++){
					try{
						solver.addClause(new VecInt(new int[] {-x[prefix.id][q], -f[q]}));
					}catch(ContradictionException e){
						System.out.println("ERROR on constraint f rejected");
						return false;
					}
				}
			}


		}

		// deterministic-ish
		return true;

	}

	private Automaton translateModel(int[] model){

		System.out.println("  == MODEL ==");
		System.out.println(Arrays.toString(model));
		System.out.println();

		Map<Integer, State> states = new HashMap();

		for(int i=0; i<n; i++){
			State tmp = new State();
			if(model[f[i]-1]>0){
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
					if(model[d[p][a][q]-1]>0){
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