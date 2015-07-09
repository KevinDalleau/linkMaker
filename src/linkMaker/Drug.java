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
	ArrayList<String> attributes;
	ArrayList<String> linked_diseases;

	 public static HashMap<String,ArrayList<String>> getPharmgkbIDStitchIDLinks() {
		HashMap<String,ArrayList<String>> links = new HashMap<String, ArrayList<String>>();
		String queryLinks = "SELECT DISTINCT ?drug ?stitch_id\n" + 
				"		WHERE {\n" + 
				"		?drug_uri <http://biodb.jp/mappings/to_c_id> ?stitch_id_uri.\n" + 
				"		FILTER regex(str(?drug_uri), \"^http://biodb.jp/mappings/pharmgkb_id/\")\n" + 
				"		BIND(REPLACE(str(?drug_uri), \"^http://biodb.jp/mappings/pharmgkb_id/\",\"\") AS ?drug)\n" + 
				"		BIND(REPLACE(str(?stitch_id_uri), \"^http://biodb.jp/mappings/c_id/\",\"\") AS ?stitch_id)\n" + 
				"		}\n" + 
				"";
		
		Query query = QueryFactory.create(queryLinks);
		QueryExecution queryExec = QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/mappings/sparql", queryLinks);
		ResultSet results = queryExec.execSelect();
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			RDFNode drugNode = solution.get("drug");
			RDFNode stitchIdNode = solution.get("stitch_id");
			if(links.get(drugNode.toString()) != null) {
				links.get(drugNode.toString()).add(stitchIdNode.toString());
			}
			else {
				ArrayList<String> stitchIds = new ArrayList<String>();
				stitchIds.add(stitchIdNode.toString());
				links.put(drugNode.toString(), stitchIds);
			}
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
		return "Drug [pharmgkb_id=" + pharmgkb_id + ", stitch_ids="
				+ stitch_ids + ", attributes=" + attributes
				+ ", linked_diseases=" + linked_diseases + "]";
	}
	
	

}