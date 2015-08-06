package linkMaker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		List<String> geneAttributes = this.geneDiseasePair.getGene().getAttributes();
		List<String> drugAttributes = this.drugDiseasePair.getDrug().getAttributes();
		List<String> diseaseAttributes = this.geneDiseasePair.getDisease().getAttributes();
		if(this.drugDiseasePair.getDisease().getAttributes() !=null) {
			diseaseAttributes.addAll(this.drugDiseasePair.getDisease().getAttributes());
		}
		List<String> two_hops_links_1 = this.geneDiseasePair.getTwoHopsLinks();
		List<String> two_hops_links_2 = this.drugDiseasePair.getTwoHopsLinks();
		List<List<String>> globalList = new ArrayList<List<String>>();
		globalList.add(geneAttributes);
		globalList.add(drugAttributes);
		globalList.add(diseaseAttributes);
		globalList.add(two_hops_links_1);
		globalList.add(two_hops_links_2);
		List<String> list = this.getCombinations(globalList);
		for(String output : list) {
			System.out.println(output);
			File linksFile = new File("./links.csv");
			if(!linksFile.exists()) {
				try {
					linksFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fileWriter = new FileWriter(linksFile.getName(), true);
				BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
    	        bufferWritter.write(output+"\n");
    	        bufferWritter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<String> getCombinations(List<List<String>> globalList) {
		List<String> result = new ArrayList<String>();
		if(globalList.get(0)!=null) {
			result = globalList.get(0);
		}
		for(int i = 1; i<globalList.size();i++) {
			if(globalList.get(i) !=null) {
				result = combineLists(result,globalList.get(i));
			}
		}
		return result;
	}
	
	private List<String> combineLists(List<String> list1, List<String> list2) {
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
