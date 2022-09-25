import java.util.*;

import dk.brics.automaton.*;

public class TransducerAutomaton extends Automaton{

	TransducerPair singleton;

	public TransducerAutomaton(){
		initial = new TransducerState();
		deterministic = true;
		singleton = null;
	}

	public TransducerAutomaton(Automaton a, Hashmap map){
		ret = new TransducerAutomaton();

		a.expandSingleton();

		Set<State> created = new HashSet<State>();
		LinkedList<State> worklist = new LinkedList<State>();

		worklist.add(a.getInitialState());
		created.add(a.getInitialState());

		while (worklist.size() > 0) {
			State s = worklist.removeFirst();
			// Create transducter state
			Collection<Transition> tr = s.transitions;
			for (Transition t : tr)
				if (!created.contains(t.to)) {
					created.add(t.to);
					worklist.add(t.to);
				}
		}	
	}

	public TransducerPair getSingleton() {
		return singleton;
	}
}