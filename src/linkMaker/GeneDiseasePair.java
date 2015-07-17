package linkMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GeneDiseasePair {
	Gene gene;
	Disease disease;
	ArrayList<String> twoHopsLinks;
	
	public GeneDiseasePair(Gene gene, Disease disease) {
		this.gene = gene;
		this.disease = disease;
		this.twoHopsLinks = new ArrayList<String>();
	}
	
	public static int containsGeneDiseasePair(List<GeneDiseasePair> haystack, GeneDiseasePair needle) {
		int index = 0;
		for (GeneDiseasePair pair : haystack) {
//			System.out.println("Pair gene " + pair.getGene().getEntrez_id() + "Needle gene "+ needle.getGene().getEntrez_id()+ " Pair disease" + pair.getDisease().getCui() + " Pair disease "+needle.getDisease().getCui());

			if(pair.getGene().getEntrez_id().equals(needle.getGene().getEntrez_id()) && pair.getDisease().getCui().equals(needle.getDisease().getCui())) {
				return index;
			}
			index++;
		}
		return -1;

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
	
	public void addTwoHopsLinks(String toAdd) {
		if(!this.getTwoHopsLinks().contains(toAdd)) {
			this.twoHopsLinks.add(toAdd);
		}
	}
	
	@Override
	public String toString() {
		return "GeneDiseasePair [gene=" + gene + ", disease=" + disease
				+ twoHopsLinks +"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + ((gene == null) ? 0 : gene.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		GeneDiseasePair other = (GeneDiseasePair) obj;
		return this.disease.cui.equals(other.disease.cui) && this.gene.entrez_id.equals(other.gene.entrez_id);
	
	}
}
