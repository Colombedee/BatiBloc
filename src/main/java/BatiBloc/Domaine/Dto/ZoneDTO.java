package BatiBloc.Domaine.Dto;

import BatiBloc.Domaine.Modeles.Formes.Forme;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.Point;
import java.util.List;

public final class ZoneDTO {

    private final Zone type;
    private final Forme forme;
    private final List<Point> points;
    private final boolean selectionnee;

    public ZoneDTO(Zone type,
                   Forme forme,
                   List<Point> points,
                   boolean selectionnee) {

        this.type = type;
        this.forme = forme;
        this.points = List.copyOf(points);
        this.selectionnee = selectionnee;
    }

    public Zone getType() {
        return type;
    }

    public Forme getForme() {
        return forme;
    }

    public List<Point> getPoints() {
        return points;
    }

    public boolean isSelectionnee() {
        return selectionnee;
    }

    public boolean contientPoint(Point p) {
        java.awt.Polygon poly = new java.awt.Polygon();
        for (Point pt : points) {
            poly.addPoint(pt.x, pt.y);
        }
        return poly.contains(p);
    }
}