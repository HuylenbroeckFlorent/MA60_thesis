package transducer;

/**
* Pair object used to hold two character in a TransducerTransition object.
*
* @author HUYLENBROECK Florent
*/
public class TransducerPair implements Comparable{

	/**
	* First component of the pair.
	*/
	private char u;

	/**
	* Second component of the pair.
	*/
	private char v;

	/**
	* Constructor.
	*
	* @param u char. First component of the pair to construct.
	* @param v char. Second component of the pair to construct.
	*/
	public TransducerPair(char u, char v){
		this.u=u;
		this.v=v;
	}

	/**
	* Constructor.
	*
	* @param s String of two characters. First (resp. second) character will be the first (resp. second) component of the pair.
	*/
	public TransducerPair(String s){
		if(s.length() == 2){
			u = s.charAt(0);
			v = s.charAt(1);
		}
		else{
			System.out.println("/!\\ ERROR in TransducerPair : Transducers do not support pairs of multiple characters.");
		}
	}

	/** 
	* Returns the first component of the pair.
	*
	* @return char first component of the pair.
	*/
	public char getU(){
		return u;
	}

	/** 
	* Returns the second component of the pair.
	*
	* @return char second component of the pair.
	*/
	public char getV(){
		return v;
	}

	/**
	* Returns a new pair with swapped components.
	*
	* @return TransducerPair with swapped components.
	*/
	public TransducerPair inverse(){
		return new TransducerPair(v, u);
	}

	/**
	* Returns a deepcopy of this pair.
	*
	* @return TransducerPair with same components as this pair.
	*/
	public TransducerPair deepcopy(){
		return new TransducerPair(u, v);
	}

	@Override
	public boolean equals(Object o){
		if (o == this){
			return true;
		}

		if (o instanceof TransducerPair){
			TransducerPair p = (TransducerPair) o;
			return (p.getU() == u && p.getV() == v);
		} else {
			return false;
		}		
	}

	@Override
	public String toString(){
		return "("+u+":"+v+")";
	}

	@Override
	public int compareTo(Object o){
		if (o instanceof TransducerPair){
			TransducerPair p = (TransducerPair) o;
			if (this.u=='0' && p.getU()!='0'){
				return -1;
			}
			else if (this.u!='0' && p.getU()=='0'){
				return 1;
			}
			else{
				return 0;
			}
		}else{
			return 0;
		}
	}
}