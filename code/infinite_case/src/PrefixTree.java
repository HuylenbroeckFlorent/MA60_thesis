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
		initial = new Node(alphabetSize, size++, 0, "-");
	}

	/**
	* Adds word if it doesn't exist already
	*/
	public Node addWord(String s,  int accept){

		Node current=initial;

		if(s.length()>0){
			for(int i=0; i<s.length(); i++){
				int c = map.get(s.charAt(i));

				Node next = current.children[c];
				if (next!=null){
					current = next;
				}
				else{
					next = new Node(alphabetSize, size++, 0, s.substring(0,i+1));
					current.children[c] = next;
					current.childrenCount++;
					current = next;
				}
			}
		}
		if(accept!=0 && current.accept==0){
			current.setAccept(accept);
		}
		return current;
	}

	public Node getNode(String s){
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

	public String getWord(int id){
		ArrayList<Node> nodes = new ArrayList();
		Node current = initial;
		while(current.id != id){
			for(Node node : current.children){
				if(node!=null)
					nodes.add(node);
			}
			if(!nodes.isEmpty()){
				current = nodes.get(0);
				nodes.remove(0);
			}
		}
		return current.word;
	}
}