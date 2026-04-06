package BatiBloc.Domaine.Modeles;

import java.awt.Point;
import java.util.List;

public class TriangleTronque extends Forme {

    public TriangleTronque(List<Point> points) {
        super(points);
        if (points.size() != 4) {
            throw new IllegalArgumentException("Un triangle tronqué doit avoir 4 sommets.");
        }
    }

    @Override
    public double getLargeur() {
        double minX = this.position.stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = this.position.stream().mapToDouble(p -> p.x).max().orElse(0);
        return maxX - minX;
    }

    @Override
    public double getHauteur() {
        double minY = this.position.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = this.position.stream().mapToDouble(p -> p.y).max().orElse(0);
        return maxY - minY;
    }

    @Override
    public double getAire() {
        Point A = this.position.get(0);
        Point B = this.position.get(1);
        Point C = this.position.get(2);
        Point D = this.position.get(3);

        return aire(A, B, C) + aire(A, C, D);
    }

    @Override
    public boolean contientPoint(Point p) {
        Point A = this.position.get(0);
        Point B = this.position.get(1);
        Point C = this.position.get(2);
        Point D = this.position.get(3);

        return this.pointDansTriangle(p, A, B, C) ||
               this.pointDansTriangle(p, A, C, D);
    }

    // Simulation livrable 4 @roneh
    public double getLargeurSommet() {
        double xGaucheSommet = this.position.get(3).x;
        double xDroitSommet  = this.position.get(2).x;
        return Math.abs(xDroitSommet - xGaucheSommet);
    }
    
    //@roneh pour sim liv.4
    @Override
    public double getLargeurAHauteur(double distanceDepuisLaBase) {
        double hauteur = getHauteur();
        if (hauteur <= 0) return getLargeur();
        double t = Math.min(1.0, distanceDepuisLaBase / hauteur);
        double grande = getLargeur();
        double petite = getLargeurSommet();
        return grande + (petite - grande) * t;
    }
    // Simulation 4
    @Override
    public double getXOffsetAHauteur(double distanceDepuisLaBase) {
        return (getLargeur() - getLargeurAHauteur(distanceDepuisLaBase)) / 2.0;
    }
    
    private double aire(Point p1, Point p2, Point p3) {
        return Math.abs(
            p1.x * (p2.y - p3.y) +
            p2.x * (p3.y - p1.y) +
            p3.x * (p1.y - p2.y)
        ) / 2.0;
    }

    private boolean pointDansTriangle(Point p, Point a, Point b, Point c) {
        double area = aire(a, b, c);
        double area1 = aire(p, b, c);
        double area2 = aire(a, p, c);
        double area3 = aire(a, b, p);

        return Math.abs(area - (area1 + area2 + area3)) < 0.01;
    }
}