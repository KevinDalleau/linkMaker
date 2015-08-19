package linkMaker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Association {
	private DrugGenePair drugGenePair;
	private GeneDiseasePair geneDiseasePair;
	private DrugDiseasePair drugDiseasePair;
	
	public Association(DrugGenePair drugGenePair,GeneDiseasePair geneDiseasePair,DrugDiseasePair drugDiseasePair) {
		this.drugGenePair = drugGenePair;
		this.geneDiseasePair= geneDiseasePair;
		this.drugDiseasePair= drugDiseasePair;
	}
	
	public void printAssociation(String type) {
		System.out.println("Correspondance trouvée entre "+this.geneDiseasePair.getGene().getEntrez_id()+ " et "+this.drugDiseasePair.getDrug().getPharmgkb_id()+" passant par "+ this.geneDiseasePair.getDisease().getCui());
		List<String> geneAttributes = this.geneDiseasePair.getGene().getAttributes();
		List<String> drugAttributes = this.drugDiseasePair.getDrug().getAttributes();
		List<String> diseaseAttributes = this.geneDiseasePair.getDisease().getAttributes();
//		if(this.drugDiseasePair.getDisease().getAttributes() !=null) {
//			diseaseAttributes.addAll(this.drugDiseasePair.getDisease().getAttributes());
//		}
		List<String> one_hops_links = this.drugGenePair.getOneHopsLinks();
		List<String> two_hops_links_1 = this.geneDiseasePair.getTwoHopsLinks();
		List<String> two_hops_links_2 = this.drugDiseasePair.getTwoHopsLinks();
		List<List<String>> globalList = new ArrayList<List<String>>();
		globalList.add(geneAttributes);
		globalList.add(drugAttributes);
		if(diseaseAttributes != null) {
			globalList.add(diseaseAttributes);
		}
		else{
			globalList.add(new ArrayList<String>());
		}
		globalList.add(one_hops_links);
		globalList.add(two_hops_links_1);
		globalList.add(two_hops_links_2);
		System.out.println(globalList);
		int numberOfPaths = Statistics.getNumberOfPaths(globalList);
		System.out.println(numberOfPaths);
		
		List<String> list = this.getCombinations(globalList);
//		System.out.println(list);
		for(String output : list) {
			int numberOfOccurrences = StringUtils.countMatches(output, ",");
			if(numberOfOccurrences!=5) {
				System.out.println(this.getDrugDiseasePair().getDrug().getPharmgkb_id()+this.getGeneDiseasePair().getGene().getEntrez_id());
				System.out.println(list);
				System.out.println(output);
			}
			File linksFile = null;
			if(type.equals("linked")) {
				linksFile = new File("./links.csv");
			}
			else if(type.equals("not_linked")) {
				linksFile = new File("./nolinks.csv");
			}
			else if(type.equals("specific")) {
				linksFile = new File("./specificlinks.csv");
			}
			else if(type.equals("ambiguous")) {
				linksFile = new File("./ambiguous.csv");
			}
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
		if(globalList.get(0)!=null && globalList.size() != 0 /*&& globalList.get(0).size() != 0*/) {
			result = globalList.get(0);
		}
		
		for(int i = 1; i<globalList.size();i++) {
			if(globalList.get(i) !=null /*&& globalList.get(i).size() != 0*/) {
				result = combineLists(result,globalList.get(i));
			}
		}
		return result;
	}
	
	private List<String> combineLists(List<String> list1, List<String> list2) {
		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		if(list1.size() !=0 && list2.size() != 0) {
			for(String string1 : list1) {
				for(String string2 : list2) {
					builder.setLength(0);
					builder.append(string1).append(",").append(string2);
					result.add(builder.toString());
				}
			}	
		}
		else if(list1.size() !=0 && list2.size() == 0){ // The second list is empty, but still, we need the elements of the first list. N.A appended to each element of list 1.
			for(String string1 : list1) {
					builder.setLength(0);
					builder.append(string1).append(",").append("N.A");
					result.add(builder.toString());
			}	
		}
		else if(list1.size() == 0 && list2.size() !=0) {
			for(String string2 : list2) {
				builder.setLength(0);
				builder.append("N.A").append(",").append(string2);
				result.add(builder.toString());
		}	
		}
		else if(list1.size() == 0 && list2.size() ==0) {
				builder.setLength(0);
				builder.append("N.A").append(",").append("N.A");
				result.add(builder.toString());
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
