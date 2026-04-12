package BatiBloc.Domaine.Modeles.Formes;

import java.awt.geom.Point2D;
import java.util.List;

public class Triangle extends Forme {

    public Triangle(List<Point2D.Double> points) {
        super(points);
    }

    @Override
    protected void validerPoints(List<Point2D.Double> points) {
        if (points.size() != 3) {
            throw new IllegalArgumentException("triangleSizeException");
        }
    }
    
    @Override
    public List<Integer> pointsQuiSuiventEnX(int i) {
        return switch (i) {
            case 1 -> List.of(2, 0);
            case 2 -> List.of(1, 0);
            default -> List.of();
        };
    }

    @Override
    public List<Integer> pointsQuiSuiventEnY(int i) {
        return switch (i) {
            case 1 -> List.of(2);
            case 2 -> List.of(1);
            default -> List.of();
        };
    }
}