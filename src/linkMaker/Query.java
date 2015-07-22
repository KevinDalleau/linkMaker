package linkMaker;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class Query {
	
	public ResultSet getGeneDiseaseRelations(String source) {
		if (source.equals("disgenet")) {
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
			return queryExec.execSelect();
		}
		else if (source.equals("clinvar")) {
			String queryLinks = "SELECT DISTINCT ?gene ?disease ?2_hops_links1\n" + 
					"WHERE {\n" + 
					"{\n" + 
					"?a <http://bio2rdf.org/clinvar_vocabulary:assertion> ?2_hops_links1 .\n" + 
					"?a <http://bio2rdf.org/clinvar_vocabulary:Variant_Gene> ?variant_gene .\n" + 
					"?variant_gene <http://bio2rdf.org/clinvar_vocabulary:x-gene> ?gene_bio .\n" + 
					"?a <http://bio2rdf.org/clinvar_vocabulary:Variant_Phenotype> ?phenotype .\n" + 
					"?phenotype <http://bio2rdf.org/clinvar_vocabulary:x-medgen> ?disease_bio\n" + 
					"      BIND(REPLACE(str(?gene_bio), \"http://bio2rdf.org/ncbigene:\", \"\") AS ?gene)\n" + 
					"      BIND(REPLACE(str(?disease_bio), \"http://bio2rdf.org/medgen:c\",\"C\") AS ?disease)\n" + 
					"}\n" + 
					"\n" + 
					"UNION {\n" + 
					"?a <http://bio2rdf.org/clinvar_vocabulary:Variant_Gene> ?variant_gene .\n" + 
					"?variant_gene <http://bio2rdf.org/clinvar_vocabulary:x-gene> ?gene_bio .\n" + 
					"?variant_gene <http://bio2rdf.org/clinvar_vocabulary:x-sequence_ontology> ?2_hops_links1 .\n" + 
					"?a <http://bio2rdf.org/clinvar_vocabulary:Variant_Phenotype> ?phenotype .\n" + 
					"?phenotype <http://bio2rdf.org/clinvar_vocabulary:x-medgen> ?disease_bio\n" + 
					"      BIND(REPLACE(str(?gene_bio), \"http://bio2rdf.org/ncbigene:\", \"\") AS ?gene)\n" + 
					"      BIND(REPLACE(str(?disease_bio), \"http://bio2rdf.org/medgen:c\",\"C\") AS ?disease)\n" + 
					"}\n" + 
					"}\n" + 
					"";
			
			QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/clinvar/sparql", queryLinks);
			queryExec.addParam("timeout","3600000");
			return queryExec.execSelect();
			
		}
		else {
			return null;
		}
	}

}