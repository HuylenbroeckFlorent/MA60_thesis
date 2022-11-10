package transducer;

import java.util.*;

/**
* Transducer object. Acts like an Automaton from dk.brics.automaton package except it runs pair of words.
* This does not support Automaton operations except the ones overriden in TransducerOperations class.
*
* @author HUYLENBROECK Florent.
*/
public class TransducerAutomaton extends Automaton{

	/**
	* Initial state.
	*/
	TransducerState initial;

	/*
	* Default constructor.
	*/
	public TransducerAutomaton(){
		initial = new TransducerState();
	}

	/**
	* Constructor. Uses an existing Automaton and a map to parse single characters into pairs of characters.
	* For epsilons, a special character '-' is used.
	*
	* @param a Automaton whose states use singles characters.
	* @param map HashMap<String, String>. 	Keys of the map are the single characters used in the Automaton.
	*										Values are Strings that can be parsed to obtain two characters that will consititute the pairs for the transitions of this Transducer.
	*/
	public TransducerAutomaton(Automaton a, HashMap<String, String> map){
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

	/**
	* Returns the initial state of this transducer.
	*
	* @return TransducerState initial state of this transducer.
	*/
	public TransducerState getInitialState(){
		return this.initial;
	}

	/**
	* Sets the initial state of this transducer.
	*
	* @param ts TransducerState state to set as initial state of this transducer.
	*/
	public void setInitialState(TransducerState ts){
		this.initial = ts;
	}

	/**
	* Returns all the states of this transducer.
	*
	* @return Set<TransducerState>, the set of states of this transducer.
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

	/**
	* Returns the transitions of this transducer.
	*
	* @return Set<TransducerTransition>, the set of transitions of this transducer.
	*/
	public Set<TransducerTransition> getTransducerTransitions(){
		Set<TransducerState> states = getTransducerStates();
		Set<TransducerTransition> ret = new HashSet();

		for (TransducerState t : states)
			ret.addAll(t.transitions);
		return ret;
	}

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
}