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
import java.util.LinkedList;

import fr.kevindalleau.Mapper.Mapper;


public class Main implements Serializable{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		Mapper mapper = new Mapper();
		LinkedList<DrugGenePair> pairs = new LinkedList<DrugGenePair>();
		String typeOfAssociation = "";

		if(args.length == 0) {
			System.out.println("Veuillez passer un argument : linked, not_linked, specific gene_id drug_id");
			System.out.println("Linked supposed");
			args[0] = "linked";
		}
		else if(args[0].equalsIgnoreCase("linked")) {
			pairs = DrugGenePair.getAssociatedPairs();
			typeOfAssociation = "linked";

		}


		else if(args[0].equalsIgnoreCase("not_linked")) {
			pairs = DrugGenePair.getNotAssociatedPairs();
			typeOfAssociation = "not_linked";
		}

		else if(args[0].equalsIgnoreCase("specific")) {
			pairs = DrugGenePair.getSpecificPair(args[1], args[2]);
			typeOfAssociation = "specific";
		}

		File finalGeneDiseasePairsFile = new File("./finalGeneDiseasePairs_notlinked.ser");
		File finalDrugDiseasePairsFile = new File("./finalDrugDiseasePairs_notlinked.ser");
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
		//		if(!pairStored) {
		HashMap<String,ArrayList<GeneDiseasePair>> geneDiseasePairs = GeneDiseasePair.getGeneDiseasesPairs();
		HashMap<String,String> geneEntrezLinks = Gene.getPharmgkbIDEntrezIDLinks();
		HashMap<String,ArrayList<String>> geneAttributes = Gene.getGeneAttributes();
		HashMap<String,ArrayList<String>> diseaseAttributes = Disease.getDiseaseAttributes();
		Iterator<DrugGenePair> iterator = pairs.iterator();
		int i = 0;
		while(iterator.hasNext()) {
			System.out.println("Pair number "+i);
			i++;
			DrugGenePair pair = iterator.next();
			Gene gene = pair.getGene();
			Drug drug = pair.getDrug();
			gene.setEntrez_id(geneEntrezLinks.get(gene.getPharmgkb_id()));
			gene.setUniprot_id(mapper.getUniProt_from_PharmGKB(gene.getPharmgkb_id()));
			drug.setStitch_ids(mapper.getStitch_from_PharmGKB(drug.getPharmgkb_id()));
			drug.setDrugbank_id(mapper.getDrugbank_from_PharmGKB(drug.getPharmgkb_id()));
			drug.setTargets();
			pair.setOneHopsLinks();
			//drug.setATC();
			ArrayList<GeneDiseasePair> geneDiseasesPairs = geneDiseasePairs.get(gene.getEntrez_id());
			ArrayList<DrugDiseasePair> drugDiseasesPairs = DrugDiseasePair.getDrugDiseasesPairs(drug);
			
			if(geneDiseasesPairs != null && drugDiseasesPairs != null) {
				for(GeneDiseasePair gdpair : geneDiseasesPairs) {
					System.out.println("Gene : "+gdpair.getGene().getEntrez_id()+" Maladie : "+gdpair.getDisease().getCui());
					for(DrugDiseasePair ddpair : drugDiseasesPairs) {
						System.out.println("Drug : "+ddpair.getDrug().getPharmgkb_id()+" Disease :"+ddpair.getDisease().getCui());
						if (gdpair.getDisease().getCui().equals(ddpair.getDisease().getCui())) {
							System.out.println("WOAW : "+gdpair.getGene().getEntrez_id()+" et "+ddpair.getDrug().getPharmgkb_id()+" sont liés ! Le passage se fait par :"+ddpair.getDisease().getCui());
							ddpair.getDisease().setAttributes(diseaseAttributes.get(ddpair.getDisease().getCui()));
							gdpair.getGene().setAttributes(geneAttributes.get(gdpair.getGene().getEntrez_id()));
							gdpair.getDisease().setAttributes(diseaseAttributes.get(gdpair.getDisease().getCui()));
							Association association = new Association(pair,gdpair,ddpair);
							association.printAssociation(typeOfAssociation);
						}
					}
				}
			}				
		}
	}		
}
