package linkMaker;

public class Association {
	private GeneDiseasePair geneDiseasePair;
	private DrugDiseasePair drugDiseasePair;
	
	public Association(GeneDiseasePair geneDiseasePair,DrugDiseasePair drugDiseasePair) {
		this.geneDiseasePair= geneDiseasePair;
		this.drugDiseasePair= drugDiseasePair;
	}
	
	public void printAssociation() {
		System.out.println("Correspondance trouvée entre "+this.geneDiseasePair.getGene().getEntrez_id()+ " et "+this.drugDiseasePair.getDisease().getCui());

		System.out.println(this.geneDiseasePair.toString());
	}

	public GeneDiseasePair getGeneDiseasePair() {
		return geneDiseasePair;
	}

	public void setGeneDiseasePair(GeneDiseasePair geneDiseasePair) {
		this.geneDiseasePair = geneDiseasePair;
	}

	public DrugDiseasePair getDrugDiseasePair() {
		return drugDiseasePair;
	}

	public void setDrugDiseasePair(DrugDiseasePair drugDiseasePair) {
		this.drugDiseasePair = drugDiseasePair;
	}

	@Override
	public String toString() {
		return "Association [geneDiseasePair=" + geneDiseasePair
				+ ", drugDiseasePair=" + drugDiseasePair + "]";
	}
}
