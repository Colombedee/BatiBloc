package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GeometryService {

    private GeometryService() {}

    public static boolean pointDansZone(Point2D.Double p, Zone zone) {
        return pointDansPolygone(p, zone.getPoints());
    }

    public static boolean pointDansPolygone(Point2D.Double p, List<Point2D.Double> poly) {
        boolean inside = false;
        int n = poly.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point2D.Double pi = poly.get(i);
            Point2D.Double pj = poly.get(j);

            boolean intersect = ((pi.y > p.y) != (pj.y > p.y)) &&
                    (p.x < (pj.x - pi.x) * (p.y - pi.y) / (pj.y - pi.y) + pi.x);

            if (intersect) inside = !inside;
        }

        return inside;
    }
    
    public static double largeurDisponible(Zone zone, double y) {
        List<Point2D.Double> pts = zone.getPoints();

        List<Double> intersections = new ArrayList<>();

        for (int i = 0; i < pts.size(); i++) {
            Point2D.Double p1 = pts.get(i);
            Point2D.Double p2 = pts.get((i + 1) % pts.size());

            if ((p1.y <= y && p2.y >= y) || (p2.y <= y && p1.y >= y)) {
                if (p1.y != p2.y) {
                    double t = (y - p1.y) / (p2.y - p1.y);
                    double x = p1.x + t * (p2.x - p1.x);
                    intersections.add(x);
                }
            }
        }

        if (intersections.size() < 2) return 0;

        Collections.sort(intersections);
        return intersections.get(intersections.size() - 1) - intersections.get(0);
    }
}