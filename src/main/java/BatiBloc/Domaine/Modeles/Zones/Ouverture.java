package BatiBloc.Domaine.Modeles.Zones;

import BatiBloc.Domaine.Modeles.Formes.Forme;
import java.awt.geom.Point2D;
import java.util.List;

public class Ouverture extends Zone {

    public Ouverture(Forme forme, List<Point2D.Double> points) {
        super(forme, points);
    }
}