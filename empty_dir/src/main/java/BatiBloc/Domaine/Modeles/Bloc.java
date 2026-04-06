package BatiBloc.Domaine.Modeles;

public class Bloc {

    private double largeur;

    public Bloc(double largeur) {
        if (largeur <= 0) {
            throw new IllegalArgumentException("La largeur d'un bloc doit être positive.");
        }
        this.largeur = largeur;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        if (largeur <= 0) {
            throw new IllegalArgumentException("La largeur d'un bloc doit être positive.");
        }
        this.largeur = largeur;
    }

    @Override
    public String toString() {
        return "Bloc(" + largeur + ")";
    }
}