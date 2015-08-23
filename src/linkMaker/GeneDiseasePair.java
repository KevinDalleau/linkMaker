package linkMaker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import fr.kevindalleau.Mapper.Mapper;

public class GeneDiseasePair implements Serializable{
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
				+ ", twoHopsLinks=" + twoHopsLinks + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + ((gene == null) ? 0 : gene.hashCode());
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
		GeneDiseasePair other = (GeneDiseasePair) obj;
		if (disease == null) {
			if (other.disease != null)
				return false;
		} else if (!disease.equals(other.disease))
			return false;
		if (gene == null) {
			if (other.gene != null)
				return false;
		} else if (!gene.equals(other.gene))
			return false;
		if (twoHopsLinks == null) {
			if (other.twoHopsLinks != null)
				return false;
		} else if (!twoHopsLinks.equals(other.twoHopsLinks))
			return false;
		return true;
	}
	
	public static HashMap<String, ArrayList<GeneDiseasePair>> getGeneDiseasesPairs() {
		HashMap<String, ArrayList<GeneDiseasePair>> geneDiseasesPairs = new HashMap<String, ArrayList<GeneDiseasePair>>();
		Query query = new Query();
		QueryEngineHTTP disgenetQuery = query.getGeneDiseaseRelations("disgenet");
		QueryEngineHTTP clinvarQuery = query.getGeneDiseaseRelations("clinvar");
		ResultSet disgenet = disgenetQuery.execSelect();
		ResultSet clinvar = clinvarQuery.execSelect();
	
		while(clinvar.hasNext()) {
			QuerySolution solution = clinvar.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode diseaseNode = solution.get("disease");
			RDFNode twoHopsLinksNode = solution.get("2_hops_links1");
			Gene gene = new Gene();
			gene.setEntrez_id(geneNode.toString());
			Disease disease = new Disease(diseaseNode.toString());
			GeneDiseasePair geneDiseasePair = new GeneDiseasePair(gene,disease);
			ArrayList<GeneDiseasePair> associatedPairs = geneDiseasesPairs.get(geneNode.toString());
			if(associatedPairs != null ) { // Test if a given gene id already has been associated with one or more gene-diseases pair(s)
				int indexOfPair = GeneDiseasePair.containsGeneDiseasePair(associatedPairs, geneDiseasePair);
				if(indexOfPair == -1) { //If the pair has not been associated to the gene
					geneDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
					associatedPairs.add(geneDiseasePair);
				}
				else {
					associatedPairs.get(indexOfPair).addTwoHopsLinks(twoHopsLinksNode.toString());
				}
				
			}
			else {
				ArrayList<GeneDiseasePair> pair = new ArrayList<GeneDiseasePair>();
				geneDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
				pair.add(geneDiseasePair);
				geneDiseasesPairs.put(geneNode.toString(), pair);
			}
		};
		clinvarQuery.close();
		System.out.println("Clinvar data for gene-disease links stored");
		
		while(disgenet.hasNext()) {
			QuerySolution solution = disgenet.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode diseaseNode = solution.get("disease");
			RDFNode twoHopsLinksNode = solution.get("2_hops_links1");
			Gene gene = new Gene();
			gene.setEntrez_id(geneNode.toString());
			
			Disease disease = new Disease(diseaseNode.toString());
			GeneDiseasePair geneDiseasePair = new GeneDiseasePair(gene,disease);
			ArrayList<GeneDiseasePair> associatedPairs = geneDiseasesPairs.get(geneNode.toString());
			if(associatedPairs != null ) { // Test if a given gene id already has been associated with one or more gene-diseases pair(s)
				int indexOfPair = GeneDiseasePair.containsGeneDiseasePair(associatedPairs, geneDiseasePair);
				if(indexOfPair == -1) { //If the pair has not been associated to the gene
					geneDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
					associatedPairs.add(geneDiseasePair);
				}
				else {
					associatedPairs.get(indexOfPair).addTwoHopsLinks(twoHopsLinksNode.toString());
				}
				
			}
			else {
				ArrayList<GeneDiseasePair> pair = new ArrayList<GeneDiseasePair>();
				geneDiseasePair.addTwoHopsLinks(twoHopsLinksNode.toString());
				pair.add(geneDiseasePair);
				geneDiseasesPairs.put(geneNode.toString(), pair);
			}
		};
		disgenetQuery.close();
		System.out.println("Disgenet data for gene-disease links stored");
		
		
		return geneDiseasesPairs;
	}
}
