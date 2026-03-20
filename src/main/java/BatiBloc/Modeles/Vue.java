package BatiBloc.Modeles;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Vue {

    private BufferedImage image;
    private double echelle = 1.0; // 1 pixel = 1 unité interne
    private double zoom = 1.0;
    private double origineX = 0;
    private double origineY = 0;

    private final List<Zone> zones = new ArrayList<>();

    public Vue(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public double getEchelle() {
        return echelle;
    }

    public void setEchelle(double echelle) {
        this.echelle = echelle;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getOrigineX() {
        return origineX;
    }

    public double getOrigineY() {
        return origineY;
    }

    public void deplacerOrigine(double dx, double dy) {
        this.origineX += dx;
        this.origineY += dy;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void ajouterZone(Zone z) {
        zones.add(z);
    }

    public void supprimerZone(Zone z) {
        zones.remove(z);
    }
}