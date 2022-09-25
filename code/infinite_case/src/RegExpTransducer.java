import java.util.*;

import dk.brics.automaton.*;

public class RegExpTransducer extends RegExp{

	public RegExpTransducer(){
		super.RegExp();
	}

	public TransducerAutomaton toTransducer(HashMap map){
		automaton = toAutomatonAllowMutate(null, null, true);
		return new TransducerAutomaton(automaton, HashMap);
	}
}