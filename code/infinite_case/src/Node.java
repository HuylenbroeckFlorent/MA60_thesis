import java.util.*;

/**
* Class representing a node in the prefix tree.
*
* @author HUYLENBROECK Florent
*/
public class Node{

	/**
	* ID of the node.
	*/
	int id;

	/**
	* Prefix of the word of length |word|+1
	*/
	Node[] children;

	/**
	* Acceptance of the node's prefix during the learning.
	*/
	int accept;

	/**
	* Full prefix represented by this node in the prefix tree.
	*/
	String word;

	/**
	* Contructor.
	*
	* @param alphabetSize int. The number of characters to be used in the prefix tree.
	* @param id int. Unique ID of the node in the tree.
	* @param accept int. 	0 means that it is not specified if the word should be accepted or not during the learning.
	*						A negative value means the word has to be rejected.
	* 						A positive value means the word has to be accepted.
	* @param word String representing the prefix represented by this node in the tree.
	*/
	public Node(int alphabetSize, int id, int accept, String word){
		children = new Node[alphabetSize];
		this.id=id;
		this.word=word;
		setAccept(accept);
	}

	/**
	* Set the acceptance behaviour for this node in the tree. Once set to a value other than 0, it can't be changed again.
	* 
	* @param accept int. 	0 means that it is not specified if the word should be accepted or not during the learning.
	*						A negative value means the word has to be rejected.
	* 						A positive value means the word has to be accepted.
	*
	*/
	public void setAccept(int accept){
		if(this.accept==0){
			if(accept>0){
				this.accept=1;
			}
			else if(accept<0){
				this.accept=-1;
			}
		}
	}
}