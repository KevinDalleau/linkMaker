package linkMaker;

import java.io.Serializable;
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

public class Drug implements Serializable{
	String pharmgkb_id;
	String drugbank_id;
	ArrayList<String> stitch_ids; //Multiple stich_id for one pharmgkb_id : multiple salts
	ArrayList<String> attributes;
	ArrayList<String> targets;
	 
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
	 
	 @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((pharmgkb_id == null) ? 0 : pharmgkb_id.hashCode());
		result = prime * result + ((stitch_ids == null) ? 0 : stitch_ids.hashCode());
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
		Drug other = (Drug) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (pharmgkb_id == null) {
			if (other.pharmgkb_id != null)
				return false;
		} else if (!pharmgkb_id.equals(other.pharmgkb_id))
			return false;
		if (stitch_ids == null) {
			if (other.stitch_ids != null)
				return false;
		} else if (!stitch_ids.equals(other.stitch_ids))
			return false;
		return true;
	}

	public Drug(String pharmgkb_id) {
		this.pharmgkb_id = pharmgkb_id;
		this.stitch_ids = null;;
		attributes = new ArrayList<String>();
		targets = new ArrayList<String>();
		
	 }
	 
	 public Drug() {
		 this.pharmgkb_id = "";
		 this.stitch_ids = null;
		 attributes = new ArrayList<String>();
		 targets = new ArrayList<String>();
	 }

	public String getPharmgkb_id() {
		return pharmgkb_id;
	}
	
	public void setATC() {
		linkMaker.Query query = new linkMaker.Query();
		if(query.getAtcCodes(this) != null) {
			ResultSet atc = query.getAtcCodes(this);
			while(atc.hasNext()) {
				QuerySolution solution = atc.nextSolution();
				RDFNode atcCode = solution.get("atc");
				if(atcCode != null) {
					this.addAttribute(atcCode.toString().substring(0, 3));
				}
			}
			
		}
		
	}
	
	public void setTargets() {
		linkMaker.Query query = new linkMaker.Query();
		if(query.getTargets(this) != null) {
			ResultSet targets = query.getTargets(this);
			while(targets.hasNext()) {
				QuerySolution solution = targets.nextSolution();
				RDFNode targetsNode = solution.get("target");
				if(targetsNode != null) {
					this.addTarget(targetsNode.toString());
				}
			}
		}
		
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
	
	public void addAttribute(String attribute) {
		this.attributes.add(attribute);
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}


	@Override
	public String toString() {
		return "Drug [pharmgkb_id=" + pharmgkb_id + ", drugbank_id="
				+ drugbank_id + ", stitch_ids=" + stitch_ids + ", attributes="
				+ attributes + ", targets=" + targets + "]";
	}

	public String getDrugbank_id() {
		return drugbank_id;
	}

	public void setDrugbank_id(String drugbank_id) {
		this.drugbank_id = drugbank_id;
	}

	public ArrayList<String> getTargets() {
		return targets;
	}

	public void setTargets(ArrayList<String> targets) {
		this.targets = targets;
	}
	
	public void addTarget(String target) {
		this.targets.add(target);
	}
	
	

}