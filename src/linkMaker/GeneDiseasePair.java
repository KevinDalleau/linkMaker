package linkMaker;

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
	
	public static HashMap<String, ArrayList<GeneDiseasePair>> getGeneDiseasesPairs() {
		Mapper mapper = new Mapper();
		HashMap<String, ArrayList<GeneDiseasePair>> geneDiseasesPairs = new HashMap<String, ArrayList<GeneDiseasePair>>();
		String queryLinks = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX  foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX  ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>\n" + 
				"PREFIX  sio:  <http://semanticscience.org/resource/>\n" + 
				"PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\n" + 
				"PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n" + 
				"PREFIX  wp:   <http://vocabularies.wikipathways.org/wp#>\n" + 
				"PREFIX  void: <http://rdfs.org/ns/void#>\n" + 
				"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX  dcterms: <http://purl.org/dc/terms/>\n" + 
				"SELECT DISTINCT  ?gene ?disease ?2_hops_links1\n" + 
				"WHERE\n" + 
				"\n" + 
				"  { ?gda sio:SIO_000253 ?source .\n" + 
				"    ?source rdfs:label ?originalSource .\n" + 
				"    ?source rdfs:comment ?curation\n" + 
				"    FILTER regex(?curation, \"CURATED\")\n" + 
				"    ?gda sio:SIO_000628 ?gene_uri .\n" + 
				"    ?gda sio:SIO_000628 ?disease_uri .\n" + 
				"    ?gda sio:SIO_000001 ?a .\n" + 
				"    ?a skos:exactMatch ?2_hops_links1 .\n" + 
				"    ?gene_uri rdf:type ncit:C16612 .\n" + 
				"    ?gene_uri sio:SIO_000062 ?pathway_id .\n" + 
				"    ?pathway_id foaf:name ?pathway .\n" + 
				"    ?gene_uri sio:SIO_000095 ?class_id .\n" + 
				"    ?class_id foaf:name ?class . \n" + 
				"    ?disease_uri rdf:type ncit:C7057\n" + 
				"    BIND((replace(str(?gene_uri), \"http://identifiers.org/ncbigene/\", \"\")) AS ?gene)\n" + 
				"    BIND((replace(str(?disease_uri), \"http://linkedlifedata.com/resource/umls/id/\", \"\")) AS ?disease)\n" + 
				"}\n" + 
				"";
		
		QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/disgenet/sparql", queryLinks);
		queryExec.addParam("timeout","3600000");

		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
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
				System.out.println(indexOfPair);
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
		queryExec.close();
		return geneDiseasesPairs;
	}
}
