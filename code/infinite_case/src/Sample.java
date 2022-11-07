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

    public Set<String> W(){
        Set<String> ret = new HashSet();
        ret.addAll(pos);
        ret.addAll(neg);
        for(CounterExample e : ex){
            ret.add(e.getU());
        }
        for(CounterExample u : uni){
            ret.add(u.getU());
        }

        return ret;
    }

    public ArrayList<String> getPos(){
        return pos;
    }

    public ArrayList<String> getNeg(){
        return neg;
    }

    public ArrayList<CounterExample> getEx(){
        return ex;
    }

    public ArrayList<CounterExample> getUni(){
        return uni;
    }

    public String toString(){
        String ret = "";
        ret += "====== pos: "+pos.toString()+"\n";
        ret += "====== neg: "+neg.toString()+"\n";
        ret += "====== ex:  \n";
        for (CounterExample ce : ex){
            ret+=ce.toString()+"\n";
        }
        ret += "====== uni: \n";
        for (CounterExample ce : uni){
            ret+=ce.toString()+"\n";
        }

        return ret;
    }
}