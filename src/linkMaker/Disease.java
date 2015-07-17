package linkMaker;

public class Disease {
	String cui;
	
	public Disease(String cui) {
		this.cui = cui;
	}

	@Override
	public String toString() {
		return "Disease [cui=" + cui + "]";
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}
}
