package linkMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class Gene {
 String pharmgkb_id;
 String entrez_id;
 ArrayList<String> attributes;
 ArrayList<String> linked_diseases;

 public static HashMap<String,String> getPharmgkbIDEntrezIDLinks() {
		HashMap<String,String> links = new HashMap<String, String>();
		String queryLinks = "SELECT ?gene ?entrez_id\n" + 
				"WHERE {\n" + 
				" ?gene_uri <http://biodb.jp/mappings/to_entrez_id> ?entrez_id_uri.\n" + 
				" FILTER regex(str(?gene_uri), \"^http://biodb.jp/mappings/pharmgkb_id/\")\n" + 
				" BIND(REPLACE(str(?gene_uri), \"^http://biodb.jp/mappings/pharmgkb_id/\",\"\") AS ?gene)\n" + 
				" BIND(REPLACE(str(?entrez_id_uri), \"^http://biodb.jp/mappings/entrez_id/\",\"\") AS ?entrez_id)\n" + 
				"}\n" + 
				"";
		
		Query query = QueryFactory.create(queryLinks);
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/mappings/sparql", queryLinks);
		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode entrezIdNode = solution.get("entrez_id");
			links.put(geneNode.toString(), entrezIdNode.toString());
		};
		queryExec.close();
		return links;
	 	}
 
 public static HashMap<String, ArrayList<String>> getGeneAttributes() {
	 HashMap<String,ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();
		String queryLinks = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"			PREFIX  foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"			PREFIX  ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>\n" + 
				"			PREFIX  sio:  <http://semanticscience.org/resource/>\n" + 
				"			PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\n" + 
				"			PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n" + 
				"			PREFIX  wp:   <http://vocabularies.wikipathways.org/wp#>\n" + 
				"			PREFIX  void: <http://rdfs.org/ns/void#>\n" + 
				"			PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"			PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"			PREFIX  dcterms: <http://purl.org/dc/terms/>\n" + 
				"			SELECT DISTINCT ?gene ?attributes\n" + 
				"			WHERE {\n" + 
				"			 {\n" + 
				"			  ?gda sio:SIO_000253 ?source . \n" + 
				"			   ?source rdfs:label ?originalSource .\n" + 
				"			   ?source rdfs:comment ?curation\n" + 
				"			   FILTER regex(?curation, \"CURATED\").\n" + 
				"			   ?gda sio:SIO_000628 ?gene_uri.\n" + 
				"			   ?gene_uri rdf:type ncit:C16612 .\n" + 
				"			  	?gene_uri sio:SIO_000062 ?pathway_id .\n" + 
				"			  	?pathway_id foaf:name ?attributes .\n" + 
				"    BIND(REPLACE(str(?gene_uri), \"http://identifiers.org/ncbigene/\", \"\") AS ?gene)\n" + 
				"			 }\n" + 
				"\n" + 
				"			 UNION {\n" + 
				"			?gda sio:SIO_000253 ?source .\n" + 
				"			   ?source rdfs:label ?originalSource .\n" + 
				"			   ?source rdfs:comment ?curation\n" + 
				"			   FILTER regex(?curation, \"CURATED\").\n" + 
				"			   ?gda sio:SIO_000628 ?gene_uri .\n" + 
				"			   ?gene_uri rdf:type ncit:C16612 .\n" + 
				"			  	?gene_uri sio:SIO_000095 ?class_id .\n" + 
				"			   ?class_id foaf:name ?attributes.\n" + 
				"        BIND(REPLACE(str(?gene_uri), \"http://identifiers.org/ncbigene/\", \"\") AS ?gene)\n" + 
				"			}\n" + 
				"			 }\n" + 
				"";
		
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/disgenet/sparql", queryLinks);
		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode attributesNode = solution.get("attributes");
			if(attributes.get(geneNode.toString()) != null) {
				attributes.get(geneNode.toString()).add(attributesNode.toString());
			}
			else {
				ArrayList<String> attributesIds = new ArrayList<String>();
				attributesIds.add(attributesNode.toString());
				attributes.put(geneNode.toString(), attributesIds);
			}
		};
		queryExec.close();
		return attributes;
 }
 public Gene(String pharmgkb_id) {
	this.pharmgkb_id = pharmgkb_id;
	this.entrez_id = "";
	attributes = new ArrayList<String>();
	linked_diseases = new ArrayList<String>();
	
 }

public String getPharmgkb_id() {
	return pharmgkb_id;
}

public void setPharmgkb_id(String pharmgkb_id) {
	this.pharmgkb_id = pharmgkb_id;
}

public String getEntrez_id() {
	return entrez_id;
}

public void setEntrez_id(String entrez_id) {
	this.entrez_id = entrez_id;
}

public ArrayList<String> getAttributes() {
	return attributes;
}

public void setAttributes(ArrayList<String> attributes) {
	this.attributes = attributes;
}

public ArrayList<String> getLinked_diseases() {
	return linked_diseases;
}

public void setLinked_diseases(ArrayList<String> linked_diseases) {
	this.linked_diseases = linked_diseases;
}

@Override
public String toString() {
	return "Gene [pharmgkb_id=" + pharmgkb_id + ", entrez_id=" + entrez_id
			+ ", attributes=" + attributes + ", linked_diseases="
			+ linked_diseases + "]";
}

}