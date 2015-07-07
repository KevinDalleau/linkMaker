package linkMaker;

public class DrugGenePair {
	Drug drug;
	Gene gene;
	
	public DrugGenePair(Gene gene, Drug drug) {
		this.gene = gene;
		this.drug = drug;
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	@Override
	public String toString() {
		return "DrugGenePair [drug=" + drug + ", gene=" + gene + "]";
	}
}
