package linkMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import fr.kevindalleau.Mapper.Mapper;

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
	public static ArrayList<DrugDiseasePair> getDrugDiseasesPairs(Drug drug) {
		Mapper mapper = new Mapper();
		System.out.println("Diseases linked to " + drug.toString());
		ArrayList<DrugDiseasePair> drugDiseasesPairs = new ArrayList<DrugDiseasePair>(10000);
		Query query = new Query();
		if(query.getDrugDiseaseRelationsFromSider(drug) != null) {
			ResultSet Sider = query.getDrugDiseaseRelationsFromSider(drug);
			while(Sider.hasNext()) {
				QuerySolution solution = Sider.nextSolution();
				RDFNode twoHopsLinksNode = solution.get("2_hops_links_2");
				RDFNode diseaseNode = solution.get("disease");
				Disease disease= new Disease(diseaseNode.toString());
				DrugDiseasePair drugDiseasePair = new DrugDiseasePair(drug,disease);
				drugDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());;
//				System.out.println(drugDiseasePair.toString());
				drugDiseasesPairs.add(drugDiseasePair);
			}
			
		}
		
		return drugDiseasesPairs;
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + ((drug == null) ? 0 : drug.hashCode());
		result = prime * result + ((twoHopsLinks == null) ? 0 : twoHopsLinks.hashCode());
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
		DrugDiseasePair other = (DrugDiseasePair) obj;
		if (disease == null) {
			if (other.disease != null)
				return false;
		} else if (!disease.equals(other.disease))
			return false;
		if (drug == null) {
			if (other.drug != null)
				return false;
		} else if (!drug.equals(other.drug))
			return false;
		if (twoHopsLinks == null) {
			if (other.twoHopsLinks != null)
				return false;
		} else if (!twoHopsLinks.equals(other.twoHopsLinks))
			return false;
		return true;
	}

	public static HashMap<String, ArrayList<DrugDiseasePair>> getDrugDiseasesPairs() {
		Mapper mapper = new Mapper();
		HashMap<String, ArrayList<DrugDiseasePair>> drugDiseasesPairs = new HashMap<String, ArrayList<DrugDiseasePair>>();
		Query query = new Query();
		ResultSet medispan = query.getDrugDiseaseRelations("medispan");
		//ResultSet sider = query.getDrugDiseaseRelations("sider");
		
		while(medispan.hasNext()) {
			QuerySolution solution = medispan.nextSolution();
			RDFNode drugNode = solution.get("drug");
			RDFNode diseaseNode = solution.get("disease");
			RDFNode twoHopsLinksNode = solution.get("2_hops_links2");
			Drug drug = new Drug();
			drug.setPharmgkb_id(mapper.getPharmGKB_from_UMLS(drugNode.toString()));
			Disease disease = new Disease(diseaseNode.toString());
			DrugDiseasePair drugDiseasePair = new DrugDiseasePair(drug,disease);
			ArrayList<DrugDiseasePair> associatedPairs = drugDiseasesPairs.get(drugNode.toString());
			if(associatedPairs != null ) { // Test if a given gene id already has been associated with one or more gene-diseases pair(s)
				int indexOfPair = DrugDiseasePair.containsDrugDiseasePair(associatedPairs, drugDiseasePair);
				if(indexOfPair == -1) {
					drugDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
					associatedPairs.add(drugDiseasePair);
				}
				else {
					associatedPairs.get(indexOfPair).addTwoHopsLinks(twoHopsLinksNode.toString());
				}
				
			}
			else {
				ArrayList<DrugDiseasePair> pair = new ArrayList<DrugDiseasePair>();
				drugDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
				pair.add(drugDiseasePair);
				drugDiseasesPairs.put(drugNode.toString(), pair);
			}
		};
		
//		while(sider.hasNext()) { //Warning : drug here is not the CUI, but the Stitch ID (unformatted)
//			QuerySolution solution = medispan.nextSolution();
//			RDFNode drugNode = solution.get("drug");
//			RDFNode diseaseNode = solution.get("disease");
//			RDFNode twoHopsLinksNode = solution.get("2_hops_links2");
//			Drug drug = new Drug();
//			
//			int stitchUnformatted = Integer.parseInt(drugNode.toString());
//			int stitch = stitchUnformatted - 1000000;
//			System.out.println(stitch);
////			drug.setPharmgkb_id(mapper.getPharmGKB_from_UMLS(drugNode.toString()));
////			Disease disease = new Disease(diseaseNode.toString());
////			DrugDiseasePair drugDiseasePair = new DrugDiseasePair(drug,disease);
////			ArrayList<DrugDiseasePair> associatedPairs = drugDiseasesPairs.get(drugNode.toString());
////			if(associatedPairs != null ) { // Test if a given gene id already has been associated with one or more gene-diseases pair(s)
////				int indexOfPair = DrugDiseasePair.containsDrugDiseasePair(associatedPairs, drugDiseasePair);
////				if(indexOfPair == -1) {
////					drugDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
////					associatedPairs.add(drugDiseasePair);
////				}
////				else {
////					associatedPairs.get(indexOfPair).addTwoHopsLinks(twoHopsLinksNode.toString());
////				}
////				
////			}
////			else {
////				ArrayList<DrugDiseasePair> pair = new ArrayList<DrugDiseasePair>();
////				drugDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
////				pair.add(drugDiseasePair);
////				drugDiseasesPairs.put(drugNode.toString(), pair);
////			}
//		};
	
		return drugDiseasesPairs;
	}

	@Override
	public String toString() {
		return "DrugDiseasePair [drug=" + drug + ", disease=" + disease
				+ ", twoHopsLinks=" + twoHopsLinks + "]";
	}
	
}
