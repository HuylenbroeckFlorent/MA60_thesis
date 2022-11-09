import java.util.*;

import transducer.*;

public class PrefixTree{

	Node initial;
	int alphabetSize;
	int size;
	Map<Character, Integer> map;

	public PrefixTree(int alphabetSize, Map<Character, Integer> map){
		size = 0;
		this.alphabetSize=alphabetSize;
		this.map=map;
		initial = new Node(alphabetSize, size++, 0);
	}

	/**
	* Adds word if it doesn't exist already
	*/
	public Node addWord(String s,  int accept, Automaton cu, Automaton ce){

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
				cons.add(addWord(word, 0, null, null));
			}
			current.setUni(cons);
		}
		if(ce!=null){
			Set<Node> cons = new HashSet();
			for(String word : SpecialOperations.getFiniteStrings(ce)){
				cons.add(addWord(word, 0, null, null));
			}
			current.setEx(cons);
		}
		return current;
	}

	public Node getWord(String s){
		Node current = initial;
		for(int i=0; i<s.length(); i++){
			int c = map.get(s.charAt(i));
			if(current.children[c]!=null){
				current = current.children[c];
			}
			else{
				return null;
			}
		}
		return current;
	}
}