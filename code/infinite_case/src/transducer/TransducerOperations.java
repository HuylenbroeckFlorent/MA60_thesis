package transducer;

import java.util.*;

public class TransducerOperations{

	/**
	 * Returns true if the given convolution of two string is accepted by the transducer. 
	 */
	public static boolean run(Automaton a, TransducerPair p) {
		if (a.isSingleton())
			return p.equals(a.singleton);
		if (a.deterministic) {
			State s = a.initial;
			for (int i = 0; i < max(p.getKey().length(), p.getValue().length()); i++) {
				State q = s.step(p.charAt(i));
				if (q == null)
					return false;
				p = q;
			}
			return p.accept;
		} else {
			Set<State> states = a.getStates();
			Automaton.setStateNumbers(states);
			LinkedList<State> pp = new LinkedList<State>();
			LinkedList<State> pp_other = new LinkedList<State>();
			BitSet bb = new BitSet(states.size());
			BitSet bb_other = new BitSet(states.size());
			pp.add(a.initial);
			ArrayList<State> dest = new ArrayList<State>();
			boolean accept = a.initial.accept;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				accept = false;
				pp_other.clear();
				bb_other.clear();
				for (State p : pp) {
					dest.clear();
					p.step(c, dest);
					for (State q : dest) {
						if (q.accept)
							accept = true;
						if (!bb_other.get(q.number)) {
							bb_other.set(q.number);
							pp_other.add(q);
						}
					}
				}
				LinkedList<State> tp = pp;
				pp = pp_other;
				pp_other = tp;
				BitSet tb = bb;
				bb = bb_other;
				bb_other = tb;
			}
			return accept;
		}
	}
}