package linkMaker;

import java.util.ArrayList;

public class DrugDiseasePair {
	Drug drug;
	Disease disease;
	ArrayList<String> twoHopsLinks;
	
	public DrugDiseasePair(Drug drug, Disease disease) {
		this.drug = drug;
		this.disease = disease;
	}
	
}
