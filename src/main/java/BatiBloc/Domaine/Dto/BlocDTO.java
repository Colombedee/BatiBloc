package BatiBloc.Domaine.Dto;

public final class BlocDTO {

    private final double hauteur;
    private final double largeur;
    private final double positionX;
    private final double positionY;
    private final double prix;

    public BlocDTO(double positionX, double positionY, double largeur,
            double hauteur, double prix) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.largeur = largeur;
        this.hauteur = hauteur;
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