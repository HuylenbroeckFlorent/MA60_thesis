package transducer;

public class TransducerPair implements Comparable{

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

	public TransducerPair inverse(){
		return new TransducerPair(v, u);
	}

	public TransducerPair deepcopy(){
		return new TransducerPair(u, v);
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

	public String toString(){
		return "("+u+":"+v+")";
	}

	public int hashcode(){
		return u*2+v*3;
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