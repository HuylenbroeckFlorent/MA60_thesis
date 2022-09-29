import dk.brics.automaton.*;

public class TransducerTransition extends Transition{
	TransducerPair min;
	TransducerPair max;

	/** 
	 * Constructs a new singleton interval transition. 
	 * @param p transition Pair
	 * @param to destination state
	 */
	public TransducerTransition(TransducerPair p, State to){
		min = max = p;
		this.to = to;
	}

	/** Returns minimum of this transition interval. */
	public TransducerPair getMin() {
		return min;
	}
	
	/** Returns maximum of this transition interval. */
	public TransducerPair getMax() {
		return max;
	}

	/** 
	 * Checks for equality.
	 * @param obj object to compare with
	 * @return true if <code>obj</code> is a transition with same 
	 *         character interval and destination state as this transition.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TransducerTransition) {
			TransducerTransition t = (TransducerTransition)obj;
			return t.min.equals(min) && t.max.equals(max) && t.to == to;
		} else
			return false;
	}

	/** 
	 * Returns hash code.
	 * The hash code is based on the character interval (not the destination state).
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return min.hashcode(); // return min * 2 + max * 3;
	}
}