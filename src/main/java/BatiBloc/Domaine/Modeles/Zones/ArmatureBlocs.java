package BatiBloc.Domaine.Modeles.Zones;

import BatiBloc.Domaine.Modeles.Formes.Forme;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ArmatureBlocs extends Zone {

    private final List<RangeeBloc> rangees = new ArrayList<>();

    public ArmatureBlocs(Forme forme, List<Point2D.Double> points) {
        super(forme, points);
    }

    public void ajouterRangee(RangeeBloc rangee) {
        this.rangees.add(rangee);
    }

    public List<RangeeBloc> getRangees() {
        return List.copyOf(this.rangees);
    }
}