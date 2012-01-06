package alice.tuprologx.eclipse.core;

public class PrologQueryResult {
	String result;
	String spy;
	String lost;
	String output;
	PrologEngine engine;
	
	public PrologQueryResult(PrologEngine engine)
	{
		this.result = "";
		this.spy = "";
		this.lost = "";
		this.output = "";
		this.engine = engine;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		if (result != null)
			this.result = result;
		else 
			this.result = "";
	}
	public String getSpy() {
		return spy;
	}
	
	public void setSpy(String spy) {
		if( spy != null)
			this.spy = spy;
		else
			this.spy = "";
	}
	
	public String getLost() {
		return lost;
	}
	
	public void setLost(String lost) {
		if(lost != null)
			this.lost = lost;
		else
			this.lost = "";
	}
	
	public String getOutput() {
		return output;
	}
	
	public void setOutput(String output) {
		if( output != null)
			this.output = output;
		else
			this.output = "";
	}
	
	public PrologEngine getEngine() {
		return engine;
	}
}
