package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.List;

public final class ZoneInteractionService {

    private Zone zoneSelectionnee = null;
    private ModeInteraction mode = ModeInteraction.NONE;

    public enum ModeInteraction {
        NONE,
        SELECTION,
        SUPPRESSION,
        DEPLACEMENT,
        REDIMENSIONNEMENT
    }

    public void setMode(ModeInteraction mode) {
        this.mode = mode;
    }

    public ModeInteraction getMode(){
        return this.mode;
    }
    
    public Zone getZoneSelectionnee(){
        return this.zoneSelectionnee;
    }
    
    public void setZoneSelctionnee(Zone zone){
        this.zoneSelectionnee = zone;
    }
    
    public Zone selectionner(List<Zone> zones, Point2D.Double p) {
        this.zoneSelectionnee = ZoneService.selectionnerZone(zones, p);
        return this.zoneSelectionnee;
    }
// la logique de suppression appartient au contrôleur
//    public void supprimer(List<Zone> zones) {
//        if (modeZoneSelectionnee != null) {
//            zones.remove(modeZoneSelectionnee);
//            modeZoneSelectionnee = null;
//        }
//    }

    public void deplacer(double dx, double dy) {
        if (this.zoneSelectionnee != null) {
            ZoneService.deplacer(this.zoneSelectionnee, dx, dy);
        }
    }

    public void redimensionner(int indexPoignee, double dx, double dy) {
        if (this.zoneSelectionnee != null) {
            ZoneService.redimensionner(this.zoneSelectionnee, indexPoignee, dx, dy);
        }
    }
    
    public void redimensionnerParBoundingBox(int indexPoignee, double dx, double dy) {
        if (this.zoneSelectionnee != null) {
            ZoneService.redimensionnerParBoundingBox(this.zoneSelectionnee, indexPoignee, dx, dy);
        }
    }
    
    public void reinitialiser(){
        this.zoneSelectionnee = null;
        this.mode = ModeInteraction.NONE;
    }
}