package BatiBloc.Domaine.Modeles;

import BatiBloc.Domaine.Constantes.ConstantesEchelle;
import BatiBloc.Domaine.Constantes.ConstantesVue;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Vue {

    private BufferedImage image;
    private double origineX = ConstantesVue.ORIGINE_X_DEFAUT;
    private double origineY = ConstantesVue.ORIGINE_Y_DEFAUT;
    private double zoom = ConstantesVue.ZOOM_DEFAUT;
    private Echelle echelle = new Echelle(ConstantesEchelle.POUCES_PAR_PIED);
    private final List<Zone> zones = new ArrayList<>();
    private Zone zoneSelectionnee = null;

    public Vue(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public double getOrigineX() {
        return this.origineX;
    }

    public double getOrigineY() {
        return this.origineY;
    }

    public void deplacerOrigine(double dx, double dy) {
        this.origineX += dx;
        this.origineY += dy;
    }

    public double getZoom() {
        return this.zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public Echelle getEchelle() {
        return this.echelle;
    }

    public void setEchelle(Echelle echelle) {
        this.echelle = echelle;
    }

    public List<Zone> getZones() {
        return List.copyOf(this.zones);
    }

    public void ajouterZone(Zone zone) {
        this.zones.add(zone);
    }

    public void supprimerZone(Zone zone) {
        if (zone == this.zoneSelectionnee) {
            this.zoneSelectionnee = null;
        }
        zones.remove(zone);
    }

    public Zone getZoneSelectionnee() {
        return this.zoneSelectionnee;
    }

    public void definirZoneSelectionnee(Zone zone) {
        this.zoneSelectionnee = zone;
    }
}