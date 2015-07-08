package linkMaker;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class Main {
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
		Query query = QueryFactory.create(linksQuery);
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/pharmgkbrelations/sparql", linksQuery);
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
	
	public static void main(String[] args) {
		LinkedList<DrugGenePair> pairs = getAssociatedPairs();
		HashMap<String,String> drugStitchLinks = Drug.getPharmgkbIDStitchIDLinks();
		HashMap<String,String> geneEntrezLinks = Gene.getPharmgkbIDEntrezIDLinks();
		Iterator iterator = pairs.iterator();
		while(iterator.hasNext()) {
		 DrugGenePair pair = (DrugGenePair) iterator.next();
		 Gene gene = pair.getGene();
		 Drug drug = pair.getDrug();
		 gene.setEntrez_id(geneEntrezLinks.get(gene.getPharmgkb_id()));
		}
		System.out.println(pairs.get(0));
		
	}

	
}
