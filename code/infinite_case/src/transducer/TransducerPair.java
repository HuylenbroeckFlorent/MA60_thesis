package transducer;

public class TransducerPair{

	private char u;
	private char v;

	public TransducerPair(char u, char v){
		this.u=u;
		this.v=v;
	}

	public TransducerPair(String s){
		if(s.length() == 2){
			u = s.charAt(0);
			v = s.charAt(1);
		}
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
			return true;
		}

		if (o instanceof TransducerPair){
			TransducerPair p = (TransducerPair) o;
			return (p.getU() == u && p.getV() == v);
		} else {
			return false;
		}		
	}

	public int hashcode(){
		return u*2+v*3;
	}
}