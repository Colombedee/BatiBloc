package BatiBloc.Domaine.Modeles.Formes;

import java.awt.geom.Point2D;
import java.util.List;

public class Rectangle extends Forme {

    public Rectangle(List<Point2D.Double> points) {
        super(points);
    }

    @Override
    protected void validerPoints(List<Point2D.Double> points) {
        if (points.size() != 4) {
            throw new IllegalArgumentException("rectangleSizeException");
        }
    }
    
    @Override
    public List<Integer> pointsQuiSuiventEnX(int i) {
        return switch (i) {
            case 0 -> List.of(3);
            case 1 -> List.of(2);
            case 2 -> List.of(1);
            case 3 -> List.of(0);
            default -> List.of();
        };
    }

    @Override
    public List<Integer> pointsQuiSuiventEnY(int i) {
        return switch (i) {
            case 0 -> List.of(1);
            case 1 -> List.of(0);
            case 2 -> List.of(3);
            case 3 -> List.of(2);
            default -> List.of();
        };
    }
}