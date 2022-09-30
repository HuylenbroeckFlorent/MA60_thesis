package transducer;

import java.util.*;

public class TransducerState extends State{

	Set<TransducerTransition> transitions;

	public TransducerState() {
		super();
	}

	// public State step(TransducerPair p) {
	// 	for (Transition t : transitions)
	// 		if (t.min <= c && c <= t.max)
	// 			return t.to;
	// 	return null;
	// }

		/**
	 * Adds an outgoing transition.
	 * @param t transition
	 */
	public void addTransition(TransducerTransition t)	{
		transitions.add(t);
	}

}