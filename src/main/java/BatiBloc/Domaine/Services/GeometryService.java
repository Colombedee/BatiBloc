package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author nicky
 */
public final class GeometryService {

    private static final double EPSILON = 0.0001;

    private GeometryService() {}

    public record SegmentHorizontal(double xMin, double xMax) {
        public double largeur() {
            return Math.max(0.0, xMax - xMin);
        }
    }


    public static boolean pointDansZone(Point2D.Double p, Zone zone) {
        if (p == null || zone == null) {
            return false;
        }
        return pointDansPolygone(p, zone.getPoints());
    }


    public static boolean pointDansPolygone(Point2D.Double p, List<Point2D.Double> poly) {
        if (p == null || poly == null || poly.size() < 3) {
            return false;
        }

        boolean inside = false;
        int n = poly.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point2D.Double pi = poly.get(i);
            Point2D.Double pj = poly.get(j);

            if (pi == null || pj == null) {
                continue;
            }

            boolean intersect = ((pi.y > p.y) != (pj.y > p.y))
                    && (p.x < (pj.x - pi.x) * (p.y - pi.y) / (pj.y - pi.y) + pi.x);

            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }


    public static double largeurDisponible(Zone zone, double y) {
        if (zone == null) {
            return 0.0;
        }

        return segmentsHorizontaux(zone, y).stream()
                .mapToDouble(SegmentHorizontal::largeur)
                .sum();
    }


    public static List<SegmentHorizontal> segmentsHorizontaux(Zone zone, double y) {
        if (zone == null) {
            return List.of();
        }

        List<Point2D.Double> pts = zone.getPoints();
        if (pts == null || pts.size() < 3) {
            return List.of();
        }

        List<Double> intersections = new ArrayList<>();

        for (int i = 0; i < pts.size(); i++) {
            Point2D.Double p1 = pts.get(i);
            Point2D.Double p2 = pts.get((i + 1) % pts.size());

            if (p1 == null || p2 == null) {
                continue;
            }

            // Règle semi-ouverte pour éviter de compter deux fois un sommet
            boolean coupe = (p1.y <= y && p2.y > y) || (p2.y <= y && p1.y > y);
            if (!coupe || Math.abs(p1.y - p2.y) < EPSILON) {
                continue;
            }

            double t = (y - p1.y) / (p2.y - p1.y);
            double x = p1.x + t * (p2.x - p1.x);
            intersections.add(x);
        }

        if (intersections.size() < 2) {
            return List.of();
        }

        Collections.sort(intersections);

        List<SegmentHorizontal> segments = new ArrayList<>();
        for (int i = 0; i + 1 < intersections.size(); i += 2) {
            double x1 = intersections.get(i);
            double x2 = intersections.get(i + 1);

            if (x2 - x1 > EPSILON) {
                segments.add(new SegmentHorizontal(x1, x2));
            }
        }

        return segments;
    }


    public static List<SegmentHorizontal> segmentsConstructibles(
            ArmatureBlocs armature,
            List<Ouverture> ouvertures,
            double yBaseRangee,
            double hauteurBloc
    ) {
        if (armature == null || hauteurBloc <= 0) {
            return List.of();
        }

        double yEchantillon = yBaseRangee + (hauteurBloc / 2.0);

        List<SegmentHorizontal> segments =
                new ArrayList<>(segmentsHorizontaux(armature, yEchantillon));

        if (segments.isEmpty()) {
            return List.of();
        }

        if (ouvertures != null) {
            for (Ouverture ouverture : ouvertures) {
                if (ouverture == null) {
                    continue;
                }

                if (!chevaucheVerticalement(ouverture, yBaseRangee, hauteurBloc)) {
                    continue;
                }

                List<SegmentHorizontal> retraits =
                        segmentsHorizontaux(ouverture, yEchantillon);

                segments = soustraireSegments(segments, retraits);
            }
        }

        segments.sort(Comparator.comparingDouble(SegmentHorizontal::xMin));
        return segments;
    }


    private static boolean chevaucheVerticalement(Zone zone, double yBase, double hauteur) {
        if (zone == null || hauteur <= 0) {
            return false;
        }

        double minY = zone.getPoints().stream()
                .mapToDouble(p -> p.y)
                .min()
                .orElse(0.0);

        double maxY = zone.getPoints().stream()
                .mapToDouble(p -> p.y)
                .max()
                .orElse(0.0);

        double yTop = yBase;
        double yBottom = yBase + hauteur;

        return maxY > yTop + EPSILON && minY < yBottom - EPSILON;
    }


    private static List<SegmentHorizontal> soustraireSegments(
            List<SegmentHorizontal> bases,
            List<SegmentHorizontal> retraits
    ) {
        if (bases == null || bases.isEmpty()) {
            return List.of();
        }

        if (retraits == null || retraits.isEmpty()) {
            return new ArrayList<>(bases);
        }

        List<SegmentHorizontal> resultat = new ArrayList<>(bases);

        for (SegmentHorizontal retrait : retraits) {
            List<SegmentHorizontal> nouveau = new ArrayList<>();

            for (SegmentHorizontal base : resultat) {
                if (retrait.xMax() <= base.xMin() + EPSILON
                        || retrait.xMin() >= base.xMax() - EPSILON) {
                    nouveau.add(base);
                    continue;
                }

                if (retrait.xMin() > base.xMin() + EPSILON) {
                    nouveau.add(new SegmentHorizontal(
                            base.xMin(),
                            Math.min(retrait.xMin(), base.xMax())
                    ));
                }

                if (retrait.xMax() < base.xMax() - EPSILON) {
                    nouveau.add(new SegmentHorizontal(
                            Math.max(retrait.xMax(), base.xMin()),
                            base.xMax()
                    ));
                }
            }

            resultat = nouveau;
        }

        return resultat;
    }
}
