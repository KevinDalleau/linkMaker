package linkMaker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class Disease implements Serializable{
	
	String cui;
	String pharmgkbid;
	ArrayList<String> attributes;
	
	public Disease(String cui) {
		this.cui = cui;
		this.pharmgkbid = "";
		this.attributes = null;
	}

	@Override
	public String toString() {
		return "Disease [cui=" + cui + ", pharmgkbid=" + pharmgkbid + ", attributes=" + attributes + "]";
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}
	
	public String getPharmgkbid() {
		return pharmgkbid;
	}
	
	public void setPharmgkbid(String pharmgkbId) {
		this.pharmgkbid = pharmgkbId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cui == null) ? 0 : cui.hashCode());
		result = prime * result + ((pharmgkbid == null) ? 0 : pharmgkbid.hashCode());
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
		Disease other = (Disease) obj;
		if (cui == null) {
			if (other.cui != null)
				return false;
		} else if (!cui.equals(other.cui))
			return false;
		if (pharmgkbid == null) {
			if (other.pharmgkbid != null)
				return false;
		} else if (!pharmgkbid.equals(other.pharmgkbid))
			return false;
		return true;
	}

	public ArrayList<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}
	
	public static HashMap<String, ArrayList<String>> getDiseaseAttributes() {
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
					"			SELECT DISTINCT ?disease ?attributes\n" + 
					"			WHERE {\n" + 
					"			 {\n" + 
					"			  ?gda sio:SIO_000253 ?source .\n" + 
					"			   ?source rdfs:label ?originalSource .\n" + 
					"			   ?source rdfs:comment ?curation\n" + 
					"			   FILTER regex(?curation, \"CURATED\").\n" + 
					"			   ?gda sio:SIO_000628 ?disease_uri.\n" + 
					"			   ?disease_uri rdf:type ncit:C7057 .\n" + 
					"			   ?disease_uri sio:SIO_000008 ?attributes .\n" + 
					"    BIND(REPLACE(str(?disease_uri), \"http://linkedlifedata.com/resource/umls/id/\",\"\") AS ?disease)\n" + 
					"			 }\n" + 
					"  UNION {\n" + 
					"   ?gda sio:SIO_000253 ?source .\n" + 
					"			   ?source rdfs:label ?originalSource .\n" + 
					"			   ?source rdfs:comment ?curation\n" + 
					"			   FILTER regex(?curation, \"CURATED\").\n" + 
					"			   ?gda sio:SIO_000628 ?disease_uri.\n" + 
					"			   ?disease_uri rdf:type ncit:C7057 .\n" + 
					"			   ?disease_uri sio:SIO_000095 ?attributes_uri .\n" + 
					"			   ?attributes_uri rdfs:isDefinedBy ?attributes.\n" + 
					"        BIND(REPLACE(str(?disease_uri), \"http://linkedlifedata.com/resource/umls/id/\",\"\") AS ?disease)\n" + 
					"  }\n" + 
					"}";
			
			QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/disgenet/sparql", queryLinks);
			ResultSet results = queryExec.execSelect();
			while(results.hasNext()) {
				QuerySolution solution = results.nextSolution();
				RDFNode diseaseNode = solution.get("disease");
				RDFNode attributesNode = solution.get("attributes");
				if(attributes.get(diseaseNode.toString()) != null) {
					attributes.get(diseaseNode.toString()).add(attributesNode.toString());
				}
				else {
					ArrayList<String> attributesIds = new ArrayList<String>();
					attributesIds.add(attributesNode.toString());
					attributes.put(diseaseNode.toString(), attributesIds);
				}
			};
			queryExec.close();
			return attributes;
	 }

}
