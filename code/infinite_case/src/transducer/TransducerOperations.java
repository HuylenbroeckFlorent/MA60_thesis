package transducer;

import java.util.*;

public class TransducerOperations{

	/**
	 * Returns true if the given convolution of two strings u and v is accepted by the transducer. 
	 */
	public static boolean run(TransducerAutomaton a, String u, String v) {
		TransducerState ts = a.initial;
		TransducerState tq;

		int i = 0, j = 0;
		while (i<u.length() || j<v.length()){
			//
			if (i<u.length() && j<v.length()){
				tq = ts.step(new TransducerPair(u.charAt(i), v.charAt(j)));
				// No match -> try with epsilons
				if(tq == null){
					tq = ts.step(new TransducerPair(u.charAt(i), '0'));
					if(tq == null){
						tq = ts.step(new TransducerPair('0', v.charAt(j)));
						if(tq == null){
							return false;
						}
						else{
							j++;
							ts = tq;
						}
					}
					else{
						i++;
						ts = tq;
					}
				}
				else{
					i++;
					j++;
					ts = tq;
				}
			}
			else{
				if(i<u.length()){
					tq = ts.step(new TransducerPair(u.charAt(i), '0'));
					if(tq == null){
						return false;
					}
					else{
						i++;
						ts = tq;
					}
				}
				else{
					if(j<v.length()){
						tq = ts.step(new TransducerPair('0', v.charAt(j)));
						if(tq == null){
							return false;
						}
						else{
							j++;
							ts = tq;
						}
					}
				}
			}
					
		}
		return ts.isAccept();
	}

	private static TransducerAutomaton deepcopy(TransducerAutomaton ta, boolean invert){
		TransducerAutomaton ret = new TransducerAutomaton();

		Set<TransducerState> created = new HashSet<TransducerState>();
		LinkedList<TransducerState> worklist = new LinkedList<TransducerState>();

		worklist.add(ta.getInitialState());
		created.add(ta.getInitialState());

		Map<Integer, TransducerState> id_map = new HashMap();

		while (worklist.size() > 0) {

			// Create transducter state
			TransducerState s = worklist.removeFirst();
			int sid = s.getId();
			TransducerState ts;

			if (s.equals(ta.getInitialState())){
				ts = new TransducerState();
				ret.setInitialState(ts);
				if (s.isAccept()){
					ts.setAccept(true);
				}
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
			Collection<TransducerTransition> tr = s.transitions;
			for (TransducerTransition t : tr){
				TransducerPair p;
				if(invert){
					p = t.getMin().inverse();
				}
				else{
					p = t.getMin().deepcopy();
				}
				TransducerState tto;
				int tid = t.to.getId();
				if (id_map.containsKey(tid)){
					tto = id_map.get(tid);
				}
				else{
					tto = new TransducerState();
					tto.setAccept(t.to.isAccept());
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
		return ret;	
	}

	public static TransducerAutomaton invert(TransducerAutomaton ta){
		return deepcopy(ta, true);
		
	}

	public static TransducerAutomaton copy(TransducerAutomaton ta){
		return deepcopy(ta, false);
		
	}
}
