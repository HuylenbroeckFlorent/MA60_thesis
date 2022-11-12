package learner;

import java.util.*;

import transducer.*;

/**
* Prefix tree used to store the words from the sample.
* Each node is defined by a unique ID. Different characters are number from 0 to alphabetSize-1.
*
* @author HUYLENBROECK Florent
*/
public class PrefixTree{

	/**
	* Root of the tree.
	*/
	Node initial;

	/**
	* Size of the alphabet used by the tree.
	*/
	int alphabetSize;

	/**
	* Number of nodes in the tree.
	*/
	int size;

	/**
	* Each character of the original alphabet is numbered from 0 to alphabetSize-1. 
	* This map keeps track of the relation between a character and its corresponding numbering.
	*/
	Map<Character, Integer> map;

	/**
	* Constructor. Initializes root and stores the map to covert the words.
	*
	* @param alphabetSize int. The size of the alphabet.
	* @param map Map<Character, Integer> to convert the words.
	*/
	public PrefixTree(int alphabetSize, Map<Character, Integer> map){
		size = 0;
		this.alphabetSize=alphabetSize;
		this.map=map;
		initial = new Node(alphabetSize, size++, 0, "-");
	}

	/**
	* Adds a word to the tree if it is not yet stored in it. Returns the node that contains the word afterward.
	* The tree also holds the information whether if a prefix should be accepted, rejected, or if it is undecided for the learning process.
	* Upon being added, a prefix can only override the acceptance behaviour if it was previously undecided.
	* Since the learning process is guarnateed to not lead to contradiction, there sould never be a prefix trying to override an acceptance value that's not 0.
	*
	* @param s String representing the word.
	* @param accept int. 	0 means that it is not specified if the word should be accepted or not during the learning.
	*						A negative value means the word has to be rejected.
	* 						A positive value means the word has to be accepted.
	* @return Node containing the prefix that was added to the tree.
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
					current = next;
				}
			}
		}
		current.setAccept(accept);
		return current;
	}

	/**
	* Returns the node corresponding to a prefix in the tree.
	*
	* @param s String representing the prefix to look for.
	* @return Node containing the prefix.
	*/
	public Node getNode(String s){
		return addWord(s, 0);
	}

	/**
	* Finds the prefix held in node of given id. Depth first search.
	*
	* @param id int. The id of the node to look for.
	* @return String representing the prefix that was found at node of given ID.
	*/
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