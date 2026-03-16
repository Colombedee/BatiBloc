package BatiBloc.Modeles;

import BatiBloc.Modeles.Bloc;
import java.util.ArrayList;
import java.util.List;

public class Ligne {

    private List<Bloc> blocs = new ArrayList<>();

    public void ajouterBloc(Bloc bloc) {
        this.blocs.add(bloc);
    }

    public List<Bloc> getBlocs() {
        return this.blocs;
    }

    public double getLargeurActuelle() {
        return this.blocs.stream()
                    .mapToDouble(Bloc::getLargeur)
                    .sum();
    }

    public boolean estComplete(double largeurTotale) {
        return Math.abs(this.getLargeurActuelle() - largeurTotale) < 0.001;
    }

    public double getEspaceRestant(double largeurTotale) {
        return largeurTotale - this.getLargeurActuelle();
    }

    @Override
    public String toString() {
        return blocs.toString();
    }
}