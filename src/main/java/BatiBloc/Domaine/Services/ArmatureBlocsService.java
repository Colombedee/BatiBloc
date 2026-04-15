package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ArmatureBlocsService {

    private final ConstructeurRangeeService constructeur = new ConstructeurRangeeService();

    public List<Bloc> simuler(ArmatureBlocs armature, double hauteurBloc,
                              double largeurBloc, double prixBloc) {

        List<Bloc> blocsUtilises = new ArrayList<>();

        double hauteurTotale = armature.getHauteur();
        double resteBloc = 0;
        double y = 0;

        while (y < hauteurTotale) {

            double largeurDisponible = GeometryService.largeurDisponible(armature, y);
            if (largeurDisponible < largeurBloc) break;

            ResultatRangee resultat = constructeur.construire(largeurDisponible,
                    largeurBloc, hauteurBloc, prixBloc, y, resteBloc);

            RangeeBloc rangee = resultat.rangee();
            armature.ajouterRangee(rangee);

            blocsUtilises.addAll(rangee.getBlocs());

            resteBloc = resultat.restePourProchaineRangee();
            y += hauteurBloc;
        }

        double nouvelleHauteur = armature.getRangees().size() * hauteurBloc;
        ajusterHauteur(armature, nouvelleHauteur);

        return blocsUtilises;
    }

    private void ajusterHauteur(ArmatureBlocs armature, double nouvelleHauteur) {
        double ancienneHauteur = armature.getHauteur();
        if (ancienneHauteur == 0) {
            return;
        }

        double ratio = nouvelleHauteur / ancienneHauteur;

        for (Point2D.Double p : armature.getPointsInternes()) {
            p.y *= ratio;
        }
    }
}