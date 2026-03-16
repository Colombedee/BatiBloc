package BatiBloc.Modeles;

public class ArmatureClassique extends Zone {

    public ArmatureClassique(double largeur, double hauteur, double largeurBloc) {
        super(largeur, hauteur, largeurBloc);
    }

    @Override
    public double getHauteurBloc() {
        return 20; 
    }
}