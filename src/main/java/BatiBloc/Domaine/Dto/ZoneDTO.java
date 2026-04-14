package BatiBloc.Domaine.Dto;

import java.awt.Point;
import java.util.List;


/**
 *
 * @author ndanic
 * Correction
 */


public final class ZoneDTO {

    private final TypeZoneDTO type;
    private final TypeForme forme;
    private final List<Point> points;
    private final boolean selectionnee;

    public ZoneDTO(TypeZoneDTO type,
                   TypeForme forme,
                   List<Point> points,
                   boolean selectionnee) {
        this.type = type;
        this.forme = forme;
        this.points = List.copyOf(points);
        this.selectionnee = selectionnee;
    }

    public TypeZoneDTO getType() {
        return type;
    }

    public TypeForme getForme() {
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
