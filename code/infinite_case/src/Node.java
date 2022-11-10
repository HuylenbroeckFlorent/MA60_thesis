import java.util.*;

public class Node{

	int id;
	Node[] children;
	int accept;

	int childrenCount;

	String word;

	public Node(int alphabetSize, int id, int accept, String word){
		children = new Node[alphabetSize];
		childrenCount = 0;
		this.id=id;
		this.word=word;
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

	public String toString(){
		return "NODE ("+id+")";
	}
}