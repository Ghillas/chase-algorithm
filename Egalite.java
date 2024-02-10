public class Egalite extends Conjonction{
    Attribut a1;
    String a1_table;
    Attribut a2;
    String a2_table;


    public Egalite(Attribut a1, Attribut a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    public Egalite(Attribut a1,String a1_table, Attribut a2, String a2_table) {
        this.a1 = a1;
        this.a1_table = a1_table;
        this.a2 = a2;
        this.a2_table = a2_table;
    }
}
