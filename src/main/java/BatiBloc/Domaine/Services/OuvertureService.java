/**
 *
 * @author roneh
 */

package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import java.awt.geom.Point2D;
import java.util.List;

public class OuvertureService {

    private static final double EPSILON = 0.0001;
    private static final double MARGE_MIN_OUVERTURE = 6.0;
    private static final double MARGE_ARMATURE_TRADITIONNELLE = 4.0;

    public record OuvertureInfo(
            double xMin,
            double xMax,
            double yMin,
            double yMaxAjuste,
            double largeur,
            boolean necessiteArmatureTraditionnelle
    ) {
        public double centreX() {
            return (xMin + xMax) / 2.0;
        }

        public boolean chevaucheCorps(double yBaseRangee, double hauteurBloc) {
            double yTop = yBaseRangee;
            double yBottom = yBaseRangee + hauteurBloc;
            return yMaxAjuste > yTop + EPSILON && yMin < yBottom - EPSILON;
        }

        public boolean estRangeeLinteau(double yBaseRangee, double hauteurBloc) {
            if (necessiteArmatureTraditionnelle) {
                return false;
            }
            return yBaseRangee + EPSILON >= yMaxAjuste
                    && yBaseRangee < yMaxAjuste + hauteurBloc - EPSILON;
        }

        public boolean estRangeeVideAuDessus(double yBaseRangee, double hauteurBloc) {
            if (!necessiteArmatureTraditionnelle) {
                return false;
            }
            return yBaseRangee + EPSILON >= yMaxAjuste
                    && yBaseRangee < yMaxAjuste + hauteurBloc - EPSILON;
        }
    }

    public double getLargeurMaxOuvertureAvecLinteau(double largeurBloc) {
        return largeurBloc - (2.0 * MARGE_MIN_OUVERTURE);
    }

    public double getMargeArmatureTraditionnelle() {
        return MARGE_ARMATURE_TRADITIONNELLE;
    }

    public OuvertureInfo analyserOuverture(
            Ouverture ouverture,
            double hauteurBloc,
            double largeurBloc
    ) {
        if (ouverture == null || hauteurBloc <= 0 || largeurBloc <= 0) {
            return null;
        }

        List<Point2D.Double> pts = ouverture.getPoints();
        if (pts == null || pts.isEmpty()) {
            return null;
        }

        double xMin = pts.stream().mapToDouble(p -> p.x).min().orElse(0.0);
        double xMax = pts.stream().mapToDouble(p -> p.x).max().orElse(0.0);
        double yMin = pts.stream().mapToDouble(p -> p.y).min().orElse(0.0);
        double yMax = pts.stream().mapToDouble(p -> p.y).max().orElse(0.0);

        double largeur = xMax - xMin;
        double hauteur = yMax - yMin;

        if (largeur <= EPSILON || hauteur <= EPSILON) {
            return null;
        }

        double hauteurAjustee = Math.ceil(hauteur / hauteurBloc) * hauteurBloc;
        double yMaxAjuste = yMin + hauteurAjustee;

        boolean necessiteArmatureTraditionnelle =
                largeur > getLargeurMaxOuvertureAvecLinteau(largeurBloc) + EPSILON;

        return new OuvertureInfo(
                xMin,
                xMax,
                yMin,
                yMaxAjuste,
                largeur,
                necessiteArmatureTraditionnelle
        );
    }

    public Bloc creerBlocLinteau(
            OuvertureInfo info,
            double yBaseRangee,
            double hauteurBloc,
            double largeurBloc,
            double prixBloc,
            List<GeometryService.SegmentHorizontal> segmentsBase
    ) {
        if (info == null || segmentsBase == null || segmentsBase.isEmpty()) {
            return null;
        }

        double depassementChaqueCote = (largeurBloc - info.largeur()) / 2.0;
        if (depassementChaqueCote + EPSILON < MARGE_MIN_OUVERTURE) {
            return null;
        }

        double xBloc = info.centreX() - (largeurBloc / 2.0);
        double xBlocMax = xBloc + largeurBloc;

        boolean rentreDansArmature = segmentsBase.stream().anyMatch(segment ->
                xBloc >= segment.xMin() - EPSILON && xBlocMax <= segment.xMax() + EPSILON
        );

        if (!rentreDansArmature) {
            return null;
        }

        return new Bloc(xBloc, yBaseRangee, largeurBloc, hauteurBloc, prixBloc);
    }
}
