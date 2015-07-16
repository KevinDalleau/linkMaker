package linkMaker;

import java.util.ArrayList;

public class GeneDiseasePair {
	Gene gene;
	Disease disease;
	ArrayList<String> twoHopsLinks;
	
	public GeneDiseasePair(Gene gene, Disease disease) {
		this.gene = gene;
		this.disease = disease;
		this.twoHopsLinks = new ArrayList<String>();
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

	public ArrayList<String> getTwoHopsLinks() {
		return twoHopsLinks;
	}

	public void setTwoHopsLinks(ArrayList<String> twoHopsLinks) {
		this.twoHopsLinks = twoHopsLinks;
	}
	
	public void addTwoHopsLink(String twoHopsLink) {
		this.twoHopsLinks.add(twoHopsLink);
	}

	@Override
	public String toString() {
		return "GeneDiseasePair [gene=" + gene + ", disease=" + disease
				+ ", twoHopsLinks=" + twoHopsLinks + "]";
	}
}
