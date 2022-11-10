import java.util.*;

import transducer.*;

public class CounterExample{
    String u;
    Automaton a;

    public CounterExample(String u, Automaton a){
        this.a=a;
        this.u=u;
    }

    public String getU(){
        return u;
    }

    public Automaton getA(){
        return a;
    }

    public Set<String> getAWords(){
        if (SpecialOperations.isFinite(a)){
            return SpecialOperations.getFiniteStrings(a);
        }
        else{
            return new HashSet<String>();
        }
    }

    public String toString(){
        return "u: "+u+"\nDFA: \n"+a.toString()+"\n";
    }

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