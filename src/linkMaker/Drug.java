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
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class Drug {
	String pharmgkb_id;
	ArrayList<String> stitch_ids; //Multiple stich_id for one pharmgkb_id : multiple salts
	ArrayList<String> attributes;
	ArrayList<String> linked_diseases;
	 
	 public static HashMap<Integer,ArrayList<String>> getStitchID_disease_links() {
			HashMap<Integer,ArrayList<String>> links = new HashMap<Integer, ArrayList<String>>();
			String queryLinks = "SELECT ?stitch_id ?2_hops_links_2\n" + 
					"WHERE {\n" + 
					"  {\n" + 
					"   	?a <http://bio2rdf.org/sider_vocabulary:stitch-flat-compound-id> ?stitch_id_uri.\n" + 
					"    ?a <http://bio2rdf.org/sider_vocabulary:side-effect> ?2_hops_links_2_uri\n" + 
					"      BIND(REPLACE(str(?stitch_id_uri), \"http://bio2rdf.org/stitch:\",\"\") AS ?stitch_id)\n" + 
					"      BIND(REPLACE(str(?2_hops_links_2_uri), \"http://bio2rdf.org/umls:\",\"\") AS ?2_hops_links_2)\n" + 
					"  }\n" + 
					"  UNION {\n" + 
					"   ?a <http://bio2rdf.org/sider_vocabulary:stitch-flat-compound-id> ?stitch_id_uri.\n" + 
					"   ?a <http://bio2rdf.org/sider_vocabulary:indication> ?2_hops_links_2_uri\n" + 
					"      BIND(REPLACE(str(?stitch_id_uri), \"http://bio2rdf.org/stitch:\",\"\") AS ?stitch_id)\n" + 
					"      BIND(REPLACE(str(?2_hops_links_2_uri), \"http://bio2rdf.org/umls:\",\"\") AS ?2_hops_links_2)\n" + 
					"  }\n" + 
					"}";
			Query query = QueryFactory.create(queryLinks);
			
			QueryEngineHTTP queryExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://cassandra.kevindalleau.fr/sider/sparql", queryLinks);
			queryExec.addParam("timeout","3600000");

			ResultSet results = queryExec.execSelect();
			while(results.hasNext()) {
				QuerySolution solution = results.nextSolution();
				RDFNode stitchIdNode = solution.get("stitch_id");
				RDFNode diseaseNode = solution.get("2_hops_links_2");
				if(links.get(stitchIdNode.toString()) != null) {
					links.get(stitchIdNode.toString()).add(diseaseNode.toString());
				}
				else {
					ArrayList<String> diseases = new ArrayList<String>();
					diseases.add(diseaseNode.toString());
					links.put(Math.abs(Integer.parseInt(stitchIdNode.toString()))-100000000, diseases);
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