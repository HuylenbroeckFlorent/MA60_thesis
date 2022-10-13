import java.util.*;

import transducer.*;

public class Main{
	public static void main(String[] args){
		int k = 2;
		RegExp v0 = new RegExp("sl*");
		RegExp v1 = new RegExp("el*");
		RegExp i = new RegExp("sl{"+(k-1)+"}l+");
		RegExp f = new RegExp("(s|e)l{"+(k-1)+"}l+");

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("a","se");
		map.put("b","es");
		map.put("c","ll");
		map.put("d","0l");
		map.put("e","l0");

		RegExp e = new RegExp("(ac*d?)|(bc*e?)");
		SafetyProblem sp = new SafetyProblem(v0.toAutomaton(), v1.toAutomaton(), i.toAutomaton(), f.toAutomaton(), e.toTransducer(map));


		// System.out.println(TransducerOperations.run(te, "slll", "elll"));

		// System.out.println(TransducerOperations.invert(sp.getTE()));
		// System.out.println(sp.getTE());

		// Automaton test = new RegExp("sl{3}").toAutomaton();
		// Automaton test = new RegExp("sl{3}e").toAutomaton();
		// System.out.println(TransducerOperations.image(sp.getTE(), test));
		// System.out.println(TransducerOperations.image(TransducerOperations.invert(sp.getTE()), test));
		
		// Sample s = new Sample();
		// Automaton w = new RegExp("(sl{"+(k-1)+"}l+)|(el{"+(k)+"}l+)").toAutomaton();
		// System.out.println(sp.checkIfWinningSet(w, s));
	}
}