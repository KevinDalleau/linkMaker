package linkMaker;


import java.util.Iterator;
import java.util.LinkedHashSet;


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
	
	public static LinkedHashSet<DrugGenePair> getAssociatedPairs() {
		String linksQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"				 \n" + 
				"				 \n" + 
				"				SELECT ?gene ?gene_entrez ?association ?drug\n" + 
				"				WHERE {  \n" + 
				"  \n" + 
				"				?gene rdf:type  <http://pharmgkb.org/relationships/Gene>. \n" + 
				"				?drug rdf:type <http://pharmgkb.org/relationships/Drug>. \n" + 
				"				?gene <http://pharmgkb.org/relationships/association> ?association. \n" + 
				"				?drug <http://pharmgkb.org/relationships/association> ?association. \n" + 
				"				?association <http://pharmgkb.org/relationships/association_type> \"associated\"\n" + 
				"  				 BIND(uri(replace(str(?gene),\"^http://pharmgkb.org/relationships/\", \"http://biodb.jp/mappings/pharmgkb_id/\")) AS ?gene_mapper)\n" + 
				"\n" + 
				"    SERVICE <http://cassandra.kevindalleau.fr/mappings/sparql> {\n" + 
				"	?gene_mapper <http://biodb.jp/mappings/to_entrez_id> ?gene_entrez\n" + 
				"}\n" + 
				"  \n" + 
				"  \n" + 
				"  \n" + 
				"				}\n" + 
				"\n" + 
				"";
		Query query = QueryFactory.create(linksQuery);
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/pharmgkbrelations/sparql", linksQuery);
		ResultSet results = queryExec.execSelect();
		LinkedHashSet<DrugGenePair> pairs = new LinkedHashSet<DrugGenePair>();
		
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode geneNode = solution.get("gene");
			RDFNode geneEntrezNode = solution.get("gene_entrez");
			RDFNode drugNode = solution.get("drug");
			Gene gene = new Gene(stripURI(geneNode.toString()), stripURI(geneEntrezNode.toString()));
			Drug drug = new Drug(stripURI(drugNode.toString()));
			DrugGenePair drugGenePair = new DrugGenePair(gene, drug);
			pairs.add(drugGenePair);	
		}
		
		return pairs;
		
	}
	
	public static void main(String[] args) {
		LinkedHashSet<DrugGenePair> pairs = getAssociatedPairs();
		Iterator iterator = pairs.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		
	}

	
}
