package learner;

import java.util.*;

import transducer.*;
import teacher.*;

/**
* Sample used by the learner to store counter-examples for the learning process.
* Positive counter-examples should be accepted by the learned DFA.
* Negative counter-examples should be rejected by the learned DFA.
* Existential implication counter-examples mean that if their antecedent word is accepted, at least on word of their consequence has to be accepted.
* Universal implication counter-examples mean that if their antecedent word is accepted, aall words of their consequence have to be accepted.
*
* @author HUYLENBROECK Florent
*/
public class Sample{

    /**
    * Positive counter-examples.
    */
    ArrayList<String> pos;

    /**
    * Negative counter-examples.
    */ 
    ArrayList<String> neg;

    /**
    * Existential implication counter-examples.
    */
    ArrayList<CounterExample> ex;

    /**
    * Universal implication counter-examples.
    */
    ArrayList<CounterExample> uni;

    /**
    * Total number of counter-examples in the sample.
    */
    int size;

    /**
    * Constructor. Initializes an empty sample.
    */
    public Sample(){
        pos = new ArrayList();
        neg = new ArrayList();
        ex = new ArrayList();
        uni = new ArrayList();

        size=0;
    }

    /**
    * Adds a positive counter-example to the sample if it does not already contain it.
    *
    * @param pos String. The positive word.
    */
    public void addPos(String pos){
        if(!(this.pos.contains(pos))){
            size++;
            this.pos.add(pos);
        }
        else{
            System.out.println("/!\\ Duplicate POS");
        }
    }

    /**
    * Adds a negative counter-example to the sample if it does not already contain it.
    *
    * @param pos String. The negative word.
    */
    public void addNeg(String neg){
        if(!(this.neg.contains(neg))){
            size++;
            this.neg.add(neg);
        }
        else{
            System.out.println("/!\\ Duplicate NEG");
        }
    }

    /**
    * Adds a existential implication counter-example to the sample if it does not already contain it.
    *
    * @param u String. The antecedent word of the counter-example.
    * @param a Automaton accepting the consequence language of the counter-example.
    */
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

    /**
    * Adds a universal implication counter-example to the sample if it does not already contain it.
    *
    * @param u String. The antecedent word of the counter-example.
    * @param a Automaton accepting the consequence language of the counter-example.
    */
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

    /**
    * Returns the set of positive counter-examples of this sample.
    *
    * @return String[]. The set of positive counter-examples of this sample.
    */
    public ArrayList<String> getPos(){
        return pos;
    }

    /**
    * Returns the set of negative counter-examples of this sample.
    *
    * @return String[]. The set of negative counter-examples of this sample.
    */
    public ArrayList<String> getNeg(){
        return neg;
    }

    /**
    * Returns the set of existential implication counter-examples of this sample.
    *
    * @return String[]. The set of existential implication counter-examples of this sample.
    */
    public ArrayList<CounterExample> getEx(){
        return ex;
    }

    /**
    * Returns the set of universal implication counter-examples of this sample.
    *
    * @return String[]. The set of universal implication counter-examples of this sample.
    */
    public ArrayList<CounterExample> getUni(){
        return uni;
    }

    @Override
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