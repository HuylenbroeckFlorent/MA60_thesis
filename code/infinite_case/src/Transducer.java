import java.util.*;

import dk.brics.automaton.*;

public class Transducer{

	private Automaton tE;
	private HashMap map;

	public Transducer(Automaton tE, HashMap<String, String> map){
		this.tE=tE;
		this.map=map;
	}

	public boolean run(Pair p){
		String s = translatePair(p);
		return tE.run(s);
	}

	private String translatePair(Pair p){
		String a = p.a;
		String b = p.b;

		int length_a = a.length();
		int length_b = b.length();

		String ret = "";

		boolean done = false;
		int i=0, j=0;
		
		while(!done){ 
			char char_a = '\u0000';
			char char_b = '\u0000';

			if(i<length_a)
				char_a = a.charAt(i);

			if(j<length_b)
				char_b = b.charAt(j);

			if(char_a=='\u0000' && char_b=='\u0000')
				break;
			
			if(char_a!='\u0000' && char_b!='\u0000' && map.containsKey(""+char_a+char_b)){
				ret+=map.get(""+char_a+char_b);
				i++;
				j++;
			}
			else if(char_a!='\u0000' && map.containsKey(""+char_a+'0')){
				ret+=map.get(""+char_a+'0');
				i++;
			}
			else if(char_b!='\u0000' && map.containsKey(""+'0'+char_b)){
				ret+=map.get(""+'0'+char_b);
				j++;
			}
			System.out.println(ret);
		}


		return ret;
	}
}