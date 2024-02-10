import java.util.*;

public class Instance {
    String table;
    Map<String, String> donnees;

    public Instance(String s, String[][]data) {
	table = s;
	donnees = new HashMap<>();
	for (int i=0; i<data.length; i++) {
	    donnees.put(data[i][0], data[i][1]);
	}
    }

    public Instance(String s) {
	table = s;
	donnees = new HashMap<>();
    }

	public String toString() {
		String res = "";
		res += "Instance de la table " + table + "\n";
		for (Map.Entry<String,String> entry : donnees.entrySet()) 
            res += "Key = " + entry.getKey() + ", Value = " + entry.getValue() + "\n";
		return res;
	}
    
}
