package linkMaker;

import java.io.Serializable;

public class Disease implements Serializable{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cui == null) ? 0 : cui.hashCode());
		result = prime * result + ((pharmgkbid == null) ? 0 : pharmgkbid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Disease other = (Disease) obj;
		if (cui == null) {
			if (other.cui != null)
				return false;
		} else if (!cui.equals(other.cui))
			return false;
		if (pharmgkbid == null) {
			if (other.pharmgkbid != null)
				return false;
		} else if (!pharmgkbid.equals(other.pharmgkbid))
			return false;
		return true;
	}

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
