import java.util.*;

public class Node{

	int id;
	Node[] children;
	int accept;

	int childrenCount;

	// Used to link a word to it's counter-exemple consequences$
	Set<Node> uni;
	Set<Node> ex;

	public Node(int alphabetSize, int id, int accept){
		children = new Node[alphabetSize];
		childrenCount = 0;
		this.id=id;
		setAccept(accept);
	}

	public void setAccept(int accept){
		if(accept>0){
			this.accept=1;
		}
		else if(accept<0){
			this.accept=-1;
		}
		else{
			this.accept=0;
		}
	}

	public void setUni(Set<Node> uni){
		this.uni=uni;
	}

	public void setEx(Set<Node> ex){
		this.ex=ex;
	}

	public String toString(){
		return "NODE ("+id+")";
	}
}