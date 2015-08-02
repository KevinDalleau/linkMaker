package linkMaker;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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


public class Main implements Serializable{
		
	public static void main(String[] args) {
		
		Mapper mapper = new Mapper();
		LinkedList<DrugGenePair> pairs = DrugGenePair.getAssociatedPairs();
		File finalGeneDiseasePairsFile = new File("./finalGeneDiseasePairs.ser");
		File finalDrugDiseasePairsFile = new File("./finalDrugDiseasePairs.ser");
		HashSet<GeneDiseasePair> finalGeneDiseasePairs = new HashSet<GeneDiseasePair>();
		HashSet<DrugDiseasePair> finalDrugDiseasePairs = new HashSet<DrugDiseasePair>();
		Boolean pairStored = true;
		
		if(finalGeneDiseasePairsFile.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(finalGeneDiseasePairsFile));
				finalGeneDiseasePairs = (HashSet<GeneDiseasePair>) ois.readObject();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			pairStored=false;
		}
		
		if(finalDrugDiseasePairsFile.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(finalDrugDiseasePairsFile));
				finalDrugDiseasePairs = (HashSet<DrugDiseasePair>) ois.readObject();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			pairStored=false;
		}
		
		System.out.println("Number of known pairs : "+ pairs.size());
		if(!pairStored) {
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
			 drug.setStitch_ids(mapper.getStitch_from_PharmGKB(drug.getPharmgkb_id()));
			 ArrayList<GeneDiseasePair> geneDiseasesPairs = geneDiseasePairs.get(gene.getEntrez_id());
			 if(geneDiseasesPairs != null) {
				 for(GeneDiseasePair gdpair : geneDiseasesPairs) {
					 gdpair.getGene().setAttributes(geneAttributes.get(gdpair.getGene().getEntrez_id()));
				 } 
			 }
			 
			 if(geneDiseasesPairs != null) {
				 finalGeneDiseasePairs.addAll(geneDiseasesPairs);
			 }
			 ArrayList<DrugDiseasePair> drugDiseasesPairs = DrugDiseasePair.getDrugDiseasesPairs(drug);
			 if(drugDiseasesPairs != null) {
				 finalDrugDiseasePairs.addAll(drugDiseasesPairs);
			 }
			}
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(finalGeneDiseasePairsFile));
				oos.writeObject(finalGeneDiseasePairs);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(finalDrugDiseasePairsFile));
				oos.writeObject(finalDrugDiseasePairs);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		int finalGeneDiseasePairsSize = finalGeneDiseasePairs.size();
		int finalDrugDiseasePairsSize = finalDrugDiseasePairs.size();
		System.out.println(finalGeneDiseasePairsSize+ " gene-disease pairs found");
		System.out.println(finalDrugDiseasePairsSize+ " drug-disease pairs found");
		Object[] geneDiseasePairsArray = finalGeneDiseasePairs.toArray();
		Object[] drugDiseasePairsArray = finalDrugDiseasePairs.toArray();
		
		for(int i = 0;i<finalGeneDiseasePairsSize;i++) {
			GeneDiseasePair pair = (GeneDiseasePair) geneDiseasePairsArray[i];
			System.out.println(pair.getDisease().getCui());
			
			for(int j=0;j<finalDrugDiseasePairsSize;j++) {
				if (pair.getDisease().getCui().equals(((DrugDiseasePair) drugDiseasePairsArray[j]).getDisease().getCui())) {
					System.out.println("Correspondance trouvée entre "+pair.getDisease().getCui()+ " et "+((DrugDiseasePair) drugDiseasePairsArray[j]).getDisease().getCui());
					System.out.println(pair.toString());
				}
			}
			
		}
		
		 
	}
	
	
		
}
