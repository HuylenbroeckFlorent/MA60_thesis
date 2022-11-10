import java.util.*;

import transducer.*;

/**
* Main class used to solve safety problems. 
* Use main function to generate/create a safety game then call solve() function to find a winning set.
*
* @author HUYLENBROECK Floretn
*/
public class Main{

	public static void main(String[] args){

		// generate safety game
		SafetyProblem linear3 = SafetyProblem.linearGame(3);

		// solve safety game
		Automaton w0 = solve(linear3, false, false);

		// display solution (use toDot() or toString())
		System.out.println(w0.toString());
	}

	/**
	* Wrapper function for solve using less parameters.
	*
	* @param sp SafetyProblem to solve.
	* @return Automata describing the winning set for the safety game player 0.
	*/
	public static Automaton solve(SafetyProblem sp){
		return solve(sp, false, false);
	}

	/**
	* Solves a safety problem.
	* Tries to learn a winning set by learning DFAs. See class SafetyProblem and Learner.
	* This function is not guaranteed to halt.
	*
	* @param sp SafetyProblem to solve.
	* @param verbose boolean. If true, more informations will be displayed while solving.
	* @param debug boolean. if true, debug informations will be displayed. Automatas will not be minimized.
	* @return Automata describing the winning set for the safety game player 0.
	*/
	public static Automaton solve(SafetyProblem sp, boolean verbose, boolean debug){
		Learner learner = new Learner(sp.getAlphabet(), verbose, debug);
		Automaton w;
		boolean isWinningSet = false;
		int loop=0;
		do{
			w = learner.conjecture();
			isWinningSet = sp.checkIfWinningSet(w, learner.s);

			if(verbose){
				System.out.println("==========");
				System.out.println("This iteration ("+(++loop)+"began with the following sample : ");
				System.out.println(learner.s);
				
				System.out.println("The following DFA was conjectured : ");
				System.out.println(w);
			}
		}while(!isWinningSet);

		if(verbose){
			System.out.println("=== Winning set found :");
			System.out.println(w);
		}
		
		return w;
	}
}