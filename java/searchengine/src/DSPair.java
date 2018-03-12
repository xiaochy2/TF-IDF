import java.util.*;
// implements Comparable interface to do the sorting;
public class DSPair implements Comparable<DSPair> {

	String docu;
	Double score;
	
	DSPair(String docu,Double score){
		this.docu = docu;
		this.score = score;
	}
	
    public String getDocu() {  
        return this.docu;  
    }  
	
    public Double getScore() {  
        return this.score;  
    }  
    public int compareTo(DSPair arg0) {  
        return arg0.getScore().compareTo(this.getScore());  
    }  
}