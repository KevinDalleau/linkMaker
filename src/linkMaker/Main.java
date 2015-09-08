package linkMaker;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
		
		else if(args[0].equalsIgnoreCase("ambiguous")) {
			pairs = DrugGenePair.getAmbiguousPairs();
			typeOfAssociation = "ambiguous";
		}
		else if(args[0].equalsIgnoreCase("specific")) {
			//pairs = DrugGenePair.getSpecificPair(args[1], args[2]);
			Drug drug = new Drug(args[2]);
			Gene gene = new Gene(args[1]);
			pairs.add(new DrugGenePair(gene, drug));
			typeOfAssociation = "specific";
		}
		else if(args[0].equalsIgnoreCase("guessed_not_linked")) {
			try {
				pairs = DrugGenePair.getGuessedNotAssociatedPairs();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			typeOfAssociation = "guessed";
		}
		
		else if(args[0].equalsIgnoreCase("get_not_linked_list")) {
			try {
				pairs = DrugGenePair.getGuessedNotAssociatedPairs();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			typeOfAssociation = "not_linked_list";
		}
		
		else if(args[0].equalsIgnoreCase("file")) {
			if(args[1]!= null) {
				try {
					pairs = DrugGenePair.getFilePairs(args[1]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				typeOfAssociation = "file";
			}
			
		}

		System.out.println("Number of known pairs : "+ pairs.size());
		
		HashMap<String,ArrayList<GeneDiseasePair>> geneDiseasePairs = new HashMap<String, ArrayList<GeneDiseasePair>>();
		
		File geneDiseasePairsFile = new File("./geneDiseasePairs.ser");
		if(geneDiseasePairsFile.exists()) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(geneDiseasePairsFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				geneDiseasePairs = (HashMap<String, ArrayList<GeneDiseasePair>>) ois.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else {
			geneDiseasePairs = GeneDiseasePair.getGeneDiseasesPairs();
		}
		HashMap<String,String> geneEntrezLinks = Gene.getPharmgkbIDEntrezIDLinks();
		HashMap<String,ArrayList<String>> geneAttributes = Gene.getGeneAttributes();
		HashMap<String,ArrayList<String>> diseaseAttributes = Disease.getDiseaseAttributes();
		Iterator<DrugGenePair> iterator = pairs.iterator();
		int i = 0;
		int numberOfPaths = 0;
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
			drug.setATC();
			ArrayList<GeneDiseasePair> geneDiseasesPairs = geneDiseasePairs.get(gene.getEntrez_id());
			ArrayList<DrugDiseasePair> drugDiseasesPairs = DrugDiseasePair.getDrugDiseasesPairs(drug);
			if(geneDiseasesPairs != null && drugDiseasesPairs != null) {
				System.out.println("Number of gene-disease pairs found : "+geneDiseasesPairs.size());
				System.out.println("Number of drug-disease pairs found : "+drugDiseasesPairs.size());
					for(GeneDiseasePair gdpair : geneDiseasesPairs) {
						for(DrugDiseasePair ddpair : drugDiseasesPairs) {
							if (gdpair.getDisease().getCui().equals(ddpair.getDisease().getCui())) {
								numberOfPaths++;
								ddpair.getDisease().setAttributes(diseaseAttributes.get(ddpair.getDisease().getCui()));
								gdpair.getGene().setAttributes(geneAttributes.get(gdpair.getGene().getEntrez_id()));
								gdpair.getDisease().setAttributes(diseaseAttributes.get(gdpair.getDisease().getCui()));
								if(typeOfAssociation.equals("not_linked_list")) {
									File linksFile = new File("./not_linked_pairs.csv");
									if(!linksFile.exists()) {
										try {
											linksFile.createNewFile();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
									try {
										FileWriter fileWriter = new FileWriter(linksFile.getName(), true);
										BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
						    	        bufferWritter.write(pair.getGene().getPharmgkb_id()+"\t"+pair.getDrug().getPharmgkb_id()+"\n");
						    	        bufferWritter.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								else {
									Association association = new Association(pair,gdpair,ddpair);
									association.printAssociation(typeOfAssociation);
								}
								
							}
						}
					}
				
				
			}				
		}
	}		
}
