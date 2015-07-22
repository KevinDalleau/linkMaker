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
	
	public static HashMap<String, ArrayList<DrugDiseasePair>> getDrugDiseasesPairs() {
		HashMap<String, ArrayList<DrugDiseasePair>> drugDiseasesPairs = new HashMap<String, ArrayList<DrugDiseasePair>>();
		String queryLinks = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"SELECT ?drug ?2_hops_links2 ?disease\n" + 
				"WHERE {\n" + 
				"  ?drug_uri ?2_hops_links2_uri ?disease_uri.\n" + 
				"  ?drug_uri rdf:type <http://orpailleur.fr/medispan/drug>.\n" + 
				"  ?disease_uri rdf:type <http://orpailleur.fr/medispan/event>\n" + 
				"    BIND(REPLACE(str(?drug_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?drug)\n" + 
				"    BIND(REPLACE(str(?disease_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?disease)\n" + 
				"  BIND(REPLACE(str(?2_hops_links2_uri), \"http://orpailleur.fr/medispan/\", \"\") AS ?2_hops_links2)\n" + 
				"}";
		Mapper mapper = new Mapper();
		QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/medispan/sparql", queryLinks);
		queryExec.addParam("timeout","3600000");

		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
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
		queryExec.close();
		return drugDiseasesPairs;
	}
	
}
