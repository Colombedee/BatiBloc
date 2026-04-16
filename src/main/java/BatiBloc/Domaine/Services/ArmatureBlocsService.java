package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Constantes.ConstantesBloc;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.RangeeBloc;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nicky
 *
 * Corrections apportées :
 * - compatibilité avec le projet actuel : aucun appel à setHauteur(...) ;
 * - compatibilité avec ConstructeurRangeeService via construireSegment(...) ;
 * - conservation d'une surcharge simuler(...) sans ouvertures pour ne pas casser
 *   le code existant ;
 * - prise en charge des segments multiples par rangée pour triangles,
 *   triangles tronqués et ouvertures.
 */
public class ArmatureBlocsService {

    private static final double EPSILON = 0.0001;
    private final ConstructeurRangeeService constructeur = new ConstructeurRangeeService();

    /**
     * Surcharge de compatibilité avec les appels existants.
     */
    public List<Bloc> simuler(
            ArmatureBlocs armature,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc
    ) {
        return simuler(armature, List.of(), hauteurBloc, largeurBloc, prixBloc);
    }

    /**
     * Simulation tenant compte des ouvertures et des formes non rectangulaires.
     */
    public List<Bloc> simuler(
            ArmatureBlocs armature,
            List<Ouverture> ouvertures,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc
    ) {
        if (armature == null || hauteurBloc <= 0 || largeurBloc <= 0 || prixBloc < 0) {
            return List.of();
        }

        List<Ouverture> ouverturesSecurisees = (ouvertures == null) ? List.of() : ouvertures;

        armature.viderRangees();
        List<Bloc> blocsUtilises = new ArrayList<>();

        double minY = armature.getPoints().stream()
                .mapToDouble(p -> p.y)
                .min()
                .orElse(0.0);

        double maxY = armature.getPoints().stream()
                .mapToDouble(p -> p.y)
                .max()
                .orElse(0.0);

        double y = minY;
        double resteBloc = 0.0;

        while (y + hauteurBloc <= maxY + EPSILON) {
            List<GeometryService.SegmentHorizontal> segments =
                    GeometryService.segmentsConstructibles(
                            armature,
                            ouverturesSecurisees,
                            y,
                            hauteurBloc
                    );

            if (segments == null || segments.isEmpty()) {
                y += hauteurBloc;
                resteBloc = 0.0;
                continue;
            }

            RangeeBloc rangeeComplete = new RangeeBloc(y);
            boolean rangeeAjoutee = false;

            for (GeometryService.SegmentHorizontal segment : segments) {
                if (segment == null || segment.largeur() < ConstantesBloc.LARGEUR_MINIMALE) {
                    continue;
                }

                /*
                 * Correction importante :
                 * on utilise construireSegment(...) et non construire(...),
                 * car le ConstructeurRangeeService actuel attend aussi
                 * la position x de départ du segment.
                 */
                ResultatRangee resultat = constructeur.construireSegment(
                        segment.xMin(),
                        segment.largeur(),
                        largeurBloc,
                        hauteurBloc,
                        prixBloc,
                        y,
                        resteBloc
                );

                if (resultat == null || resultat.rangee() == null) {
                    resteBloc = 0.0;
                    continue;
                }

                List<Bloc> blocsSegment = resultat.rangee().getBlocs();
                if (!blocsSegment.isEmpty()) {
                    for (Bloc bloc : blocsSegment) {
                        rangeeComplete.ajouterBloc(bloc);
                    }
                    rangeeAjoutee = true;
                }

                resteBloc = resultat.restePourProchaineRangee();
            }

            if (rangeeAjoutee) {
                armature.ajouterRangee(rangeeComplete);
                blocsUtilises.addAll(rangeeComplete.getBlocs());
            } else {
                resteBloc = 0.0;
            }

            y += hauteurBloc;
        }

        return blocsUtilises;
    }
}

