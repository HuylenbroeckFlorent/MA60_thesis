import java.util.*;

import transducer.*;

public class PrefixTree{

	Node initial;
	int alphabetSize;
	int size;

	public PrefixTree(int alphabetSize){
		size = 0;
		this.alphabetSize=alphabetSize;
		initial = new Node(alphabetSize, size++, 0);
	}

	/**
	* Adds word if it doesn't exist already
	*/
	public Node addWord(String s, Map<Character, Integer> map,  int accept, Automaton cu, Automaton ce){

		Node current=initial;

		if(s.length()>0){
			for(int i=0; i<s.length(); i++){
				int c = map.get(s.charAt(i));

				Node next = current.children[c];
				if (next!=null){
					current = next;
				}
				else{
					next = new Node(alphabetSize, size++, 0);
					current.children[c] = next;
					current.childrenCount++;
					current = next;
				}
			}
		}
		if(accept!=0 && current.accept==0){
			current.setAccept(accept);
		}
		if(cu!=null){
			Set<Node> cons = new HashSet();
			for(String word : SpecialOperations.getFiniteStrings(cu)){
				cons.add(this.addWord(word, map, 0, null, null));
			}
			current.setUni(cons);
		}
		if(ce!=null){
			Set<Node> cons = new HashSet();
			for(String word : SpecialOperations.getFiniteStrings(ce)){
				cons.add(this.addWord(word, map, 0, null, null));
			}
			current.setEx(cons);
		}
		return current;
	}

	public Node getWord(String s){
		Node current = initial;
		for(int i=0; i<s.length(); i++){
			if(current.children[s.charAt(i)]!=null){
				current = current.children[s.charAt(i)];
			}
			else{
				return null;
			}
		}
		return current;
	}
}