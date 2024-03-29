package transducer;

import java.util.*;

/**
* Class that is used to perform multiple operations on transducer, to be used by a teacher for safety games to perform various checks.
*
* @author HUYLENBROECK Florent
*/
public class TransducerOperations{

	/**
	 * Returns true if the given convolution of two strings u and v is accepted by the transducer. 
	 *
	 * @param a TransducerAutomaton to perform the run on.
	 * @param u String. The first component of the pair of String for the run.
	 * @param v String. The second component of the pair of String for the run.
	 * @return boolean. True if the pair of string is accepted by the transducer. False otherwise.
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
					tq = ts.step(new TransducerPair(u.charAt(i), '-'));
					if(tq == null){
						tq = ts.step(new TransducerPair('-', v.charAt(j)));
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
					tq = ts.step(new TransducerPair(u.charAt(i), '-'));
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
						tq = ts.step(new TransducerPair('-', v.charAt(j)));
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

	/**
	* Perform a deepcopy of a transducer. If told to, the copy will have inverted pairs.
	*
	* @param ta TransducerAutomaton to copy.
	* @param invert boolean. True means the copy will have inverted pairs.
	* @return TransducerAutomaton copied from original transducer.
	*/
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
					p = t.getPair().inverse();
				}
				else{
					p = t.getPair().deepcopy();
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

	/**
	* Returns a transducer with inverted pairs (i.e. the input and output languages are swapped).
	*
	* @param ta TransducerAutomaton to invert.
	* @return TransducerAutomaton inverted.
	*/
	public static TransducerAutomaton invert(TransducerAutomaton ta){
		return deepcopy(ta, true);
	}

	/**
	* Returns a copy (in reality, a deepcopy) of a transducer.
	*
	* @param ta TransducerAutomaton to (deep)copy.
	* @return TransducerAutomaton. The (deep)copy of the transducer.
	*/
	public static TransducerAutomaton copy(TransducerAutomaton ta){
		return deepcopy(ta, false);
	}


	/**
	 * Computes the image of the given transducer for the langage accapted by a given NFA.
	 * Returns a NFa accepting that image.
	 * 
	 * @param ta TransducerAutomaton representing the relation.
	 * @param a Automaton to compute the image.
	 * @return Automaton accepting the computed image.
	 */
	public static Automaton image(TransducerAutomaton ta, Automaton a){
		Automaton ret = new Automaton();
		a.expandSingleton();
		State as = a.getInitialState();
		TransducerState tas = ta.getInitialState();
		imageRec(ret.getInitialState(), as, tas);
		ret.minimize();

		return ret;
	}

	/**
	* Recursion step function used by image function.
	*/
	private static void imageRec(State current, State as, TransducerState tas){

		if (as.isAccept() && tas.isAccept()){  // <----------- CHANGED THIS
			current.setAccept(true);
		}

		for (Transition ast : as.getTransitions()){
			char c = ast.getMin();
			for (TransducerTransition tast : tas.getAlphaTransitions()){

				TransducerPair p = tast.getPair();

				if(c == p.getU()){
					if(p.getV() == '-'){
						imageRec(current, ast.getDest(), tast.getDest());
					}else{
						if(ast.getDest().equals(as)){
							Transition tmp = new Transition(p.getV(), current);
							if (!(current.getTransitions().contains(tmp))){
								current.addTransition(tmp);
								imageRec(current, as, tast.getDest());
							}
						}else{
							State next = new State();
							current.addTransition(new Transition(p.getV(), next));
							imageRec(next, ast.getDest(), tast.getDest());
						}
					}
				}
			}
		}

		for (TransducerTransition tast : tas.getEpsilonTransitions()){

			TransducerPair p = tast.getPair();
			State next = new State();
			for (Transition tmp : current.getTransitions()){
				if(tmp.getMin() == p.getV())
					next = tmp.getDest();
			}
			current.addTransition(new Transition(p.getV(), next));
			imageRec(next, as, tast.getDest());
		}
	}
}
