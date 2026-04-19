/**
 *
 * @author roneh
 */

package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class FusionService {

    private static final double EPSILON = 0.0001;
    private static final double PAS_ECHANTILLON = 0.5;

    /**
     * Retourne des groupes temporaires de zones ArmatureBlocs contiguës.
     * Cette fusion est uniquement logique pour la simulation.
     */
    public List<List<ArmatureBlocs>> trouverGroupesFusionnables(
            List<Zone> zones,
            double hauteurBloc
    ) {
        List<ArmatureBlocs> armatures = zones.stream()
                .filter(ArmatureBlocs.class::isInstance)
                .map(ArmatureBlocs.class::cast)
                .toList();

        List<List<ArmatureBlocs>> groupes = new ArrayList<>();
        boolean[] visites = new boolean[armatures.size()];

        for (int i = 0; i < armatures.size(); i++) {
            if (visites[i]) {
                continue;
            }

            List<ArmatureBlocs> groupe = new ArrayList<>();
            ArrayDeque<Integer> pile = new ArrayDeque<>();
            pile.push(i);
            visites[i] = true;

            while (!pile.isEmpty()) {
                int courant = pile.pop();
                ArmatureBlocs zoneCourante = armatures.get(courant);
                groupe.add(zoneCourante);

                for (int j = 0; j < armatures.size(); j++) {
                    if (visites[j]) {
                        continue;
                    }

                    if (sontFusionnables(zoneCourante, armatures.get(j), hauteurBloc)) {
                        visites[j] = true;
                        pile.push(j);
                    }
                }
            }

            groupes.add(groupe);
        }

        return groupes;
    }

    /**
     * Deux zones sont fusionnables seulement si :
     * - elles ont une bande verticale commune d'au moins hauteurBloc
     * - et sur cette bande, leurs segments horizontaux se touchent réellement
     */
    private boolean sontFusionnables(
            ArmatureBlocs a,
            ArmatureBlocs b,
            double hauteurBloc
    ) {
        if (a == null || b == null || hauteurBloc <= 0) {
            return false;
        }

        double aMinY = a.getPoints().stream().mapToDouble(p -> p.y).min().orElse(0.0);
        double aMaxY = a.getPoints().stream().mapToDouble(p -> p.y).max().orElse(0.0);

        double bMinY = b.getPoints().stream().mapToDouble(p -> p.y).min().orElse(0.0);
        double bMaxY = b.getPoints().stream().mapToDouble(p -> p.y).max().orElse(0.0);

        double debutCommun = Math.max(aMinY, bMinY);
        double finCommun = Math.min(aMaxY, bMaxY);

        if (finCommun - debutCommun + EPSILON < hauteurBloc) {
            return false;
        }

        double longueurContinue = 0.0;
        double pas = Math.min(PAS_ECHANTILLON, hauteurBloc / 4.0);
        if (pas <= 0) {
            pas = PAS_ECHANTILLON;
        }

        for (double y = debutCommun + pas / 2.0; y < finCommun - pas / 2.0 + EPSILON; y += pas) {
            boolean contactIci = segmentsSeTouchent(
                    GeometryService.segmentsHorizontaux(a, y),
                    GeometryService.segmentsHorizontaux(b, y)
            );

            if (contactIci) {
                longueurContinue += pas;
                if (longueurContinue + EPSILON >= hauteurBloc) {
                    return true;
                }
            } else {
                longueurContinue = 0.0;
            }
        }

        return false;
    }

    /**
     * Retourne vrai si au moins un segment de A touche ou chevauche un segment de B.
     */
    private boolean segmentsSeTouchent(
            List<GeometryService.SegmentHorizontal> segmentsA,
            List<GeometryService.SegmentHorizontal> segmentsB
    ) {
        if (segmentsA == null || segmentsB == null || segmentsA.isEmpty() || segmentsB.isEmpty()) {
            return false;
        }

        for (GeometryService.SegmentHorizontal sa : segmentsA) {
            if (sa == null) {
                continue;
            }

            for (GeometryService.SegmentHorizontal sb : segmentsB) {
                if (sb == null) {
                    continue;
                }

                boolean contactOuChevauchement =
                        sa.xMax() >= sb.xMin() - EPSILON
                        && sb.xMax() >= sa.xMin() - EPSILON;

                if (contactOuChevauchement) {
                    return true;
                }
            }
        }

        return false;
    }
}

