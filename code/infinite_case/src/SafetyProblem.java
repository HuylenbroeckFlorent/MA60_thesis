import transducer.*;

public class SafetyProblem{

	private Automaton aV0, aV1, aI, aF;
	private TransducerAutomaton tE;
	private char[] alphabet;

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

	// public static SafetyProblem linearGame(int k){
		
	// }

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
	 * Add a counter example to the sample if a check fails
	 *
	 * @param cdfa Automaton conjectured by the learner 
	 * @param s Sample object to store the current counter-examples.
	 * @return Boolean, true if the Automaton represents the winning set for sp. False if a counter-example was added to the sample.
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
		// System.out.println("CHECKING FOR EXISTENTIAL CLOSURE");
		// System.out.println("- INVERTED TE : ");
		// System.out.println(TransducerOperations.invert(tE));
		// System.out.println("- CDFA :");
		// System.out.println(cdfa.toString());
		Automaton a3a = TransducerOperations.image(TransducerOperations.invert(tE), cdfa);
		// System.out.println("IMAGE PRODUCED :");
		// System.out.println(a3a.toString());
		// System.out.println("- REMOVING IMAGE FROM :");
		// System.out.println(aV0.toString());
		// System.out.println("PRODUCED :");
		Automaton a3b = BasicOperations.minus(aV0, a3a);
		// System.out.println(a3b.toString());
		// System.out.println("- INTERSECTION WITH : ");
		// System.out.println(cdfa.toString());
		Automaton a3c = BasicOperations.intersection(cdfa, a3b);
		// System.out.println("PRODUCED : ");
		// System.out.println(a3c.toString());
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

