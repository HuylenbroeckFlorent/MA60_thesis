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
}

