package transducer;

/**
* Transition object for TransducerAutomaton object.
*
* @author HUYLENBROECK Florent
*/
public class TransducerTransition implements Comparable{

	/**
	* The pair of character used by this transiton.
	*/
	TransducerPair p;

	/**
	* The destination of this transition.
	*/
	TransducerState to;

	/** 
	 * Constructor. 
	 *
	 * @param p TransducerPair used by this transition. 
	 * @param to TransducerState. Destination state.
	 */
	public TransducerTransition(TransducerPair p, TransducerState to){
		this.p = p;
		this.to = to;
	}


	/** 
	* Returns the pair of this transition. 
	*
	* @return TransducerPair of this transition.
	*/
	public TransducerPair getPair() {
		return p;
	}

	/**
	* Returns the destination state of this transition.
	*
	* @return TransducerState. Destination state.
	*/
	public TransducerState getDest(){
		return to;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TransducerTransition) {
			TransducerTransition t = (TransducerTransition)obj;
			return p.equals(p) && t.to == to;
		} else
			return false;
	}

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