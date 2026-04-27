package BatiBloc.Domaine.Modeles.Zones;

import BatiBloc.Domaine.Modeles.Formes.Forme;
import java.awt.geom.Point2D;
import java.util.List;

public class ArmatureClassique extends Zone {

    private final boolean automatique;

    public ArmatureClassique(Forme forme, List<Point2D.Double> points) {
        this(forme, points, false);
    }

    public ArmatureClassique(Forme forme, List<Point2D.Double> points, boolean automatique) {
        super(forme, points);
        this.automatique = automatique;
    }

    public boolean isAutomatique() {
        return this.automatique;
    }
}
