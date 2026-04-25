package BatiBloc.Domaine.Modeles.Zones;

import BatiBloc.Domaine.Modeles.Formes.Forme;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Zone {

    private final Forme forme;
    private final List<Point2D.Double> points;

    public Zone(Forme forme, List<Point2D.Double> points) {
        if (points.size() != forme.nombreDePoints()) {
            throw new IllegalArgumentException("exception.verticeNumber");
        }

        this.forme = forme;
        this.points = new ArrayList<>();
        for (Point2D.Double p : points) {
            this.points.add(new Point2D.Double(p.x, p.y));
        }
    }

    public double getHauteur() {
        return forme.calculerHauteur(points);
    }

    public double getLargeur() {
        return forme.calculerLargeur(points);
    }

    public Forme getForme() {
        return this.forme;
    }
    
    public void redimensionner(int indexPoignee, double dx, double dy) {
        forme.redimensionner(points, indexPoignee, dx, dy);
    }

    public void deplacement(double dx, double dy) {
        for (Point2D.Double p : points) {
            p.x += dx;
            p.y += dy;
        }
    }

    public List<Point2D.Double> getPoints() {
        return List.copyOf(points);
    }
    
    public List<Point2D.Double> getPointsInternes() {
        return points;
    }
}