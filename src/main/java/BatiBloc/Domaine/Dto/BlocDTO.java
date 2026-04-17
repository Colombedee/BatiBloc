
package BatiBloc.Domaine.Dto;

import java.io.Serializable;

/**
 *
 * @author nicky
 */
public final class BlocDTO implements Serializable {

    private final double x;
    private final double y;
    private final double largeur;
    private final double hauteur;
    private final double prix;

    public BlocDTO(double x, double y, double largeur, double hauteur, double prix) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.prix = prix;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getLargeur() {
        return this.largeur;
    }

    public double getHauteur() {
        return this.hauteur;
    }

    public double getPrix() {
        return this.prix;
    }
}

