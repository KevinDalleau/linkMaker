package linkMaker;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneDiseasePair {
	Gene gene;
	Disease disease;
	HashMap<GeneDiseasePair,ArrayList<String>> twoHopsLinks;
	
	public GeneDiseasePair(Gene gene, Disease disease) {
		this.gene = gene;
		this.disease = disease;
		this.twoHopsLinks = new HashMap<GeneDiseasePair,ArrayList<String>>();
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public HashMap<GeneDiseasePair,ArrayList<String>> getTwoHopsLinks() {
		return twoHopsLinks;
	}

	public void setTwoHopsLinks(HashMap<GeneDiseasePair,ArrayList<String>> twoHopsLinks) {
		this.twoHopsLinks = twoHopsLinks;
	}
	
	@Override
	public String toString() {
		return "GeneDiseasePair [gene=" + gene + ", disease=" + disease
				+ ", twoHopsLinks=" + twoHopsLinks + "]";
	}
}
