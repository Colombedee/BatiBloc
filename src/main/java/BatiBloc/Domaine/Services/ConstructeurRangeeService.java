package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import BatiBloc.Domaine.Constantes.ConstantesBloc;


/**
 * 
 * @author nicky
 * Correction : Ajout d'une surcharge pour la construction par segment.
 */

public class ConstructeurRangeeService {

    private static final double EPSILON = 0.0001;

    public ResultatRangee construireSegment(
            double xDepart,
            double largeurDisponible,
            double largeurBloc,
            double hauteurBloc,
            double prixBloc,
            double positionY,
            double restePrecedent
    ) {
        RangeeBloc rangee = new RangeeBloc(positionY);
        double xCourant = xDepart;

        if (restePrecedent >= ConstantesBloc.LARGEUR_MINIMALE
                && restePrecedent <= largeurDisponible + EPSILON) {
            rangee.ajouterBloc(new Bloc(xCourant, positionY, restePrecedent, hauteurBloc, prixBloc));
            xCourant += restePrecedent;
        }

        while (xCourant + largeurBloc <= xDepart + largeurDisponible + EPSILON) {
            rangee.ajouterBloc(new Bloc(xCourant, positionY, largeurBloc, hauteurBloc, prixBloc));
            xCourant += largeurBloc;
        }

        double restant = (xDepart + largeurDisponible) - xCourant;
        double restePourProchaineRangee = 0.0;

        if (restant >= ConstantesBloc.LARGEUR_MINIMALE) {
            rangee.ajouterBloc(new Bloc(xCourant, positionY, restant, hauteurBloc, prixBloc));
            restePourProchaineRangee = largeurBloc - restant;
        }

        return new ResultatRangee(restePourProchaineRangee, rangee);
    }
}