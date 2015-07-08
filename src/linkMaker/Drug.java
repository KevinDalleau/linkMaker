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

public class Drug {
	String pharmgkb_id;
	ArrayList<String> stitch_ids; //Multiple stich_id for one pharmgkb_id : multiple salts
	HashSet<String> attributes;
	HashSet<String> linked_diseases;

	 public static HashMap<String,String> getPharmgkbIDStitchIDLinks() {
		HashMap<String,String> links = new HashMap<String, String>();
		String queryLinks = "SELECT DISTINCT ?drug ?stitch_id\n" + 
				"				WHERE \n" + 
				"   {\n" + 
				"	?drug <http://biodb.jp/mappings/to_c_id> ?stitch_id.\n" + 
				"  FILTER regex(str(?drug), \"^http://biodb.jp/mappings/pharmgkb_id/\")\n" + 
				"}\n" + 
				"\n" + 
				"";
		
		Query query = QueryFactory.create(queryLinks);
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/mappings/sparql", queryLinks);
		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode drugNode = solution.get("drug");
			RDFNode stitchIdNode = solution.get("stitch_id");
			links.put(drugNode.toString(), stitchIdNode.toString());
		};
		queryExec.close();
		return links;
	 	}
	 
	 public Drug(String pharmgkb_id) {
		this.pharmgkb_id = pharmgkb_id;
		this.stitch_ids = null;;
		attributes = null;
		linked_diseases = null;
		
	 }
	
	private ArrayList<String> setStitchId() {
			ArrayList<String> stitch_ids= new ArrayList<String>();
			String uri = "<http://biodb.jp/mappings/pharmgkb_id/"+this.pharmgkb_id+">";
			String queryGeneAttributes = "SELECT ?stitch_id\n" + 
					"WHERE {\n" + 
					uri+" <http://biodb.jp/mappings/to_c_id> ?stitch_id\n" + 
					"}\n";
			Query query = QueryFactory.create(queryGeneAttributes);
			QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/disgenet/sparql", queryGeneAttributes);
			ResultSet results = queryExec.execSelect();
			while(results.hasNext()) {
				QuerySolution solution = results.nextSolution();
				RDFNode stitchIdNode = solution.get("stitch_id");
				stitch_ids.add(stitchIdNode.toString());
			}
			queryExec.close();
			return stitch_ids;
			
		}
	public HashSet<String> getAttributes() {
		return attributes;
	}

	public void addAttribute(String attribute) {
		this.attributes.add(attribute);
	}

	public HashSet<String> getLinked_diseases() {
		return linked_diseases;
	}

	public void addLinkedDiseases(String linked_disease) {
		this.linked_diseases.add(linked_disease);
	}

	@Override
	public String toString() {
		return "Drug [pharmgkb_id=" + pharmgkb_id + ", attributes="
				+ attributes + ", linked_diseases=" + linked_diseases + "]";
	}

	public String getPharmgkb_id() {
		return pharmgkb_id;
	}

	public void setPharmgkb_id(String pharmgkb_id) {
		this.pharmgkb_id = pharmgkb_id;
	}

	public ArrayList<String> getStitch_ids() {
		return stitch_ids;
	}

	public void setStitch_ids(ArrayList<String> stitch_ids) {
		this.stitch_ids = stitch_ids;
	}

	public void setAttributes(HashSet<String> attributes) {
		this.attributes = attributes;
	}

	public void setLinked_diseases(HashSet<String> linked_diseases) {
		this.linked_diseases = linked_diseases;
	}
	}