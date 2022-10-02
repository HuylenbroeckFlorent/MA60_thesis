import java.util.*;

import transducer.*;

public class Solver{
	public static void main(String[] args){
		RegExp v0 = new RegExp("sl*");
		RegExp v1 = new RegExp("el*");
		RegExp i = new RegExp("(s|e)l{2}l*");
		RegExp f = new RegExp("sl{2}l*");

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("a","se");
		map.put("b","es");
		map.put("c","ll");
		map.put("d","0l");
		map.put("e","l0");

		RegExp e = new RegExp("(ac*d?)|(bc*e?)");

		TransducerAutomaton te = e.toTransducer(map);

		// System.out.println(te);
		// System.out.println(TransducerOperations.run(te, "slll", "elll"));

		// TransducerAutomaton cte = TransducerOperations.invert(te);

		// System.out.println(cte);
		// System.out.println(TransducerOperations.run(te, "slll", "elll"));

		SafetyProblem sp = new SafetyProblem(v0.toAutomaton(), v1.toAutomaton(), i.toAutomaton(), f.toAutomaton(), te);
	}
}