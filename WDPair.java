
public class WDPair {
	
	public String term;
	
	public String docu;
	
	public double score;
	
	public WDPair() {
			
	}
	
	public WDPair(String term, String docu, double score) {
		this.term = term;
		this.docu = docu;
		this.score  = score;	
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getDocu() {
		return docu;
	}
	
	public void setDocu(String docu) {
		this.docu = docu;
	}
	
	public double getScore() {
		return score;
	}
	
	public double setScore(double score) {
		this.score = score;
	}
	
}