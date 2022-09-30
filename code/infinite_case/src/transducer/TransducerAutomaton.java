package transducer;

import java.util.*;

public class TransducerAutomaton extends Automaton{

	TransducerPair singleton;


	public TransducerAutomaton(Automaton a, HashMap<String, String> map){
		TransducerState ret = new TransducerState();

		a.expandSingleton();

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
				this.setInitialState(ts);
				id_map.put(sid, ts);

				
			}
			else {
				if (id_map.containsKey(s.getId())){
					ts = id_map.get(sid);
				}
				else {
					ts = new TransducerState();
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
}