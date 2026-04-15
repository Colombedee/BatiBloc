package BatiBloc.Domaine.Modeles.Formes;

import java.awt.geom.Point2D;
import java.util.List;

public abstract class Forme {
    
    public abstract int nombreDePoints();
    
    public Forme(List<Point2D.Double> points) {
        this.validerPoints(points);
    }
    
    public double calculerHauteur(List<Point2D.Double> points) {
        double minY = points.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = points.stream().mapToDouble(p -> p.y).max().orElse(0);
        return maxY - minY;
    }

    public double calculerLargeur(List<Point2D.Double> points) {
        double minX = points.stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = points.stream().mapToDouble(p -> p.x).max().orElse(0);
        return maxX - minX;
    }

    public abstract List<Integer> pointsQuiSuiventEnX(int indexPoignee);

    public abstract List<Integer> pointsQuiSuiventEnY(int indexPoignee);

    public void redimensionner(List<Point2D.Double> points, int index, double dx, double dy) {
        Point2D.Double p = points.get(index);
        p.x += dx;
        p.y += dy;

        for (int i : pointsQuiSuiventEnX(index)) {
            points.get(i).x += dx;
        }
        
        for (int i : pointsQuiSuiventEnY(index)) {
            points.get(i).y += dy;
        }
    }

    protected abstract void validerPoints(List<Point2D.Double> points);
}