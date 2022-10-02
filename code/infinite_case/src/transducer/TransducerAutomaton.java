package transducer;

import java.util.*;

public class TransducerAutomaton extends Automaton{

	TransducerState initial;

	public TransducerAutomaton(){
		initial = new TransducerState();
	}

	public TransducerAutomaton(Automaton a, HashMap<String, String> map){
		TransducerState ret = new TransducerState();

		Set<State> created = new HashSet<State>();
		LinkedList<State> worklist = new LinkedList<State>();

		worklist.add(a.getInitialState());
		created.add(a.getInitialState());

		Map<Integer, TransducerState> id_map = new HashMap();

		while (worklist.size() > 0) {

			// Create transducter state
			State s = worklist.removeFirst();
			int sid = s.getId();
			TransducerState ts;

			if (s.equals(a.getInitialState())){
				ts = new TransducerState();
				if (s.isAccept()){
					ts.setAccept(true);
				}
				this.setInitialState(ts);
				id_map.put(sid, ts);
			}
			else {
				if (id_map.containsKey(s.getId())){
					ts = id_map.get(sid);
				}
				else {
					ts = new TransducerState();
					if (s.isAccept()){
						ts.setAccept(true);
					}
					id_map.put(sid, ts);
				}
			}

			// Create transitions
			Collection<Transition> tr = s.transitions;
			for (Transition t : tr){
				String c = ""+t.getMin();
				TransducerPair p = new TransducerPair(map.get(c));
				TransducerState tto;
				int tid = t.to.getId();
				if (id_map.containsKey(tid)){
					tto = id_map.get(tid);
				}
				else{
					tto = new TransducerState();
					if (t.to.isAccept()){
						tto.setAccept(true);
					}
					id_map.put(tid, tto);
				}
				TransducerTransition tt = new TransducerTransition(p, tto);
				ts.addTransition(tt);
				if (!created.contains(t.to)) {
					created.add(t.to);
					worklist.add(t.to);
				}
			}
		}	
	}

	public TransducerState getInitialState(){
		return this.initial;
	}

	public void setInitialState(TransducerState ts){
		this.initial = ts;
	}

	/**
	 * Returns a string representation of this automaton.
	 */
	@Override
	public String toString() {
		String b = "";

		Set<TransducerState> states = getTransducerStates();
		// setStateNumbers(states);
		b += "initial state: " + initial.getId()+"\n";
		for (TransducerState s : states)
			b += s.toString();

		return b;
	}

	/** 
	 * Returns the set of states that are reachable from the initial state.
	 * @return set of {@link State} objects
	 */
	public Set<TransducerState> getTransducerStates() {

		Set<TransducerState> visited = new HashSet<TransducerState>();
		LinkedList<TransducerState> worklist = new LinkedList<TransducerState>();

		worklist.add(this.initial);
		visited.add(this.initial);

		while (worklist.size() > 0) {
			TransducerState s = worklist.removeFirst();
			Collection<TransducerTransition> tr = s.transitions;
			for (TransducerTransition t : tr)
				if (!visited.contains(t.to)) {
					visited.add(t.to);
					worklist.add(t.to);
				}
		}
		return visited;
	}
}