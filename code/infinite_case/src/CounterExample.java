import java.util.*;

import transducer.*;

/**
* Implication counter-example object used in the Sample object.
* An implication counter-example stores a word u and an automata A.
* An implication counter-example means that if the antecedent u was accepted during the learning, 
* then the words of the consequence A will play a role in the learning too.
* 
* @author HUYLENBROECK Florent
*/
public class CounterExample{

    /**
    * The antecedent word u.
    */
    String u;

    /**
    * The consequence automata A.
    */
    Automaton a;

    /**
    * Constructor.
    *
    * @param u String. The antecedent word for this implication counter-example.
    * @param a Automaton. The language that will be used as consequence for this couter-example.
    */
    public CounterExample(String u, Automaton a){
        this.a=a;
        this.u=u;
    }

    /**
    * Returns the antecedent word of this counter-example.
    *
    * @return String. antecedent of this counter-example.
    */
    public String getU(){
        return u;
    }

    /**
    * Returns the automata accepting the language of the consequence of this counter-example.
    *
    * @return Automaton accepting the language of the consequence of this counter-example.
    */
    public Automaton getA(){
        return a;
    }

    @Override
    public String toString(){
        return "u: "+u+"\nDFA: \n"+a.toString()+"\n";
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        if(!(o instanceof CounterExample)){
            return false;
        }

        CounterExample ce = (CounterExample) o;

        return this.u.equals(ce.getU()) && this.a.equals(ce.getA());
    }
}