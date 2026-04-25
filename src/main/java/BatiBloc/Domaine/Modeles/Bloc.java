package BatiBloc.Domaine.Modeles;

public class Bloc {
    
    private double positionX;
    private double positionY;
    private double hauteur;
    private double largeur;
    private double prix;
    
   public Bloc(double hauteur, double largeur, double prix) {
       this(0.0, 0.0, largeur, hauteur, prix);
   }

    public Bloc(double positionX, double positionY, double largeur, double hauteur, double prix) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.setHauteur(hauteur);
        this.setLargeur(largeur);
        this.setPrix(prix);
    }
    
    public double getX(){
        return this.positionX;
    }
    
    public double getY(){
        return this.positionY;
    }
    
    public double getHauteur() { 
        return this.hauteur;
    }
    
    public void setHauteur(double hauteur) {
        if (hauteur <= 0) {
            throw new IllegalArgumentException("exception.blockHeight");
        }
        this.hauteur = hauteur;
    }
    
    public double getLargeur() { 
        return this.largeur;
    }

    public void setLargeur(double largeur) {
        if (largeur <= 0) {
            throw new IllegalArgumentException("exception.blockWidth");
        }
        this.largeur = largeur;
    }
    
    public double getPrix() {
        return this.prix;
    }

    public void setPrix(double prix) {
        if (prix < 0) throw new IllegalArgumentException("exception.blockPrice");
        this.prix = prix;
    }
}