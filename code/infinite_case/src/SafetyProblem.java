import transducer.*;

public class SafetyProblem{

	private Automaton aV0, aV1, aI, aF;
	private TransducerAutomaton tE;

	public SafetyProblem(Automaton aV0, Automaton aV1, Automaton aI, Automaton aF, TransducerAutomaton tE){
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

	public Automaton getAV0(){
		return aV0;
	}

	public Automaton getAV1(){
		return aV1;
	}

	public Automaton getAI(){
		return aI;
	}

	public Automaton getAF(){
		return aF;
	}

	public TransducerAutomaton getTE(){
		return tE;
	}

	/**
	 * Checks if the given Automaton represents the winning set of given safety problem 
	 *
	 * @param sp SafetyProblem object to symbolizes the rational safety problem
	 * @param cdfa Automaton conjectured by the learner 
	 * @return Boolean, true if the Automaton represents the winning set for sp.
	 */
	public Boolean checkIfWinningSet(Automaton cdfa, Sample s){
		// Initial vertices
		Automaton a1 = BasicOperations.minus(aI, cdfa);
		if (!(BasicOperations.isEmpty(a1))){
			s.addPos(BasicOperations.getShortestExample(a1, true));
			return false;
		}

		// Safe vertices
		Automaton a2 = BasicOperations.minus(cdfa, aF);
		if (!(BasicOperations.isEmpty(a2))){
			s.addNeg(BasicOperations.getShortestExample(a2, true));
			return false;
		}

		// Existential closure
		Automaton a3a = TransducerOperations.image(TransducerOperations.invert(tE), cdfa);
		Automaton a3b = BasicOperations.minus(aV0, a3a);
		Automaton a3c = BasicOperations.intersection(cdfa, a3b);
		if (!(BasicOperations.isEmpty(a3c))){
			String u = BasicOperations.getShortestExample(a3c, true);
			s.addEx(u, TransducerOperations.image(tE, new RegExp(u).toAutomaton()));
			return false;
		}

		// Universale closure
		Automaton a4a = BasicOperations.minus(BasicOperations.union(aV0, aV1), cdfa);
		Automaton a4b = TransducerOperations.image(TransducerOperations.invert(tE), a4a);
		Automaton a4c = BasicOperations.intersection(BasicOperations.intersection(aV1, cdfa), a4b);
		if (!(BasicOperations.isEmpty(a4c))){
			String u = BasicOperations.getShortestExample(a4c, true);
			s.addUni(u, TransducerOperations.image(tE, new RegExp(u).toAutomaton()));
			return false;
		}

		// Passes
		return true;
	}
}

