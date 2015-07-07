package linkMaker;

import java.util.HashSet;

public class Gene {
 String pharmgkb_id;
 HashSet<String> attributes;
 HashSet<String> linked_diseases;

 
 public Gene(String pharmgkb_id) {
	this.pharmgkb_id = pharmgkb_id;
	attributes = null;
	linked_diseases = null;
	
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
	return "Gene [pharmgkb_id=" + pharmgkb_id + ", attributes=" + attributes
			+ ", linked_diseases=" + linked_diseases + "]";
}
}
