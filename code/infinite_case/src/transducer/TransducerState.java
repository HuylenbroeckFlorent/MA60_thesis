package transducer;

import java.util.*;

public class TransducerState extends State{

	public Set<TransducerTransition> transitions = new HashSet();
	private boolean accept;

	public TransducerState() {
		super();
		this.accept = false;
	}

	public TransducerState step(TransducerPair p) {
		for (TransducerTransition t : transitions)
			if (t.getPair().equals(p) || t.getPair().equals(p))
				return t.to;
		return null;
	}

		/**
	 * Adds an outgoing transition.
	 * @param t transition
	 */
	public void addTransition(TransducerTransition t)	{
		this.transitions.add(t);
	}

	public void setAccept(boolean b){
		this.accept = b;
	}

	public boolean isAccept(){
		return accept;
	}

	public List<TransducerTransition> getSortedTransitions(){
		ArrayList<TransducerTransition> ret = new ArrayList(transitions);
		Collections.sort(ret, Collections.reverseOrder());
		return ret;
	}

	/** 
	 * Returns string describing this state. Normally invoked via 
	 * {@link Automaton#toString()}. 
	 */
	@Override
	public String toString() {
		String b = "";
		b += "state " + this.getId();
		if (this.isAccept())
			b += " [accept]";
		else
			b += " [reject]";
		b+= "\n";
		for (TransducerTransition t : transitions)
			b+="  "+t.toString()+"\n";
		return b;
	}
}