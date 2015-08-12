package linkMaker;

import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import fr.kevindalleau.Mapper.Mapper;

public class Query {
	
	private String stitchValues;
	
	public ResultSet getAtcCodes(Drug drug) {
		
		if(drug.getPharmgkb_id() !=null) {
			String pharmgkb_id = drug.getPharmgkb_id();
			String queryLinks = "PREFIX void: <http://rdfs.org/ns/void#>\n" + 
					"			PREFIX dv: <http://bio2rdf.org/bio2rdf.dataset_vocabulary:>\n" + 
					"			SELECT ?atc\n" + 
					"			WHERE {\n" + 
					"			<http://bio2rdf.org/pharmgkb:"+pharmgkb_id+"> <http://bio2rdf.org/pharmgkb_vocabulary:x-atc> ?atc_uri.\n" + 
					"			BIND(replace(str(?atc_uri), \"http://bio2rdf.org/atc:\", \"\") AS ?atc)\n" + 
					"			}";
			QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://pharmgkb.bio2rdf.org/sparql", queryLinks);
			queryExec.addParam("timeout","3600000");
			return queryExec.execSelect();
			
		}

		else return null;
		}
	
	public ResultSet getTargets(Drug drug) {
		if(drug.getDrugbank_id() !=null) {
			String drugbank_id = drug.getDrugbank_id();
			String queryLinks = "PREFIX http: <http://www.w3.org/2011/http#>\n" + 
					"prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
					"SELECT DISTINCT ?target\n" + 
					"WHERE {\n" + 
					"<http://bio2rdf.org/drugbank:DB00318> <http://bio2rdf.org/drugbank_vocabulary:target> ?target_uri\n" + 
					"BIND(replace(str(?target_uri), \"http://bio2rdf.org/drugbank:\",\"\") AS ?target)\n" + 
					"}";
			QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr:/drugbank/sparql", queryLinks);
			queryExec.addParam("timeout","3600000");
			return queryExec.execSelect();
			
		}

		else return null;
		}
	

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
	public ResultSet getDrugDiseaseRelationsFromSider(Drug drug) {
		if(drug.getStitch_ids() !=null) {
			ArrayList<String> stitchIds = drug.getStitch_ids();
			stitchValues = "";
			for(int i=0; i < stitchIds.size(); i++) {
				stitchValues += "(<http://bio2rdf.org/stitch:-".concat(Integer.toString((Integer.parseInt(stitchIds.get(i))+100000000))).concat(">)");
			}
			
			String queryLinks = "SELECT ?stitch_id ?2_hops_links_2 ?disease\n" + 
					"WHERE {\n" + 
					"  {\n" + 
					"  VALUES(?stitch_id_uri) {\n" + 
					stitchValues +
					"    }\n" + 
					"  ?a <http://bio2rdf.org/sider_vocabulary:stitch-flat-compound-id> ?stitch_id_uri.\n" + 
					"  ?a <http://bio2rdf.org/sider_vocabulary:side-effect> ?disease_uri\n" + 
					"  BIND(\"side-effect\" AS ?2_hops_links_2)\n" + 
					"  BIND(REPLACE(str(?stitch_id_uri), \"http://bio2rdf.org/stitch:\",\"\") AS ?stitch_id)\n" + 
					"  BIND(REPLACE(str(?disease_uri), \"http://bio2rdf.org/umls:\",\"\") AS ?disease)\n" + 
					"  }\n" + 
					"  UNION {\n" + 
					"     VALUES(?stitch_id_uri) {\n" + 
					stitchValues +
					"     }\n" + 
					"  ?a <http://bio2rdf.org/sider_vocabulary:stitch-flat-compound-id> ?stitch_id_uri.\n" + 
					"  ?a <http://bio2rdf.org/sider_vocabulary:indication> ?disease_uri.\n" + 
					"  BIND(\"indication\" AS ?2_hops_links_2)\n" + 
					"  BIND(REPLACE(str(?stitch_id_uri), \"http://bio2rdf.org/stitch:\",\"\") AS ?stitch_id)\n" + 
					"  BIND(REPLACE(str(?disease_uri), \"http://bio2rdf.org/umls:\",\"\") AS ?disease)\n" + 
					"  }}\n" + 
					"";
				QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/sider/sparql", queryLinks);
				queryExec.addParam("timeout","3600000");
				return queryExec.execSelect();
		}
		else return null;
		
	}
	
	public ResultSet getDrugDiseaseRelationsFromMedispan(Drug drug) {
		if(drug.getPharmgkb_id() !=null) {
			Mapper mapper = new Mapper();
			String cui = mapper.getUMLS_from_PharmGKB(drug.getPharmgkb_id());
			System.out.println(cui);
			if(cui != null) {
				String queryLinks = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n" + 
						"				SELECT ?drug ?2_hops_links2 ?disease  \n" + 
						"				WHERE {  \n" + 
						"  {\n" + 
						"				?drug_uri ?2_hops_links2_uri ?disease_uri.  \n" + 
						"				?drug_uri rdf:type <http://orpailleur.fr/medispan/drug>.\n" + 
						"    			?drug_uri <http://orpailleur.fr/medispan/indication> ?disease_uri.\n" + 
						"				?disease_uri rdf:type <http://orpailleur.fr/medispan/event>\n" + 
						"     			  BIND(\"indication\" AS ?2_hops_links2)\n" + 
						"    			  FILTER regex(str(?drug_uri), \"http://orpailleur.fr/medispan/"+cui+"\")\n" + 
						"				  BIND(REPLACE(str(?drug_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?drug)  \n" + 
						"    BIND(REPLACE(str(?disease_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?disease)  }\n" + 
						"  \n" + 
						"  UNION {\n" + 
						"  	?drug_uri ?2_hops_links2_uri ?disease_uri.  \n" + 
						"				?drug_uri rdf:type <http://orpailleur.fr/medispan/drug>.\n" + 
						"    			?drug_uri <http://orpailleur.fr/medispan/side_effet> ?disease_uri.\n" + 
						"				?disease_uri rdf:type <http://orpailleur.fr/medispan/event>\n" + 
						"      		      BIND(\"side_effect\" AS ?2_hops_links2)\n" + 
						"    			  FILTER regex(str(?drug_uri), \"http://orpailleur.fr/medispan/"+cui+"\")\n" + 
						"				  BIND(REPLACE(str(?drug_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?drug)  \n" + 
						"				  BIND(REPLACE(str(?disease_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?disease)  \n" + 
						"  }\n" + 
						"}\n" + 
						"";
					QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/medispan/sparql", queryLinks);
					queryExec.addParam("timeout","3600000");
					return queryExec.execSelect();
			}
			else return null;
		}

		else return null;
		
	}
	
	
	public ResultSet getDrugDiseaseRelations(String source) {
		if (source.equals("medispan")) {
			String queryLinks = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"SELECT ?drug ?2_hops_links2 ?disease\n" + 
				"WHERE {\n" + 
				"  ?drug_uri ?2_hops_links2_uri ?disease_uri.\n" + 
				"  ?drug_uri rdf:type <http://orpailleur.fr/medispan/drug>.\n" + 
				"  ?disease_uri rdf:type <http://orpailleur.fr/medispan/event>\n" + 
				"    BIND(REPLACE(str(?drug_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?drug)\n" + 
				"    BIND(REPLACE(str(?disease_uri), \"http://orpailleur.fr/medispan/\",\"\") AS ?disease)\n" + 
				"  BIND(REPLACE(str(?2_hops_links2_uri), \"http://orpailleur.fr/medispan/\", \"\") AS ?2_hops_links2)}\n";
			
			QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/medispan/sparql", queryLinks);
			queryExec.addParam("timeout","3600000");
			return queryExec.execSelect();
		}
		else if (source.equals("sider")) {
			String queryLinks = "SELECT ?stitch_id ?2_hops_links_2 ?disease\n" + 
					"					WHERE { \n" + 
					"					  { \n" + 
					"					   	?a <http://bio2rdf.org/sider_vocabulary:stitch-flat-compound-id> ?stitch_id_uri. \n" + 
					"					    ?a <http://bio2rdf.org/sider_vocabulary:side-effect> ?disease_uri\n" + 
					"     					  BIND(\"side-effect\" AS ?2_hops_links_2)\n" + 
					"					      BIND(REPLACE(str(?stitch_id_uri), \"http://bio2rdf.org/stitch:\",\"\") AS ?stitch_id) \n" + 
					"					      BIND(REPLACE(str(?disease_uri), \"http://bio2rdf.org/umls:\",\"\") AS ?disease) \n" + 
					"					  } \n" + 
					"					  UNION { \n" + 
					"					   ?a <http://bio2rdf.org/sider_vocabulary:stitch-flat-compound-id> ?stitch_id_uri. \n" + 
					"					   ?a <http://bio2rdf.org/sider_vocabulary:indication> ?disease_uri\n" + 
					"      					  BIND(\"indication\" AS ?2_hops_links_2)\n" + 
					"					      BIND(REPLACE(str(?stitch_id_uri), \"http://bio2rdf.org/stitch:\",\"\") AS ?stitch_id) \n" + 
					"    					  BIND(REPLACE(str(?disease_uri), \"http://bio2rdf.org/umls:\",\"\") AS ?disease) \n" + 
					"					  } \n" + 
					"					}\n";
			
			QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/sider/sparql", queryLinks);
			queryExec.addParam("timeout","3600000");
			return queryExec.execSelect();
			
		}
		else {
			return null;
		}
	}

}
