package linkMaker;

import java.util.ArrayList;

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
	public static void main(String[] args) {
		String linksQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"\n" + 
				"\n" + 
				"SELECT ?gene ?association ?drug\n" + 
				"WHERE {\n" + 
				"  ?gene rdf:type  <http://pharmgkb.org/relationships/Gene>.\n" + 
				"  ?drug rdf:type <http://pharmgkb.org/relationships/Drug>.\n" + 
				"  ?gene <http://pharmgkb.org/relationships/association> ?association.\n" + 
				"  ?drug <http://pharmgkb.org/relationships/association> ?association.\n" + 
				"  ?association <http://pharmgkb.org/relationships/association_type> \"associated\"\n" + 
				"}";
		Query query = QueryFactory.create(linksQuery);
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/pharmgkbrelations/sparql", linksQuery);
		ResultSet results = queryExec.execSelect();
		ArrayList<DrugGenePair> pairs = new ArrayList<DrugGenePair>();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode drugNode = solution.get("drug");
			Gene gene = new Gene(stripURI(geneNode.toString()));
			Drug drug = new Drug(stripURI(drugNode.toString()));
			DrugGenePair drugGenePair = new DrugGenePair(gene, drug);
			pairs.add(drugGenePair);
		}
		System.out.println(pairs.get(2));
	}

	
}
