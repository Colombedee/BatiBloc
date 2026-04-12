package BatiBloc.Domaine.Modeles.Zones;

import BatiBloc.Domaine.Modeles.Formes.Forme;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Zone {

    private double hauteur;
    private final Forme forme;
    private final List<Point2D.Double> points = new ArrayList<>();

    public Zone(double hauteur, Forme forme, List<Point2D.Double> points) {
        this.hauteur = hauteur;
        this.forme = forme;
        for (Point2D.Double p : points) {
            this.points.add(new Point2D.Double(p.x, p.y));
        }
    }
    
    public void setHauteur(double nouvelleHauteur) {
        double minY = points.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = minY + nouvelleHauteur;

        for (Point2D.Double p : points) {
            if (p.y > minY) {
                p.y = maxY;
            }
        }
    }

    public Forme getForme() {
        return this.forme;
    }

    public double getHauteur() {
        double minY = points.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = points.stream().mapToDouble(p -> p.y).max().orElse(0);
        return maxY - minY;
    }

    public double getLargeur() {
        double minX = points.stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = points.stream().mapToDouble(p -> p.x).max().orElse(0);
        return maxX - minX;
    }

    public List<Point2D.Double> getPoints() {
        return List.copyOf(this.points);
    }

    public void deplacement(double dx, double dy) {
        for (Point2D.Double p : this.points) {
            p.x += dx;
            p.y += dy;
        }
    }

    public void redimensionner(int indexPoignee, double dx, double dy) {
        Point2D.Double p = this.points.get(indexPoignee);
        p.x += dx;
        p.y += dy;

        for (int i : forme.pointsQuiSuiventEnX(indexPoignee)) {
            this.points.get(i).x += dx;
        }
        for (int i : forme.pointsQuiSuiventEnY(indexPoignee)) {
            this.points.get(i).y += dy;
        }
    }
}