package linkMaker;

import java.util.ArrayList;
import java.util.LinkedList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class DrugGenePair {
	Drug drug;
	Gene gene;
	ArrayList<String> diseases;
	
	public DrugGenePair(Gene gene, Drug drug) {
		this.gene = gene;
		this.drug = drug;
	}
	
	public void add_undirect_link_clinvar() {
		String gene = this.gene.getEntrez_id();
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
				"\n" + 
				"SELECT DISTINCT ?gene ?disease\n" + 
				"WHERE {\n" + 
				"{\n" + 
				"?a <http://bio2rdf.org/clinvar_vocabulary:Variant_Gene> ?variant_gene .\n" + 
				"?variant_gene <http://bio2rdf.org/clinvar_vocabulary:x-gene> ?gene_bio .\n" + 
				"?a <http://bio2rdf.org/clinvar_vocabulary:Variant_Phenotype> ?phenotype .\n" + 
				"?phenotype <http://bio2rdf.org/clinvar_vocabulary:x-medgen> ?disease_bio\n" + 
				"      FILTER regex(str(?gene_bio),\"http://bio2rdf.org/ncbigene:"+gene+"\")\n" + 
				"      BIND(REPLACE(str(?gene_bio), \"http://bio2rdf.org/ncbigene:\", \"\") AS ?gene)\n" + 
				"      BIND(REPLACE(str(?disease_bio), \"http://bio2rdf.org/medgen:c\",\"C\") AS ?disease)\n" + 
				"\n" + 
				"}\n" + 
				"}\n" + 
				"LIMIT 200\n" + 
				"";
		Query query = QueryFactory.create(queryLinks);
		
		QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/clinvar/sparql", queryLinks);
		queryExec.addParam("timeout","3600000");

		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode stitchIdNode = solution.get("gene");
			RDFNode diseaseNode = solution.get("disease");
			this.addDisease(diseaseNode.toString());
		};
		queryExec.close();
	}
	
	public ArrayList<GeneDiseasePair> getGeneDiseasesPairs() {
		String gene = this.gene.getEntrez_id();
		ArrayList<GeneDiseasePair> geneDiseasePairs = new ArrayList<GeneDiseasePair>();
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
				"    FILTER regex(?gene, \""+gene+"\")\n" + 
				"    BIND((replace(str(?gene_uri), \"http://identifiers.org/ncbigene/\", \"\")) AS ?gene)\n" + 
				"    BIND((replace(str(?disease_uri), \"http://linkedlifedata.com/resource/umls/id/\", \"\")) AS ?disease)\n" + 
				"}\n" + 
				"";
		Query query = QueryFactory.create(queryLinks);
		
		QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/clinvar/sparql", queryLinks);
		queryExec.addParam("timeout","3600000");

		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode diseaseNode = solution.get("disease");
			RDFNode twoHopsLinksNode = solution.get("2_hops_links1");
			Disease disease = new Disease(diseaseNode.toString());
			GeneDiseasePair geneDiseasePair = new GeneDiseasePair(this.getGene(),disease);
			//geneDiseasePair.addTwoHopsLink(twoHopsLinksNode.toString());
			geneDiseasePairs.add(geneDiseasePair);
			
		};
		queryExec.close();
		return geneDiseasePairs;
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}
	
	public ArrayList<String> getDiseases() {
		return diseases;
	}

	public void setDiseases(ArrayList<String> disease) {
		this.diseases = disease;
	}
	
	public void addDisease(String disease) {
		this.diseases.add(disease);
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	@Override
	public String toString() {
		return "DrugGenePair [drug=" + drug + ", gene=" + gene + ", diseases =" + diseases +"]";
	}
	
	public static String stripURI(String in) {
		return in.replaceAll("(.*)/", "");
	}; 
	
	public static LinkedList<DrugGenePair> getAssociatedPairs() {
		String linksQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"				 \n" + 
				"				 \n" + 
				"				SELECT ?gene ?association ?drug\n" + 
				"				WHERE {  \n" + 
				"				?gene rdf:type  <http://pharmgkb.org/relationships/Gene>. \n" + 
				"				?drug rdf:type <http://pharmgkb.org/relationships/Drug>. \n" + 
				"				?gene <http://pharmgkb.org/relationships/association> ?association. \n" + 
				"				?drug <http://pharmgkb.org/relationships/association> ?association. \n" + 
				"				?association <http://pharmgkb.org/relationships/association_type> \"associated\"\n" + 
				"				}";
		
		QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/pharmgkbrelations/sparql", linksQuery);
		queryExec.addParam("timeout","3600000");
		ResultSet results = queryExec.execSelect();
		LinkedList<DrugGenePair> pairs = new LinkedList<DrugGenePair>();
		
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode drugNode = solution.get("drug");
			Gene gene = new Gene(stripURI(geneNode.toString()));
			Drug drug = new Drug(stripURI(drugNode.toString()));
			DrugGenePair drugGenePair = new DrugGenePair(gene, drug);
			pairs.add(drugGenePair);	
		}
		
		return pairs;
		
	}
}
