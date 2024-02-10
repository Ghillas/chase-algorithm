public class Attribut {
    boolean not_null;
    String name;
    String type; // INT/TEXT/BOOL/FLOAT

    public Attribut(boolean f, String name, String type) {
	not_null = f;
	this.name = name;
	this.type = type;
    }

    public Attribut(String name, String type) {
	not_null = false;
	this.name = name;
	this.type = type;
    }
    
    public String toString() {
    	return name + " : " + type + '\n';
    }
}
    
