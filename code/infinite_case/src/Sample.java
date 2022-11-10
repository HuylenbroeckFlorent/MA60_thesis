import java.util.*;

import transducer.*;

public class Sample{
    ArrayList<String> pos;
    ArrayList<String> neg;
    ArrayList<CounterExample> ex;
    ArrayList<CounterExample> uni;

    int size;

    public Sample(){
        pos = new ArrayList();
        neg = new ArrayList();
        ex = new ArrayList();
        uni = new ArrayList();

        size=0;
    }

    public void addPos(String pos){
        if(!(this.pos.contains(pos))){
            size++;
            this.pos.add(pos);
        }
        else{
            System.out.println("/!\\ Duplicate POS");
        }
    }

    public void addNeg(String neg){
        if(!(this.neg.contains(neg))){
            size++;
            this.neg.add(neg);
        }
        else{
            System.out.println("/!\\ Duplicate NEG");
        }
    }

    public void addEx(String u, Automaton a){
        CounterExample cex = new CounterExample(u, a);
        if(!(this.ex.contains(cex))){
            size++;
            ex.add(cex);
        }
        else{
            System.out.println("/!\\ Duplicate EX");
        }
    }

    public void addUni(String u, Automaton a){
        CounterExample cuni = new CounterExample(u, a);
        if(!(this.uni.contains(cuni))){
            size++;
            uni.add(cuni);
        }
        else{
            System.out.println("/!\\ Duplicate UNI");
        }
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
        ret += "====== pos: ";
        if(pos.size()>0){
            ret += pos.toString();
        }
        ret += "\n";
        ret += "====== neg: ";
        if(neg.size()>0){
            ret += neg.toString();
        }
        ret += "\n";
        ret += "====== ex:  \n";
        for (CounterExample ce : ex){
            ret+="ex-"+ce.toString()+"\n";
        }
        ret += "====== uni: \n";
        for (CounterExample ce : uni){
            ret+="uni-"+ce.toString()+"\n";
        }

        return ret;
    }
}