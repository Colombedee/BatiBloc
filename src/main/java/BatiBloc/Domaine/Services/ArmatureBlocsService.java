package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Constantes.ConstantesBloc;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import java.util.ArrayList;
import java.util.List;

public class ArmatureBlocsService {

    private final ConstructeurRangeeService constructeur = new ConstructeurRangeeService();

    public List<Bloc> simuler(ArmatureBlocs armature) {

        List<Bloc> blocsUtilises = new ArrayList<>();

        double hauteurTotale = armature.getHauteur();
        double hauteurBloc = ConstantesBloc.HAUTEUR_DEFAUT;
        double prixBloc = ConstantesBloc.PRIX_DEFAUT; // si tu en as un
        double resteBloc = 0;
        double y = 0;

        while (y < hauteurTotale) {

            double largeurDisponible = GeometryService.largeurDisponible(armature, y);
            if (largeurDisponible < ConstantesBloc.LARGEUR_MINIMALE) break;

            ResultatRangee resultat = constructeur.construire(largeurDisponible,
                    ConstantesBloc.LARGEUR_DEFAUT, hauteurBloc, prixBloc, y, resteBloc);

            RangeeBloc rangee = resultat.rangee();
            armature.ajouterRangee(rangee);

            blocsUtilises.addAll(rangee.getBlocs());

            resteBloc = resultat.restePourProchaineRangee();
            y += hauteurBloc;
        }

        double nouvelleHauteur = armature.getRangees().size() * hauteurBloc;
        armature.setHauteur(nouvelleHauteur);

        return blocsUtilises;
    }
}