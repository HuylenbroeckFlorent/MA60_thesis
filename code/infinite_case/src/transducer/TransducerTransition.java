package transducer;

public class TransducerTransition{
	TransducerPair min;
	TransducerPair max;

	TransducerState to;

	/** 
	 * Constructs a new singleton interval transition. 
	 * @param p transition Pair
	 * @param to destination state
	 */
	public TransducerTransition(TransducerPair p, TransducerState to){
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

	/** 
	 * Returns a string describing this state. Normally invoked via 
	 * {@link Automaton#toString()}. 
	 */
	@Override
	public String toString() {
		String b = "";
		b += min.toString();
		b += " -> " + to.getId();
		return b;
	}
}