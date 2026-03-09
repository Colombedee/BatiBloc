package BatiBloc.dto;

public class RognerVueDTO {

    private final int x;
    private final int y;
    private final int largeur;
    private final int hauteur;

    public RognerVueDTO(int x, int y, int largeur, int hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }
}