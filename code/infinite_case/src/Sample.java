import java.util.*;

import transducer.*;

public class Sample{
    ArrayList<String> pos;
    ArrayList<String> neg;
    ArrayList<CounterExample> ex;
    ArrayList<CounterExample> uni;

    public Sample(){
        pos = new ArrayList();
        neg = new ArrayList();
        ex = new ArrayList();
        uni = new ArrayList();
    }

    public void addPos(String pos){
        this.pos.add(pos);
    }

    public void addNeg(String neg){
        this.neg.add(neg);
    }

    public void addEx(String u, Automaton a){
        ex.add(new CounterExample(u, a));
    }

    public void addUni(String u, Automaton a){
        ex.add(new CounterExample(u, a));
    }
}