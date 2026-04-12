package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.Zone;

import java.awt.geom.Point2D;
import java.util.List;

public final class ZoneService {

    private ZoneService() {}

    public static Zone selectionnerZone(List<Zone> zones, Point2D.Double p) {
        for (Zone z : zones) {
            if (GeometryService.pointDansZone(p, z)) {
                return z;
            }
        }
        return null;
    }

    public static void deplacer(Zone zone, double dx, double dy) {
        zone.deplacement(dx, dy);
    }

    public static void redimensionner(Zone zone, int indexPoignee, double dx, double dy) {
        zone.redimensionner(indexPoignee, dx, dy);
    }
}