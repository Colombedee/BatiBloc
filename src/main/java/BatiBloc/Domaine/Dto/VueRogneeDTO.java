package BatiBloc.Domaine.Dto;

public class VueRogneeDTO {

    private final int x;
    private final int y;
    private final int largeur;
    private final int hauteur;

    public VueRogneeDTO(int x, int y, int largeur, int hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLargeur() {
        return this.largeur;
    }

    public int getHauteur() {
        return this.hauteur;
    }
}