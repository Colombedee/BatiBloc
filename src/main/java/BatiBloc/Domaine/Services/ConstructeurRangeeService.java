package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import BatiBloc.Domaine.Constantes.ConstantesBloc;

public class ConstructeurRangeeService {

    public ResultatRangee construire(double largeurDisponible, double largeurBloc,
                            double hauteurBloc, double prixBloc, double hauteur, double restePrecedent) {

        RangeeBloc rangee = new RangeeBloc(hauteur);
        double x = 0;

        if (restePrecedent >= ConstantesBloc.LARGEUR_MINIMALE) {
            rangee.ajouterBloc(new Bloc(hauteurBloc, restePrecedent, prixBloc));
            x += restePrecedent;
        }

        while (x + largeurBloc <= largeurDisponible) {
            rangee.ajouterBloc(new Bloc(hauteurBloc, largeurBloc, prixBloc));
            x += largeurBloc;
        }

        double restant = largeurDisponible - x;
        double restePourProchaineRangee = 0;

        if (restant >= ConstantesBloc.LARGEUR_MINIMALE) {
            rangee.ajouterBloc(new Bloc(hauteurBloc, restant, prixBloc));
            restePourProchaineRangee = largeurBloc - restant;
        }

        return new ResultatRangee(restePourProchaineRangee, rangee);
    }
}