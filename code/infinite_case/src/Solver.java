import java.util.*;

import dk.brics.automaton.*;

public class Solver{
	public static void main(String[] args){
		RegExp v0 = new RegExp("sl*");
		RegExp v1 = new RegExp("el*");
		RegExp i = new RegExp("(s|e)l{2}l*");
		RegExp f = new RegExp("sl{2}l*");

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("se","a");
		map.put("es","b");
		map.put("ll","c");
		map.put("0l","d");
		map.put("l0","e");

		RegExp e = new RegExp("(ac*d?)|(bc*e?)");

		Transducer te = new Transducer(e.toAutomaton(), map);

		SafetyProblem sp = new SafetyProblem(v0.toAutomaton(), v1.toAutomaton(), i.toAutomaton(), f.toAutomaton(), te);
	}
}