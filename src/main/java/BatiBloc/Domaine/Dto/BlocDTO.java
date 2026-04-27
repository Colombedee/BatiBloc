package BatiBloc.Domaine.Dto;

public final class BlocDTO {

    private final double hauteur;
    private final double largeur;
    private final double positionX;
    private final double positionY;
    private final double prix;

    public BlocDTO(double hauteur, double largeur, double positionX,
            double positionY, double prix) {
        this.hauteur = hauteur;
        this.largeur = largeur;
        this.positionX = positionX;
        this.positionY = positionY;
        this.prix = prix;
    }
    
    public double getHauteur() {
        return this.hauteur;
    }
    
    public double getLargeur() {
        return this.largeur;
    }

    public double getPositionX() {
        return this.positionX;
    }

    public double getPositionY() {
        return this.positionY;
    }

    public double getPrix() {
        return this.prix;
    }
}