import java.util.*;

public class Database {
    String name;
    Table[] tables;
    Dependance[] dependances;
    ArrayList<ArrayList<Instance>> instances; // chaque ArrayList contient les instances d'une meme table

    public Database(String name, Table[] liste, Dependance[] deps) {
	this.name = name;
	tables = liste;
	dependances = deps;
	instances = new ArrayList<>();
    }

    public Database(String name, Table[] liste, Dependance[] deps, ArrayList<ArrayList<Instance>> inst) {
	this.name = name;
	tables = liste;
	dependances = deps;
	instances = inst;
    }
    
    public String toString() {
    	return "BDD " + name;
    }

    public boolean compare(Instance i, Relation r) {
	String acomp1 = i.table;
	String acomp2 = r.name;
	if (!acomp1.equals(acomp2)) return false;
	if (r.rel.length != i.donnees.size()) return false;
	return true;
    }

    public Instance correspondance(Relation r) {
	for (int i=0; i<instances.size(); i++) {
	    for (int j = 0; j < instances.get(i).size(); j++)	{
		Instance tmp = instances.get(i).get(j);
		if (this.compare(tmp, r)) return tmp;
	    }
	}
	return null;
    }	    
    
    public boolean satisfait_corps(Dependance d) {
	for (Conjonction c : d.corps) {
	    if (c instanceof Relation) {
		if (correspondance((Relation)c) == null) return false;
	    }
	}
	return true;
    }

    /* 
       public ArrayList<Instance> getInstanceEGD(Dependance d) {
       ArrayList<Instance> res = new ArrayList<Instance>();
       for (Conjonction c : d.corps) {
       if(c instanceof Relation) {
       for (Instance i : instances) {
       Relation tmp = (Relation)c;
       String name = tmp.name;
       if(i.table == name) {
       res.add(i);
       }
       }
       }
       }
       return res;
       }
    */

    public boolean test_egalite(Dependance dep, ArrayList<Pairs> inst) {
	// voir si les les instances inst respecte la dependance (qui est une EGD)
	int last_rel = 0;
	/*for(int i = 0; i < inst.size(); i++) {
	  System.out.println(inst.get(i));
	  }*/
	for (int i = 0; i < dep.corps.length; i++) {
	    if(dep.corps[i] instanceof Relation) { // on part du principe que les relations sont au debut de la dependance et les egalité a la fin
		last_rel++;
	    } else {
		Egalite tmp = (Egalite)dep.corps[i];
		String e1 = "";
		String e2 = "";
		for(int j = 0; j < last_rel; j++) {
		    Pairs ma_pairs = inst.get(j);
		    if(e1.equals("")) {
			e1 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a1.name,"***null***");
			if(e1.equals("***null***")) {
			    e1 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a2.name,"***null***");
			}
		    } else if(e2.equals("")) {
			e2 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a2.name,"***null***");
			if(e2.equals("***null***")) {
			    e2 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a1.name,"***null***");
			}
		    } else {
			if(!e1.equals(e2)) {
			    return true; // le corps de la dependance n'est pas respecter donc on a pas a egaliser
			}
		    }
		}
		if(!e1.equals(e2)) return true;
	    }
	}
	String e1 = "";
	String e2 = "";
	//si on arrive ici c'est que le corps de la dependance a été respecter
	for (int i = 0; i < dep.tete.length; i++) {
	    Egalite tmp = (Egalite)dep.tete[i];
	    e1 = "";
	    e2 = "";
	    for(int j = 0; j < last_rel; j++) {
		Pairs ma_pairs = inst.get(j);
		if(e1.equals("")) {
		    e1 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a1.name,"***null***");
		    if(e1.equals("***null***")) {
			e1 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a2.name,"***null***");
		    }
		} else if(e2.equals("")) {
		    e2 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a2.name,"***null***");
		    if(e2.equals("***null***")) {
			e2 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(tmp.a1.name,"***null***");
		    }
		} else {
		    if(!e1.equals(e2)) {
			return false; // la tete de la dependance n'est pas respecter donc on doit egaliser
		    }
		}
		
	    }
	}
	return e1.equals(e2);
    }

    public ArrayList<Pairs> est_egal(Dependance dep) {
	ArrayList<Pairs> grand_t = new ArrayList<Pairs>(); // contient les index des instances 
	//int[] index = new int[dep.corps.length];
	boolean finis = false;
	Map<String,Integer> indexMap = new HashMap<String,Integer>();
	int nb_iteration = 0;
	int nb_iteration_max = 1;
	boolean nb_iteration_augmente = true;
	while(!finis) {
	    boolean augmente = true;
	    int taille = instances.size();
	    for (int k = 0; k < dep.corps.length; k++) {
		Relation r;
		if(dep.corps[k] instanceof Relation) {
		    r = (Relation)dep.corps[k];
		    if(indexMap.get(r.name + String.valueOf(k)) == null) {
			indexMap.put(r.name + String.valueOf(k), 0);
		    }
		    for(int i = 0; i < taille; i++) { // on parcours tout les ArrayList d'instances
			int taille_tab = instances.get(i).size();
			if(taille_tab > 0 && r.name.equals(instances.get(i).get(0).table)) {	
			    if(nb_iteration_augmente)nb_iteration_max *= taille_tab;
			    for(int j = indexMap.get(r.name + String.valueOf(k)); j < taille_tab; j++) { // on parcours toutes les instances
				nb_iteration++;
				int map_plus = indexMap.get(r.name + String.valueOf(k));
				grand_t.add(new Pairs(i,j));
				if(augmente && map_plus < taille_tab-1) {
				    indexMap.put(r.name + String.valueOf(k), map_plus+1);
				    augmente = false;
				} else if(augmente && map_plus == taille_tab-1) {
				    indexMap.put(r.name + String.valueOf(k), taille_tab/3);
				}
				break;
			    }
			    //break;
			}
								
		    }

		}
	    }
	    nb_iteration_augmente = false;
	    if(nb_iteration > nb_iteration_max + nb_iteration_max/4) {
		finis = true;
	    }
				
	    if(!grand_t.isEmpty() && test_egalite(dep,grand_t)) {
		// on test la suite pour voir avec les tuples suivant
		grand_t.clear();
		//System.out.println("il n'y a pas a egaliser");
	    } else {
		return grand_t; // on retourner le(s) tuples qui ne repescte pas la dependance pour pouvoir les egaliser ensuite
	    }
	    /* 
	       finis = true;
	       for (int z = 0; z < dep.corps.length; z++) {
	       if(dep.corps[z] instanceof Relation) {
	       Relation r = (Relation)dep.corps[z];
	       for(int j = 0; j < taille; j++) {
	       if(instances.get(j).size() > 0 && r.name.equals(instances.get(j).get(0).table)) {
	       if(indexMap.get(r.name + String.valueOf(z)) < instances.get(j).size()-1) 
	       finis = false; // on regarde si on a parcouru toutes les instances
								
	       }
	       }
	       }
	       }*/
				
	}
		


	/*for (Conjonction c : dep.tete) {
	  if(!(c instanceof Egalite)) {
	  return false;
	  } else {
	  for(int i = 1; i < instance_concernee.size(); i++) {
						
	  }
	  }
	  }*/
	return grand_t; // grand_t est normalement vide si on arrive ici
    }
	
    public boolean satisfait_tete(Dependance d) {
	/*if(d instanceof EGD) {
	  return est_egal(d).isEmpty();
	  }*/
	for (Conjonction c : d.tete) {
	    if (c instanceof Relation) {
		if (correspondance((Relation)c) == null) return false;
	    }
	}
	return true;
    }

    public static Instance create_new_instance_tgd(Relation r, Relation p, Instance i) { // R(a,b) -> P(a,b,z1) / i : Instance_de_r
	Instance u = new Instance(p.name);
	for (int k=0; k<p.rel.length; k++) { // on parcourt p
	    u.donnees.put(p.rel[k].name, i.donnees.getOrDefault(p.rel[k].name, ""));
	}
	return u;
    }


    public void egalise(Dependance dep,int conjonc_acc ,ArrayList<Pairs> grand_t) {
	String val = "";
	Egalite eg = (Egalite)dep.tete[conjonc_acc];
	for(int j = 0; j < grand_t.size(); j++) {
	    Pairs ma_pairs = grand_t.get(j);
	    if(!instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(eg.a1.name,"***null***").equals("***null***")) { // cas ou l'attribut n'existe pas
		//if(instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(eg.a1.name,"***null***") != null) { // cas ou l'attribut existe mais vaut null
		val = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(eg.a1.name,"***null***");
		break;
		//}
	    } 
	}
	for(int j = 0; j < grand_t.size(); j++) {
	    Pairs ma_pairs = grand_t.get(j);
	    String val2 = instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.getOrDefault(eg.a2.name,"***null***");
	    if(!val2.equals("***null***")) { // cas ou l'attribut n'existe pas
		if((val == null || val.equals("")) && (!val2.equals("") && val2 != null)) {
		    //si le 1er attribut est null mais le 2eme ne n'est pas
		    //on crée une nouvelle egaliter en inversant les 2 attribut et on appelle la fonction recursivement
		    Conjonction[] conj = {new Egalite(eg.a2, eg.a1)};
		    Dependance dep2 = new EGD(null,conj);
		    egalise(dep2, 0, grand_t);
		    break;
		} else {
		    instances.get(ma_pairs.index).get(ma_pairs.index_index).donnees.put(eg.a2.name, val); // egalisation
		}
	    } 
	}
	if(conjonc_acc < dep.tete.length-1) {
	    egalise(dep, conjonc_acc+1, grand_t);
	}
    }
	
    public static int trouve_df(boolean[] tab) {
	for (int i=0; i<tab.length; i++) {
	    if (!tab[i]) return i;
	}
	return -1;
    }
		
    public void chase() {
	boolean[] appliquee = new boolean[dependances.length];
	for (int i=0; i<dependances.length; i++) {
	    ArrayList<Pairs> grant_t;
	    if(dependances[i] instanceof EGD) {
		grant_t = est_egal(dependances[i]);
	    } else {
		grant_t = new ArrayList<Pairs>(); //empty
	    }
			
	    if (!appliquee[i] && ((satisfait_corps(dependances[i]) && !satisfait_tete(dependances[i])) || !grant_t.isEmpty())) {
		if (dependances[i] instanceof TGD) {
		    Relation dep = (Relation)dependances[i].corps[0]; 
		    Relation arr = (Relation)dependances[i].tete[0];
		    Instance cop = correspondance(dep);
		    Instance u = create_new_instance_tgd(dep,arr,cop);
		    System.out.println("création d'un nouveau tuple");
		    boolean ajouter = false;
		    for (int j = 0 ; j < instances.size(); j++) {
			if(instances.get(j).size() > 0 && instances.get(j).get(0).table.equals(u.table)) {
			    instances.get(j).add(u);
			    ajouter = true;
			}
		    }
		    if(!ajouter) {
			ArrayList<Instance> nouv_inst = new ArrayList<Instance>();
			nouv_inst.add(u);
			instances.add(nouv_inst);
		    }
		} else if (dependances[i] instanceof EGD) { // egaliser
		    while(!grant_t.isEmpty()) {
			System.out.println("nous allons egaliser");
			print_instance_egalise(grant_t);
			egalise(dependances[i],0,grant_t);
			grant_t.clear();
			grant_t = est_egal(dependances[i]);
		    }
					
		}
		appliquee[i] = true;
	    }
	}
    }
    
    public void oblivious_chase() {
	for (int i=0; i<dependances.length; i++) {
	    if (dependances[i] instanceof TGD && satisfait_corps(dependances[i])) {
		Relation dep = (Relation)dependances[i].corps[0]; 
		Relation arr = (Relation)dependances[i].tete[0];
		Instance cop = correspondance(dep);
		Instance u = create_new_instance_tgd(dep,arr,cop);
		System.out.println("création d'un nouveau tuple");
		boolean ajouter = false;
		for (int j = 0 ; j < instances.size(); j++) {
		    if(instances.get(j).size() > 0 && instances.get(j).get(0).table.equals(u.table)) {
			instances.get(j).add(u);
			ajouter = true;
		    }
		}
		if(!ajouter) {
		    ArrayList<Instance> nouv_inst = new ArrayList<Instance>();
		    nouv_inst.add(u);
		    instances.add(nouv_inst);
		}
	    }
	}
    }


    public void print_instance() {
	System.out.println("size = " + instances.size());
	for(int i = 0; i < instances.size(); i++) {
	    int taille = instances.get(i).size();
	    System.out.println("size_size = " + taille);
	    for(int j = 0; j < taille; j++) {
		System.out.println("i = " + i + " j = " + j + " " + instances.get(i).get(j).toString());
	    }
	}
    }

    public void print_instance_egalise(ArrayList<Pairs> inst) {
	for(int i = 0; i < inst.size(); i++) {
	    System.out.println(instances.get(inst.get(i).index).get(inst.get(i).index_index));
	}
    }

    public static void test1_EGD() {
	Attribut[] r1 = new Attribut[2];
	r1[0] = new Attribut("a", "string");
	r1[1] = new Attribut("b", "string");


	Attribut[] p1 = new Attribut[2];
	p1[0] = new Attribut("c", "string");
	p1[1] = new Attribut("d", "string");

	Table r = new Table("R", r1);
	Table p = new Table("P", p1);

	Table[] mes_tables = {r,p};

	Conjonction[] cor = {new Relation("R", r1), new Relation("P", p1), new Egalite(r1[1],p1[0])};
	Conjonction[] te = {new Egalite(r1[0], p1[1])};
	Dependance dt = new EGD(cor,te);

	Dependance[] mesDependance = {dt};

	String[][] dat = {{"a","a66"},{"b","b44"}};
	Instance i1 = new Instance("R", dat);

	String[][] dat2 = {{"c","b44"},{"d","d12"}};
	Instance i2 = new Instance("P", dat2);

	ArrayList<Instance> al1 = new ArrayList<Instance>();
	al1.add(i1);

	ArrayList<Instance> al2 = new ArrayList<Instance>();
	al2.add(i2);

	ArrayList<ArrayList<Instance>> al_final = new ArrayList<ArrayList<Instance>>();
	al_final.add(al1);
	al_final.add(al2);

	Database db = new Database("data base", mes_tables, mesDependance,al_final);

	db.print_instance();

	db.chase();

	db.print_instance();
    }

    public static void test2_EGD() {
	System.out.println("test EGD");
	Attribut[] et = new Attribut[2];
	et[0] = new Attribut("numero", "string");
	et[1] = new Attribut("nom", "string");


		

	Table r = new Table("Pilote", et);

	Table[] mes_tables = {r};

	Conjonction[] cor = {new Relation("Pilote", et), new Relation("Pilote", et), new Egalite(et[0],et[0])};
	Conjonction[] te = {new Egalite(et[1], et[1])};
	Dependance dt = new EGD(cor,te);

	Dependance[] mesDependance = {dt};

	String[][] dat = {{"numero","44"},{"nom","Hamilton"}};
	Instance i1 = new Instance("Pilote", dat);

	String[][] dat2 = {{"numero","33"},{"nom","Verstappen"}};
	Instance i2 = new Instance("Pilote", dat2);

	String[][] dat3 = {{"numero","44"},{"nom","Bottas"}};
	Instance i3 = new Instance("Pilote", dat3);

	String[][] dat4 = {{"numero","14"},{"nom","Alonso"}};
	Instance i4 = new Instance("Pilote", dat4);

	String[][] dat5 = {{"numero","33"},{"nom","Kvyat"}};
	Instance i5 = new Instance("Pilote", dat5);

	String[][] dat6 = {{"numero","16"},{"nom","Leclerc"}};
	Instance i6 = new Instance("Pilote", dat6);

	ArrayList<Instance> al1 = new ArrayList<Instance>();
	al1.add(i1);
	al1.add(i2);
	al1.add(i3);
	al1.add(i4);
	al1.add(i5);
	al1.add(i6);


	ArrayList<ArrayList<Instance>> al_final = new ArrayList<ArrayList<Instance>>();
	al_final.add(al1);

	Database db = new Database("les pilotes", mes_tables, mesDependance,al_final);

	db.print_instance();
	System.out.println("--------------------------------");
	db.chase();
	System.out.println("--------------------------------");
	db.print_instance();
    }

    public static void test_TGD() {
	System.out.println("exemple du sujet");
        Attribut[] ar = new Attribut[2];
        ar[0] = new Attribut("a","string");
        ar[1] = new Attribut("b","string");
        Attribut[] ap = new Attribut[2];
        ap[0] = new Attribut("b","string");
        ap[1] = new Attribut("z2","string");
        Attribut[] aq = new Attribut[3];
        aq[0] = new Attribut("a","string");
        aq[1] = new Attribut("b","string");
        aq[2] = new Attribut("z1","string");
        
        Table[] list_tables = new Table[3];
        list_tables[0] = new Table("R",ar);
        list_tables[1] = new Table("Q",aq);
        list_tables[2] = new Table("P",ap);
        
        Conjonction[] co1 = {new Relation("R",ar)};
        Conjonction[] te1 = {new Relation("Q",aq)};
        Dependance d1 = new TGD(co1, te1);
        Conjonction[] te2 = {new Relation("P",ap)};
        Dependance d2 = new TGD(te1,te2);
	Conjonction[] co3 = {new Relation("R",ar),new Relation("P",ap),new Egalite(ar[1],ap[0])};
	Conjonction[] te3 = {new Egalite(ar[0],ap[1])};
	Dependance d3 = new EGD(co3,te3);
        Dependance[] list_dep = {d1,d2,d3};
        
        String[][]data1 = {{"a","a11"},{"b","b11"}};
        Instance i1 = new Instance("R",data1);
        ArrayList<Instance> li = new ArrayList<>();
        li.add(i1);
        ArrayList<ArrayList<Instance>> lli = new ArrayList<>();
        lli.add(li);
        
        Database bdd = new Database("bdd1", list_tables, list_dep, lli);
        bdd.print_instance();
	bdd.chase();
	bdd.print_instance();
	System.out.println("---------------- oblivious chase --------------------------");
	bdd.oblivious_chase();
	bdd.print_instance();
    }
	
    public static void main (String[] args) {
	/*Attribut[] tt = new Attribut[3];
	  tt[0] = new Attribut("family name", "string");
	  tt[1] = new Attribut("given name", "string");
	  tt[2] = new Attribut("age", "int");
	  Table t1 = new Table("recensement", tt);
	  Dependance[] dt = new Dependance[0];
	  Table[] t = new Table[1];
	  t[0] = t1;
	  Database d1 = new Database("test", t, dt);
	  System.out.println(d1.toString());
	  System.out.println(t1.toString());*/

	
	test2_EGD();

	System.out.println("/////////////////////////////////////////////////");

	
	test_TGD();
	/*Attribut[] ar = new Attribut[2];
        ar[0] = new Attribut("a","string");
        ar[1] = new Attribut("b","string");
	Relation r = new Relation("R",ar);
	Attribut[] aq = new Attribut[3];
        aq[0] = new Attribut("a","string");
        aq[1] = new Attribut("b","string");
        aq[2] = new Attribut("c","string");
	Relation q = new Relation("Q",aq);
	String[][]data1 = {{"a","a11"},{"b","b11"}};
	Instance i1 = new Instance("R",data1);
	System.out.println(i1.toString());
	Instance test = create_new_instance_tgd(r,q,i1);
	System.out.println(test.toString());*/
	
		
    }

     

}
