package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.List;

public final class ZoneInteractionService {

    private Zone modeZoneSelectionnee = null;
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

    public Zone selectionner(List<Zone> zones, Point2D.Double p) {
        for (Zone z : zones) {
            if (GeometryService.pointDansZone(p, z)) {
                modeZoneSelectionnee = z;
                return z;
            }
        }
        modeZoneSelectionnee = null;
        return null;
    }

    public void supprimer(List<Zone> zones) {
        if (modeZoneSelectionnee != null) {
            zones.remove(modeZoneSelectionnee);
            modeZoneSelectionnee = null;
        }
    }

    public void deplacer(double dx, double dy) {
        if (modeZoneSelectionnee != null) {
            modeZoneSelectionnee.deplacement(dx, dy);
        }
    }

    public void redimensionner(int indexPoignee, double dx, double dy) {
        if (modeZoneSelectionnee != null) {
            modeZoneSelectionnee.redimensionner(indexPoignee, dx, dy);
        }
    }
}