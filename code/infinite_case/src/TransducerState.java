import dk.brics.automaton.*;

public class TransducerState extends State{

	TransducerTransisiton transitions;

	public TransducerState() {
		super.State()
	}

	public State step(TransducerPair p) {
		for (Transition t : transitions)
			if (t.min <= c && c <= t.max)
				return t.to;
		return null;
	}

}