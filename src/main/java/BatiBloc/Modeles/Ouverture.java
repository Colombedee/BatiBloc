package BatiBloc.Modeles;

public class Ouverture extends Zone {

    public enum Type { PORTE, FENETRE }

    private Type type;

    public Ouverture(double largeur, double hauteur, double largeurBloc, Type type) {
        super(largeur, hauteur, largeurBloc);
        this.type = type;
    }

    @Override
    public double getHauteurBloc() {
        return 0;
    }
}