package BatiBloc.Domaine.Modeles;

import java.util.ArrayList;
import java.util.List;

public class RangeeBloc {

    private final double positionY;
    private final List<Bloc> blocs = new ArrayList<>();

    public RangeeBloc(double positionY) {
        this.positionY = positionY;
    }

    public void ajouterBloc(Bloc bloc) {
        this.blocs.add(bloc);
    }

    public List<Bloc> getBlocs() {
        return List.copyOf(this.blocs);
    }

    public double getPositionY() {
        return this.positionY;
    }
}