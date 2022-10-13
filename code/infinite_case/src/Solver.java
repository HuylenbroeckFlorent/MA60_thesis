import java.util.*;

import transducer.*;

public class Solver{
	public static void main(String[] args){
		int k = 2;
		RegExp v0 = new RegExp("sl*");
		RegExp v1 = new RegExp("el*");
		RegExp i = new RegExp("sl{"+(k-1)+"}l*");
		RegExp f = new RegExp("(s|e)l{"+(k-1)+"}l*");

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("a","se");
		map.put("b","es");
		map.put("c","ll");
		map.put("d","0l");
		map.put("e","l0");

		RegExp e = new RegExp("(ac*d?)|(bc*e?)");

		TransducerAutomaton te = e.toTransducer(map);

		// System.out.println(TransducerOperations.run(te, "slll", "elll"));

		// TransducerAutomaton cte = TransducerOperations.invert(te);

		// System.out.println(cte);
		// System.out.println(TransducerOperations.run(te, "slll", "elll"));

		SafetyProblem sp = new SafetyProblem(v0.toAutomaton(), v1.toAutomaton(), i.toAutomaton(), f.toAutomaton(), te);

		// System.out.println(TransducerOperations.invert(sp.getTE()));
		// System.out.println(sp.getTE());
		// Automaton test = sp.getAV0();
		// Automaton test = sp.getAV1();
		//Automaton test = new RegExp("sl{3}").toAutomaton();
		//Automaton test = new RegExp("sl{3}e").toAutomaton();


		// System.out.println(test);

		//System.out.println(TransducerOperations.image(sp.getTE(), test));
		//System.out.println(TransducerOperations.image(TransducerOperations.invert(sp.getTE()), test));

		Sample s = new Sample();

		System.out.println(check(sp, new RegExp("(e|s)ll*").toAutomaton(), s));
	}

	/**
	 * Checks if the given Automaton represents the winning set of given safety problem 
	 *
	 * @param sp SafetyProblem object to symbolizes the rational safety problem
	 * @param cdfa Automaton conjectured by the learner 
	 * @return Boolean, true if the Automaton represents the winning set for sp.
	 */
	private static Boolean check(SafetyProblem sp, Automaton cdfa, Sample s){
		// Initial vertices
		Automaton a1 = BasicOperations.minus(sp.getAI(), cdfa);
		if (!(BasicOperations.isEmpty(a1))){
			s.addPos(BasicOperations.getShortestExample(a1, true));
			System.out.println("Initial vertices failed, returned the following NFA :");
			System.out.println(a1);
			return false;
		}

		// Safe vertices
		Automaton a2 = BasicOperations.minus(cdfa, sp.getAF());
		if (!(BasicOperations.isEmpty(a2))){
			s.addNeg(BasicOperations.getShortestExample(a2, true));
			System.out.println("Safe vertices failed, returned the following NFA :");
			System.out.println(a2);
			return false;
		}

		// Existential closure
		Automaton a3a = TransducerOperations.image(TransducerOperations.invert(sp.getTE()), cdfa);
		Automaton a3b = BasicOperations.minus(sp.getAV0(), a3a);
		Automaton a3c = BasicOperations.intersection(cdfa, a3b);
		if (!(BasicOperations.isEmpty(a3c))){
			String u = BasicOperations.getShortestExample(a3c, true);
			s.addEx(u, TransducerOperations.image(sp.getTE(), new RegExp(u).toAutomaton()));
			System.out.println("Existential closure failed, returned the following NFA :");
			return false;
		}

		// Universale closure
		Automaton a4a = BasicOperations.minus(BasicOperations.union(sp.getAV0(), sp.getAV1()), cdfa);
		Automaton a4b = TransducerOperations.image(TransducerOperations.invert(sp.getTE()), a4a);
		Automaton a4c = BasicOperations.intersection(BasicOperations.intersection(sp.getAV1(), cdfa), a4b);
		if (!(BasicOperations.isEmpty(a4c))){
			String u = BasicOperations.getShortestExample(a4c, true);
			s.addUni(u, TransducerOperations.image(sp.getTE(), new RegExp(u).toAutomaton()));
			System.out.println("Universal closure failed, returned the following NFA :");
			return false;
		}

		// Passes
		return true;
	}

}