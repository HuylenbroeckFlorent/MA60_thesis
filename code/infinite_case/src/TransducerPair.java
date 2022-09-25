public class TransducerPair{

	private char u = u;
	private char v = v;

	public TransducerPair(char u, char v){
		this.u=u;
		this.v=v;
	}

	public char getU(){
		return u;
	}

	public char getV(){
		return v;
	}

	public TransducerPair invert(){
		return new TransducerPair(v, u);
	}

	public boolean equals(Object o){
		if (o == this){
			return True;
		}

		if (o instanceof TransducerPair){
			TransducerPair p = (TransducerPair) o;
			return (p.getU() == u && p.getV() == v);
		} else {
			return False;
		}		
	}

	public int hashcode(){
		return u*2+v*3;
	}
}