/**
 *
 * @author roneh
 */

package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Formes.Rectangle;
import BatiBloc.Domaine.Modeles.Vue;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.ArmatureClassique;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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

    public void synchroniserArmaturesAutomatiques(
            Vue vue,
            double hauteurBloc,
            double largeurBloc
    ) {
        if (vue == null || hauteurBloc <= 0 || largeurBloc <= 0) {
            return;
        }

        vue.supprimerArmaturesAutomatiques();

        List<ArmatureClassique> armaturesAutomatiques =
                creerArmaturesTraditionnellesAutomatiques(
                        vue.getZones(),
                        hauteurBloc,
                        largeurBloc
                );

        vue.insererZonesAvantOuvertures(armaturesAutomatiques);
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

    public List<ArmatureClassique> creerArmaturesTraditionnellesAutomatiques(
            List<Zone> zones,
            double hauteurBloc,
            double largeurBloc
    ) {
        if (zones == null || zones.isEmpty() || hauteurBloc <= 0 || largeurBloc <= 0) {
            return List.of();
        }

        List<ArmatureBlocs> zonesBlocs = zones.stream()
                .filter(ArmatureBlocs.class::isInstance)
                .map(ArmatureBlocs.class::cast)
                .toList();

        if (zonesBlocs.isEmpty()) {
            return List.of();
        }

        List<ArmatureClassique> resultat = new ArrayList<>();

        for (Zone zone : zones) {
            if (!(zone instanceof Ouverture ouverture)) {
                continue;
            }

            OuvertureInfo info = analyserOuverture(ouverture, hauteurBloc, largeurBloc);

            if (info == null || !info.necessiteArmatureTraditionnelle()) {
                continue;
            }

            if (!interagitAvecAuMoinsUneZoneBlocs(info, hauteurBloc, zonesBlocs)) {
                continue;
            }

            ajouterArmaturesAutourOuverture(resultat, info, hauteurBloc);
        }

        return resultat;
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

    private void ajouterArmaturesAutourOuverture(
            List<ArmatureClassique> resultat,
            OuvertureInfo info,
            double hauteurBloc
    ) {
        double marge = MARGE_ARMATURE_TRADITIONNELLE;

        double xGaucheMin = info.xMin() - marge;
        double xGaucheMax = info.xMin();

        double xDroiteMin = info.xMax();
        double xDroiteMax = info.xMax() + marge;

        double yBasOuverture = info.yMaxAjuste();
        double yHautLinteau = yBasOuverture + hauteurBloc;

        ArmatureClassique gauche = creerRectangleAutomatique(
                xGaucheMin,
                info.yMin(),
                xGaucheMax,
                yBasOuverture
        );

        ArmatureClassique droite = creerRectangleAutomatique(
                xDroiteMin,
                info.yMin(),
                xDroiteMax,
                yBasOuverture
        );

        ArmatureClassique haut = creerRectangleAutomatique(
                xGaucheMin,
                yBasOuverture,
                xDroiteMax,
                yHautLinteau
        );

        if (gauche != null) {
            resultat.add(gauche);
        }

        if (droite != null) {
            resultat.add(droite);
        }

        if (haut != null) {
            resultat.add(haut);
        }
    }

    private ArmatureClassique creerRectangleAutomatique(
            double x1,
            double y1,
            double x2,
            double y2
    ) {
        double xMin = Math.min(x1, x2);
        double xMax = Math.max(x1, x2);
        double yMin = Math.min(y1, y2);
        double yMax = Math.max(y1, y2);

        if (xMax - xMin <= EPSILON || yMax - yMin <= EPSILON) {
            return null;
        }

        List<Point2D.Double> points = List.of(
                new Point2D.Double(xMin, yMin),
                new Point2D.Double(xMax, yMin),
                new Point2D.Double(xMax, yMax),
                new Point2D.Double(xMin, yMax)
        );

        return new ArmatureClassique(new Rectangle(points), points, true);
    }

    private boolean interagitAvecAuMoinsUneZoneBlocs(
            OuvertureInfo info,
            double hauteurBloc,
            List<ArmatureBlocs> zonesBlocs
    ) {
        double xMin = info.xMin() - MARGE_ARMATURE_TRADITIONNELLE;
        double xMax = info.xMax() + MARGE_ARMATURE_TRADITIONNELLE;
        double yMin = info.yMin();
        double yMax = info.yMaxAjuste() + hauteurBloc;

        for (ArmatureBlocs zoneBlocs : zonesBlocs) {
            if (zoneBlocs == null) {
                continue;
            }

            Boite boite = calculerBoite(zoneBlocs);

            if (boite == null) {
                continue;
            }

            boolean chevauche = xMax > boite.xMin() + EPSILON
                    && xMin < boite.xMax() - EPSILON
                    && yMax > boite.yMin() + EPSILON
                    && yMin < boite.yMax() - EPSILON;

            if (chevauche) {
                return true;
            }
        }

        return false;
    }

    private Boite calculerBoite(Zone zone) {
        if (zone == null || zone.getPoints() == null || zone.getPoints().isEmpty()) {
            return null;
        }

        double xMin = zone.getPoints().stream().mapToDouble(p -> p.x).min().orElse(0.0);
        double xMax = zone.getPoints().stream().mapToDouble(p -> p.x).max().orElse(0.0);
        double yMin = zone.getPoints().stream().mapToDouble(p -> p.y).min().orElse(0.0);
        double yMax = zone.getPoints().stream().mapToDouble(p -> p.y).max().orElse(0.0);

        if (xMax - xMin <= EPSILON || yMax - yMin <= EPSILON) {
            return null;
        }

        return new Boite(xMin, xMax, yMin, yMax);
    }

    private record Boite(double xMin, double xMax, double yMin, double yMax) {}
}
