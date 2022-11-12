package teacher;

import safetyproblem.*;
import transducer.*;
import learner.*;

/**
* Teacher for safety games. This object can be used, paired with a learner for safety games,
* to find a winning set for a safety game.
*
* @author HUYLENBROECK Florent
*/
public class Teacher{

	/**
	* Safety problem to solve
	*/
	private SafetyProblem sp;

	/**
	* Constructor.
	*
	* @param sp SafetyProblem to solve.
	*/
	public Teacher(SafetyProblem sp){
		this.sp=sp;
	}

	/**
	 * Checks if the given Automaton represents the winning set of this safety game.
	 * Add a counter example to the sample if a check fails.
	 * The check are always performed in the same order even tough it doesn't matter.
	 *
	 * @param cdfa Automaton conjectured by the learner 
	 * @param s Sample object to store the current counter-examples.
	 * @return Boolean, true if the Automaton represents the winning set for sp. False if a counter-example was added to the sample.
	 */
	public Boolean checkIfWinningSet(Automaton cdfa, Sample s){
		
		// Initial vertices
		Automaton a1 = BasicOperations.minus(sp.getAI(), cdfa);
		if (!(BasicOperations.isEmpty(a1))){
			s.addPos(BasicOperations.getShortestExample(a1, true));
			return false;
		}

		// Safe vertices
		Automaton a2 = BasicOperations.minus(cdfa, sp.getAF());
		if (!(BasicOperations.isEmpty(a2))){
			s.addNeg(BasicOperations.getShortestExample(a2, true));
			return false;
		}

		// Existential closure
		Automaton a3a = TransducerOperations.image(TransducerOperations.invert(sp.getTE()), cdfa);
		Automaton a3b = BasicOperations.minus(sp.getAV0(), a3a);
		Automaton a3c = BasicOperations.intersection(cdfa, a3b);
		if (!(BasicOperations.isEmpty(a3c))){
			String u = BasicOperations.getShortestExample(a3c, true);
			s.addEx(u, TransducerOperations.image(sp.getTE(), new RegExp(u).toAutomaton()));
			return false;
		}

		// Universale closure
		Automaton a4a = BasicOperations.minus(BasicOperations.union(sp.getAV0(), sp.getAV1()), cdfa);
		Automaton a4b = TransducerOperations.image(TransducerOperations.invert(sp.getTE()), a4a);
		Automaton a4c = BasicOperations.intersection(BasicOperations.intersection(sp.getAV1(), cdfa), a4b);
		if (!(BasicOperations.isEmpty(a4c))){
			String u = BasicOperations.getShortestExample(a4c, true);
			s.addUni(u, TransducerOperations.image(sp.getTE(), new RegExp(u).toAutomaton()));
			return false;
		}

		// Passes
		return true;
	}
}