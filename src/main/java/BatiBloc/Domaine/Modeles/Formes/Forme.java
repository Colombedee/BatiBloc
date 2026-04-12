package BatiBloc.Domaine.Modeles.Formes;

import java.awt.geom.Point2D;
import java.util.List;

public abstract class Forme {
    
    public Forme(List<Point2D.Double> points) {
        this.validerPoints(points);
    }

    protected abstract void validerPoints(List<Point2D.Double> points);

    public abstract List<Integer> pointsQuiSuiventEnX(int indexPoignee);

    public abstract List<Integer> pointsQuiSuiventEnY(int indexPoignee);
}