package BatiBloc.Domaine.Dto;

public class VueRogneeDTO {

    private final int positionX;
    private final int positionY;
    private final int largeur;
    private final int hauteur;

    public VueRogneeDTO(int positionX, int positionY, int largeur, int hauteur) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public int getPositionX() {
        return this.positionX;
    }

    public int getPositionY() {
        return this.positionY;
    }

    public int getLargeur() {
        return this.largeur;
    }

    public int getHauteur() {
        return this.hauteur;
    }
}