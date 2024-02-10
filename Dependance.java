public abstract class Dependance {
	Conjonction[] corps;
	Conjonction[] tete;
	
	public Dependance(Conjonction[] l, Conjonction[] r) {
		corps = l;
		tete = r;
	}
}
