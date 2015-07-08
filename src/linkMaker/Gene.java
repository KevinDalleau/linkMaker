package linkMaker;

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
 HashSet<String> attributes;
 HashSet<String> linked_diseases;

 public static HashMap<String,String> getPharmgkbIDEntrezIDLinks() {
		HashMap<String,String> links = new HashMap<String, String>();
		String queryLinks = "SELECT ?gene ?entrez_id\n" + 
				"	WHERE\n" + 
				"   {\n" + 
				"	?gene <http://biodb.jp/mappings/to_entrez_id> ?entrez_id.\n" + 
				"   FILTER regex(str(?gene), \"^http://biodb.jp/mappings/pharmgkb_id/\")";
		
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
 
 
 public Gene(String pharmgkb_id) {
	this.pharmgkb_id = pharmgkb_id;
	this.entrez_id = entrez_id;
	attributes = new HashSet<String>();
	linked_diseases = new HashSet<String>();
	
 }

public HashSet<String> getAttributes() {
	return attributes;
}

public void addAttribute(String attribute) {
	this.attributes.add(attribute);
}

private String getEntrezId() {
	String entrez_id="";
	String uri = "<http://biodb.jp/mappings/pharmgkb_id/"+this.pharmgkb_id+">";
	String queryGeneAttributes = "SELECT ?entrez_id\n" + 
			"WHERE {\n" + 
			uri+" <http://biodb.jp/mappings/to_entrez_id> ?entrez_id\n" + 
			"}\n";
	Query query = QueryFactory.create(queryGeneAttributes);
	QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/disgenet/sparql", queryGeneAttributes);
	ResultSet results = queryExec.execSelect();
	while(results.hasNext()) {
		QuerySolution solution = results.nextSolution();
		RDFNode entrezIdNode = solution.get("entrez_id");
		entrez_id = entrezIdNode.toString();
	}
	queryExec.close();
	return entrez_id;
	
}

public void setAttributes() {
	String queryGeneAttributes = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
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
			"SELECT DISTINCT ?attributes\n" + 
			"WHERE {\n" + 
			"  {\n" + 
			"   ?gda sio:SIO_000253 ?source .\n" + 
			"    ?source rdfs:label ?originalSource .\n" + 
			"    ?source rdfs:comment ?curation\n" + 
			"    FILTER regex(?curation, \"CURATED\").\n" + 
			"    FILTER regex(str(?gene),\"http://identifiers.org/ncbigene/1292\").\n" + 
			"    ?gda sio:SIO_000628 ?gene .\n" + 
			"    ?gene rdf:type ncit:C16612 .\n" + 
			"   	?gene sio:SIO_000062 ?pathway_id .\n" + 
			"   	?pathway_id foaf:name ?attributes .\n" + 
			"  }\n" + 
			"  \n" + 
			"  UNION {\n" + 
			"	?gda sio:SIO_000253 ?source .\n" + 
			"    ?source rdfs:label ?originalSource .\n" + 
			"    ?source rdfs:comment ?curation\n" + 
			"    FILTER regex(?curation, \"CURATED\").\n" + 
			"    FILTER regex(str(?gene),\"http://identifiers.org/ncbigene/1292\").\n" + 
			"    ?gda sio:SIO_000628 ?gene .\n" + 
			"    ?gene rdf:type ncit:C16612 .\n" + 
			"   	?gene sio:SIO_000095 ?class_id .\n" + 
			"    ?class_id foaf:name ?attributes.\n" + 
			"}\n" + 
			"  }\n" + 
			"\n" + 
			"   ";
	Query query = QueryFactory.create(queryGeneAttributes);
	QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/disgenet/sparql", queryGeneAttributes);
	ResultSet results = queryExec.execSelect();
	while(results.hasNext()) {
		QuerySolution solution = results.nextSolution();
		RDFNode attributesNode = solution.get("attributes");
		this.addAttribute(attributesNode.toString());
	}
	queryExec.close();
}

public HashSet<String> getLinked_diseases() {
	return linked_diseases;
}

public void addLinkedDiseases(String linked_disease) {
	this.linked_diseases.add(linked_disease);
}

@Override
public String toString() {
	return "Gene [pharmgkb_id=" + pharmgkb_id + ", entrez_id=" + entrez_id + ", attributes=" + attributes
			+ ", linked_diseases=" + linked_diseases + "]";
}
}
