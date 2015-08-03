package linkMaker;

import java.util.ArrayList;
import java.util.List;

public class Association {
	private GeneDiseasePair geneDiseasePair;
	private DrugDiseasePair drugDiseasePair;
	
	public Association(GeneDiseasePair geneDiseasePair,DrugDiseasePair drugDiseasePair) {
		this.geneDiseasePair= geneDiseasePair;
		this.drugDiseasePair= drugDiseasePair;
	}
	
	public void printAssociation() {
		System.out.println("Correspondance trouvée entre "+this.geneDiseasePair.getGene().getEntrez_id()+ " et "+this.drugDiseasePair.getDisease().getCui());
		List<String> two_hops_links_1 = this.geneDiseasePair.getTwoHopsLinks();
		List<String> two_hops_links_2 = this.drugDiseasePair.getTwoHopsLinks();
		List<String> list = this.getCombinations(two_hops_links_1, two_hops_links_2);
		for(String output : list) {
			System.out.println(output);
		}
	}
	
	private List<String> getCombinations(List<String> list1, List<String> list2) {
		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for(String string1 : list1) {
			for(String string2 : list2) {
				builder.setLength(0);
				builder.append(string1).append(",").append(string2);
				result.add(builder.toString());
			}
		}
		return result;
	}
	public GeneDiseasePair getGeneDiseasePair() {
		return geneDiseasePair;
	}

	public void setGeneDiseasePair(GeneDiseasePair geneDiseasePair) {
		this.geneDiseasePair = geneDiseasePair;
	}

	public DrugDiseasePair getDrugDiseasePair() {
		return drugDiseasePair;
	}

	public void setDrugDiseasePair(DrugDiseasePair drugDiseasePair) {
		this.drugDiseasePair = drugDiseasePair;
	}

	@Override
	public String toString() {
		return "Association [geneDiseasePair=" + geneDiseasePair
				+ ", drugDiseasePair=" + drugDiseasePair + "]";
	}
}
