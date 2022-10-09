package transducer;

public class TransducerTransition implements Comparable{
	TransducerPair p;
	TransducerState to;

	/** 
	 * Constructs a new singleton interval transition. 
	 * @param p transition Pair
	 * @param to destination state
	 */
	public TransducerTransition(TransducerPair p, TransducerState to){
		this.p = p;
		this.to = to;
	}

	/** Returns the pair of this transition. */
	public TransducerPair getPair() {
		return p;
	}

	public TransducerState getDest(){
		return to;
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
			return p.equals(p) && t.to == to;
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
		return p.hashcode(); // return min * 2 + max * 3;
	}

	/** 
	 * Returns a string describing this state. Normally invoked via 
	 * {@link Automaton#toString()}. 
	 */
	@Override
	public String toString() {
		String b = "";
		b += p.toString();
		b += " -> " + to.getId();
		return b;
	}

	@Override
	public int compareTo(Object o){
		if (o instanceof TransducerTransition){
			TransducerTransition tt = (TransducerTransition) o;
			return this.p.compareTo(tt.getPair());
		}else{
			return 0;
		}
		
	}
}