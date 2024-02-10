public class Table {
    String name;
    Attribut[] attributs;

    public Table(String name, Attribut[] liste) {
	this.name = name;
	attributs = liste;
    }
    
    public String toString() {
    	String s = "Table " + name + '\n';
    	for(int i=0; i<attributs.length; i++) {
    		s += attributs[i].toString();
    	}
    	return s;
    }
}
