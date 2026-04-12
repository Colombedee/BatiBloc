package BatiBloc.Domaine.Modeles;

public class Bloc {

    private double hauteur;
    private double largeur;
    private double prix;

    public Bloc(double hauteur, double largeur, double prix) {
        this.setHauteur(hauteur);
        this.setLargeur(largeur);
        this.setPrix(prix);
    }
    
    public double getHauteur() { 
        return this.hauteur;
    }
    
    public void setHauteur(double hauteur) {
        if (hauteur <= 0) {
            throw new IllegalArgumentException("blocHeightException");
        }
        this.hauteur = hauteur;
    }
    
    public double getLargeur() { 
        return this.largeur;
    }

    public void setLargeur(double largeur) {
        if (largeur <= 0) {
            throw new IllegalArgumentException("blocWidthException");
        }
        this.largeur = largeur;
    }
    
    public double getPrix() {
        return this.prix;
    }

    public void setPrix(double prix) {
        if (prix < 0) throw new IllegalArgumentException("blocPriceException");
        this.prix = prix;
    }
}