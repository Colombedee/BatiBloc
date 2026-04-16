package BatiBloc.Domaine.Modeles.Zones;

import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Formes.Forme;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nicky
 * Ajout de deux méthodes utilitaires : viderRangees() et getBlocsSimules()
 */
public class ArmatureBlocs extends Zone {

    private final List<RangeeBloc> rangees = new ArrayList<>();

    public ArmatureBlocs(Forme forme, List<Point2D.Double> points) {
        super(forme, points);
    }

    public void ajouterRangee(RangeeBloc rangee) {
        if (rangee != null) {
            this.rangees.add(rangee);
        }
    }

    public void viderRangees() {
        this.rangees.clear();
    }

    public List<RangeeBloc> getRangees() {
        return List.copyOf(this.rangees);
    }

    public List<Bloc> getBlocsSimules() {
        List<Bloc> blocs = new ArrayList<>();
        for (RangeeBloc rangee : this.rangees) {
            blocs.addAll(rangee.getBlocs());
        }
        return blocs;
    }
}