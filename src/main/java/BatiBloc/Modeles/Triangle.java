package BatiBloc.Modeles;

import java.awt.Point;
import java.util.List;

public class Triangle extends Forme {

    public Triangle(List<Point> points) {
        super(points);
        if (points.size() != 3) {
            throw new IllegalArgumentException("Un triangle doit avoir exactement 3 sommets.");
        }
    }

    @Override
    public double getLargeur() {
        double minX = Math.min(this.position.get(0).x, Math.min(this.position.get(1).x, this.position.get(2).x));
        double maxX = Math.max(this.position.get(0).x, Math.max(this.position.get(1).x, this.position.get(2).x));
        return maxX - minX;
    }

    @Override
    public double getHauteur() {
        double minY = Math.min(this.position.get(0).y, Math.min(this.position.get(1).y, this.position.get(2).y));
        double maxY = Math.max(this.position.get(0).y, Math.max(this.position.get(1).y, this.position.get(2).y));
        return maxY - minY;
    }

    @Override
    public double getAire() {
        return aire(this.position.get(0), this.position.get(1), this.position.get(2));
    }

    @Override
    public boolean contientPoint(Point p) {
        return this.pointDansTriangle(p, this.position.get(0), this.position.get(1), this.position.get(2));
    }

    private boolean pointDansTriangle(Point p, Point a, Point b, Point c) {
        double area = aire(a, b, c);
        double area1 = aire(p, b, c);
        double area2 = aire(a, p, c);
        double area3 = aire(a, b, p);

        return Math.abs(area - (area1 + area2 + area3)) < 0.01;
    }

    private double aire(Point p1, Point p2, Point p3) {
        return Math.abs(
            p1.x * (p2.y - p3.y) +
            p2.x * (p3.y - p1.y) +
            p3.x * (p1.y - p2.y)
        ) / 2.0;
    }
}