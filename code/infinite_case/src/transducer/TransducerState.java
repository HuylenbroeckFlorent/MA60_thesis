package transducer;

import java.util.*;

/**
* State object for a TransducerAutomaton object.
*
* @author HUYLENBROECK Florent
*/
public class TransducerState extends State{

	/**
	* The set of outgoing transitions.
	*/
	public Set<TransducerTransition> transitions = new HashSet();

	/**
	* Acceptance behaviour for this state.
	*/
	private boolean accept;

	/**
	* Default constructor.
	*/
	public TransducerState() {
		super();
		this.accept = false;
	}

	/**
	* Function used to perform a run on the transducer. Checks the transitions of this state for a given pair of character.
	* If no such transition exists, returns null.
	*
	* @param p TransducerPair tocheck for in each transition. 
	* @return TransducerState reached by the transition that holds the pair of character. Returns null if none is found.
	*/
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

	/**
	* Sets the acceptance behaviour for this state.
	*
	* @param b boolean. True means the state should be an accepting state. False means it shouldn't.
	*/
	public void setAccept(boolean b){
		this.accept = b;
	}

	/**
	* Returns the acceptance behaviour of this state.
	*
	* @return boolean. True means the state is accepting. False means it is not.
	*/
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
			if (t.getPair().getU()!='-'){
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
			if (t.getPair().getU()=='-'){
				ret.add(t);
			}
		}
		return ret;
	}

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