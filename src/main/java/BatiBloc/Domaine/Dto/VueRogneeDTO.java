package BatiBloc.Domaine.Dto;

public class VueRogneeDTO {
    
    private final int hauteur;
    private final int largeur;
    private final int positionX;
    private final int positionY;


    public VueRogneeDTO(int hauteur,  int largeur, int positionX, int positionY ) {
        this.hauteur = hauteur;
        this.largeur = largeur;
        this.positionX = positionX;
        this.positionY = positionY; 
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