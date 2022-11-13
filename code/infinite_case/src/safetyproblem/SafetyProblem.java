package safetyproblem;

import java.util.*;

import transducer.*;

/**
* This class represents the teacher for safety games solver. 
* Since the teacher knows the safety game, this class also represents a safety game.
*
* @author HUYLENBROECK Florent
*/
public class SafetyProblem{

	/**
	* Automatas representing, repsectively,
	* aV0 : The set of vertice of player 0.
	* aV1 : The set of vertice of player 1.
	* aI  : The set of initial vertices.
	* aF  : The set of safe vertices.
	*/
	private Automaton aV0, aV1, aI, aF;

	/**
	* Transducer representing the transition relation.
	*/
	private TransducerAutomaton tE;

	/**
	* The alphabet used in this safety game.
	*/
	private char[] alphabet;

	/**
	* Generates a linear safety game with hyperparameter k.
	* Linear safety problem is an infinite grid, bounded on the left, where player vertices alternate.
	* Parameter k defines the set F. After k-th vertice of each player, F begins. Game starts on a vertice >k.
	* Player 0 is called the system (s) and player 1 is called the environment (e)
	*
	* (e,O)<->(s,0)<->(e,1)<->(s,1)<-> ... <->(e,k)<->(s,k)<->(e,k+1)<-> ...
	*
	* @param k int used to parameterize safe and initial vertices.
	* @return SafetyProblem linear safety game of parameter k.
	*/
	public static SafetyProblem linearGame(int k){

		char[] alphabet = {'e','s','l'};
		RegExp v0 = new RegExp("sl*");
		RegExp v1 = new RegExp("el*");
		RegExp i = new RegExp("sl{"+(k-1)+"}l+");
		RegExp f = new RegExp("(s|e)l{"+(k-1)+"}l+");

		HashMap<String, String> map = new HashMap();
		map.put("a","se");
		map.put("b","es");
		map.put("c","ll");
		map.put("d","-l");
		map.put("e","l-");

		RegExp e = new RegExp("(ac*d?)|(bc*e?)");
		return new SafetyProblem(alphabet, v0.toAutomaton(), v1.toAutomaton(), i.toAutomaton(), f.toAutomaton(), e.toTransducer(map));
	}

	/**
	* Generates a box safety game. 
	* A box safetygame is layed on an infinite grid.
	* The token is initially placed on a horizontal line. The goal for the system is to stay within 1 cell of that horizontal line.
	*
	* Enconding is as follows : a word starting with a means it's the system's turn. b means it's the environment turn.
	* The the trail of a's indicate the vertical offset from the starting position.
	* The next b is a separator character.
	* Then the following trail of a's indicate the horizontal offset.
	*
	* @return SafetyGame boxgame.
	*/
	public static SafetyProblem boxGame(){
		char[] alphabet = {'a','b'};
		RegExp v0 = new RegExp("aa*ba*");
		RegExp v1 = new RegExp("ba*ba*");
		RegExp i = new RegExp("baba*");
		RegExp f = new RegExp("(a|b)a{0,2}ba*");

		HashMap<String, String> map = new HashMap();
		map.put("a","aa");
		map.put("b","ba");
		map.put("c","-a");
		map.put("d","ab");
		map.put("e","bb");
		map.put("f","-b");
		map.put("g","a-");
		map.put("h","b-");
		map.put("i","--");

		RegExp e = new RegExp("(d((a*b(f|(da*c))))|(a*c(h|(ba*g))))|(ba*ea*(c|g))");
		return new SafetyProblem(alphabet, v0.toAutomaton(), v1.toAutomaton(), i.toAutomaton(), f.toAutomaton(), e.toTransducer(map));
	}

	/**
	* Constructor for safety games.
	*
	* @param alphabet char[] were each entry is a letter used in the represented safety game.
	* @param aV0 Automaton describing the set of vertices for player 0.
	* @param aV1 Automaton describing the set of vertices for player 1.
	* @param aI Automaton decribing the set of initial vertices.
	* @param aF Automaton describing the set of safe vertices.
	* @param tE TransducerAutomaton describing the transition relation.
	*/
	public SafetyProblem(char[] alphabet, Automaton aV0, Automaton aV1, Automaton aI, Automaton aF, TransducerAutomaton tE){
		this.alphabet=alphabet;
		this.aV0=aV0;
		this.aV1=aV1;
		this.aI=aI;
		this.aF=aF;
		this.tE=tE;

		aV0.reduce();
		aV0.minimize();

		aV1.reduce();
		aV1.minimize();

		aI.reduce();
		aI.minimize();

		aF.reduce();
		aF.minimize();
	}

	/**
	* Returns automata describing the set of states of player 0.
	*
	* @return Automaton describing the set of states of player 0.
	*/
	public Automaton getAV0(){
		return aV0;
	}

		/**
	* Returns automata describing the set of states of player 1.
	*
	* @return Automaton describing the set of states of player 1.
	*/
	public Automaton getAV1(){
		return aV1;
	}

	/**
	* Returns automata describing the set of initial states.
	*
	* @return Automaton describing the set of initial states.
	*/
	public Automaton getAI(){
		return aI;
	}

	/**
	* Returns automata describing the set of safe states.
	*
	* @return Automaton describing the set of safe states.
	*/
	public Automaton getAF(){
		return aF;
	}

	/**
	* Returns transducer describing the transition relation.
	*
	* @return TransducerAutomaton describing the transition relation.
	*/
	public TransducerAutomaton getTE(){
		return tE;
	}

	/**
	* Returns the alphabet used by the automatas.
	*
	* @return char[] alphabet used by the automatas.
	*/
	public char[] getAlphabet(){
		return alphabet;
	}
}

