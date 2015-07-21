package linkMaker;

public class Disease {
	String cui;
	String pharmgkbid;
	public Disease(String cui) {
		this.cui = cui;
		this.pharmgkbid = "";
	}

	@Override
	public String toString() {
		return "Disease [cui =" + cui + " pharmgkbid =" + pharmgkbid +" ]";
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}
	
	public String getPharmgkbid() {
		return pharmgkbid;
	}
	
	public void setPharmgkbid(String pharmgkbId) {
		this.pharmgkbid = pharmgkbId;
	}
}
