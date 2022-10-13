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

	/**
	 * Returns a list of the non-epsilon transition from this state.
	 * 
	 * @return Set<TransducerState> non-epsilon transitions going ou of this state.
	 */
	public Set<TransducerTransition> getAlphaTransitions(){
		Set<TransducerTransition> ret = new HashSet();
		for (TransducerTransition t: transitions){
			if (t.getPair().getU()!='0'){
				ret.add(t);
			}
		}
		return ret;
	}

	/**
	 * Returns a list of the epsilon transition from this state.
	 * 
	 * @return Set<TransducerState> epsilon transitions going ou of this state.
	 */
	public Set<TransducerTransition> getEpsilonTransitions(){
		Set<TransducerTransition> ret = new HashSet();
		for (TransducerTransition t: transitions){
			if (t.getPair().getU()=='0'){
				ret.add(t);
			}
		}
		return ret;
	}

	/** 
	 * Returns string describing this state.
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