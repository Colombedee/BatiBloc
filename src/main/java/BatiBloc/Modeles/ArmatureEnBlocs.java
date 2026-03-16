package BatiBloc.Modeles;

public class ArmatureEnBlocs extends Zone {

    public ArmatureEnBlocs(double largeur, double hauteur, double largeurBloc) {
        super(largeur, hauteur, largeurBloc);
    }

    @Override
    public double getHauteurBloc() {
        return 30;
    }
}