package linkMaker;

import java.util.ArrayList;
import java.util.List;

public class DrugDiseasePair {
	Drug drug;
	Disease disease;
	ArrayList<String> twoHopsLinks;
	
	public DrugDiseasePair(Drug drug, Disease disease) {
		this.drug = drug;
		this.disease = disease;
		this.twoHopsLinks = new ArrayList<String>();
	}
	
	public static int containsDrugDiseasePair(List<DrugDiseasePair> haystack, DrugDiseasePair needle) {
		int index = 0;
		for (DrugDiseasePair pair : haystack) {
			if(pair.getDrug().getPharmgkb_id() != null && needle.getDrug().getPharmgkb_id() !=null) {
				if(pair.getDrug().getPharmgkb_id().equals(needle.getDrug().getPharmgkb_id()) && pair.getDisease().getCui().equals(needle.getDisease().getCui())) {
					return index;
				}
			}
			index++;
		}
		return -1;

	}
	
	public void addTwoHopsLinks(String toAdd) {
		if(!this.getTwoHopsLinks().contains(toAdd)) {
			this.twoHopsLinks.add(toAdd);
		}
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ArrayList<String> getTwoHopsLinks() {
		return twoHopsLinks;
	}

	public void setTwoHopsLinks(ArrayList<String> twoHopsLinks) {
		this.twoHopsLinks = twoHopsLinks;
	}
	
}
