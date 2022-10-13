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
}