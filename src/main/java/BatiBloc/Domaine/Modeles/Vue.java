package BatiBloc.Domaine.Modeles;

import BatiBloc.Domaine.Constantes.ConstantesEchelle;
import BatiBloc.Domaine.Constantes.ConstantesVue;
import BatiBloc.Domaine.Modeles.Zones.ArmatureClassique;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
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

    public List<Zone> getZonesSansArmaturesAutomatiques() {
        return this.zones.stream()
                .filter(zone -> !(zone instanceof ArmatureClassique armatureClassique
                        && armatureClassique.isAutomatique()))
                .toList();
    }

    public void ajouterZone(Zone zone) {
        if (zone != null) {
            this.zones.add(zone);
        }
    }

    public void supprimerZone(Zone zone) {
        if (zone == this.zoneSelectionnee) {
            this.zoneSelectionnee = null;
        }
        this.zones.remove(zone);
    }

    public void supprimerArmaturesAutomatiques() {
        this.zones.removeIf(zone -> {
            boolean estAutomatique = zone instanceof ArmatureClassique armatureClassique
                    && armatureClassique.isAutomatique();

            if (estAutomatique && zone == this.zoneSelectionnee) {
                this.zoneSelectionnee = null;
            }

            return estAutomatique;
        });
    }

    public void insererZonesAvantOuvertures(List<? extends Zone> zonesAInserer) {
        if (zonesAInserer == null || zonesAInserer.isEmpty()) {
            return;
        }

        int indexInsertion = indexAvantPremiereOuverture();

        for (Zone zone : zonesAInserer) {
            if (zone != null) {
                this.zones.add(indexInsertion, zone);
                indexInsertion++;
            }
        }
    }

    private int indexAvantPremiereOuverture() {
        for (int i = 0; i < this.zones.size(); i++) {
            if (this.zones.get(i) instanceof Ouverture) {
                return i;
            }
        }

        return this.zones.size();
    }

    public Zone getZoneSelectionnee() {
        return this.zoneSelectionnee;
    }

    public void definirZoneSelectionnee(Zone zone) {
        this.zoneSelectionnee = zone;
    }
}
