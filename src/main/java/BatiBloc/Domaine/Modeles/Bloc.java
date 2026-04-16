package BatiBloc.Domaine.Modeles;

/**
 * 
 * @author nicky
 * Correction retrait de this dans certains return car ce n'était pas nécessaire
 * Il faut connaître la position réelle des blocs (avec x et y ). 
 * Le modèle Bloc précédent contenait hauteur/largeur/prix, mais pas de
 * coordonnées suffisantes pour l’affichage bloc par bloc.
 */

public class Bloc {
    
    private double x;
    private double y;
    private double hauteur;
    private double largeur;
    private double prix;
    
   public Bloc(double hauteur, double largeur, double prix) {
       this(0.0, 0.0, largeur, hauteur, prix);
   }

    public Bloc( double x, double y, double largeur, double hauteur, double prix) {
        this.x = x;
        this.y = y;
        this.setHauteur(hauteur);
        this.setLargeur(largeur);
        this.setPrix(prix);
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getHauteur() { 
        return hauteur;
    }
    
    public void setHauteur(double hauteur) {
        if (hauteur <= 0) {
            throw new IllegalArgumentException("blocHeightException");
        }
        this.hauteur = hauteur;
    }
    
    public double getLargeur() { 
        return largeur;
    }

    public void setLargeur(double largeur) {
        if (largeur <= 0) {
            throw new IllegalArgumentException("blocWidthException");
        }
        this.largeur = largeur;
    }
    
    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        if (prix < 0) throw new IllegalArgumentException("blocPriceException");
        this.prix = prix;
    }
}