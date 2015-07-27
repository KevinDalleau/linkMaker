package linkMaker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import fr.kevindalleau.Mapper.Mapper;


public class Main {
		
	public static void main(String[] args) {
		
		Mapper mapper = new Mapper();
		LinkedList<DrugGenePair> pairs = DrugGenePair.getAssociatedPairs();
		HashSet<GeneDiseasePair> finalGeneDiseasePairs = new HashSet<GeneDiseasePair>();
		HashSet<DrugDiseasePair> finalDrugDiseasePairs = new HashSet<DrugDiseasePair>();
		System.out.println("Number of known pairs : "+ pairs.size());
		
		HashMap<String,ArrayList<GeneDiseasePair>> geneDiseasePairs = GeneDiseasePair.getGeneDiseasesPairs();
		HashMap<String,String> geneEntrezLinks = Gene.getPharmgkbIDEntrezIDLinks();
		HashMap<String,ArrayList<String>> geneAttributes = Gene.getGeneAttributes();
		Iterator<DrugGenePair> iterator = pairs.iterator();
		int i = 0;
		while(iterator.hasNext()) {
		 System.out.println("Pair number "+i);
		 i++;
		 DrugGenePair pair = iterator.next();
		 Gene gene = pair.getGene();
		 Drug drug = pair.getDrug();
		 gene.setEntrez_id(geneEntrezLinks.get(gene.getPharmgkb_id()));
		 gene.setAttributes(geneAttributes.get(gene.getEntrez_id()));
		 drug.setStitch_ids(mapper.getStitch_from_PharmGKB(drug.getPharmgkb_id()));
		 ArrayList<GeneDiseasePair> geneDiseasesPairs = geneDiseasePairs.get(gene.getEntrez_id());
		 if(geneDiseasesPairs != null) {
			 finalGeneDiseasePairs.addAll(geneDiseasesPairs);
		 }
		 ArrayList<DrugDiseasePair> drugDiseasesPairs = DrugDiseasePair.getDrugDiseasesPairs(drug);
		 if(drugDiseasesPairs != null) {
			 finalDrugDiseasePairs.addAll(drugDiseasesPairs);
		 }
		}
		System.out.println(finalGeneDiseasePairs.size()+ " gene-disease pairs found");
		System.out.println(finalDrugDiseasePairs.size()+ " drug-disease pairs found");
		 
	}
	
	
		
}
