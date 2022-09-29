package transducer;

public class TransducerState extends State{

	public TransducerState() {
		super();
	}

	public State step(TransducerPair p) {
		for (Transition t : transitions)
			if (t.min <= c && c <= t.max)
				return t.to;
		return null;
	}

}