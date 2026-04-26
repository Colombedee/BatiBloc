package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Formes.Rectangle;
import BatiBloc.Domaine.Modeles.Formes.Triangle;
import BatiBloc.Domaine.Modeles.Formes.TriangleTronque;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.List;


public final class ZoneService {

    private static final double TAILLE_MIN = 1.0;

    private ZoneService() {}


    public static Zone selectionnerZone(List<Zone> zones, Point2D.Double p) {
        if (zones == null || p == null) {
            return null;
        }

        // On parcourt à l'envers pour sélectionner la zone la plus récente, donc visuellement celle qui est au-dessus.
        for (int i = zones.size() - 1; i >= 0; i--) {
            Zone z = zones.get(i);
            
            if (z != null && GeometryService.pointDansZone(p, z)) {
                return z;
            }
        }

        return null;
    }


    public static void deplacer(Zone zone, double dx, double dy) {
        if (zone == null) {
            return;
        }
        zone.deplacement(dx, dy);
    }


    public static void redimensionner(Zone zone, int indexPoignee, double dx, double dy) {
        if (zone == null) {
            return;
        }
        zone.redimensionner(indexPoignee, dx, dy);
    }


    public static void redimensionnerParBoundingBox( Zone zone, int indexPoignee, double dx,
            double dy)
    {
        if (zone == null) {
            return;
        }

        List<Point2D.Double> points = zone.getPointsInternes();
        if (points == null || points.isEmpty()) {
            return;
        }
        
      
        if (zone.getForme() instanceof TriangleTronque) {
           redimensionnerTriangleTronque(zone, indexPoignee, dx, dy);
            return;
        }

        double ancienMinX = points.stream().mapToDouble(p -> p.x).min().orElse(0.0);
        double ancienMaxX = points.stream().mapToDouble(p -> p.x).max().orElse(0.0);
        double ancienMinY = points.stream().mapToDouble(p -> p.y).min().orElse(0.0);
        double ancienMaxY = points.stream().mapToDouble(p -> p.y).max().orElse(0.0);

        double nouveauMinX = ancienMinX;
        double nouveauMaxX = ancienMaxX;
        double nouveauMinY = ancienMinY;
        double nouveauMaxY = ancienMaxY;

        switch (indexPoignee) {
            case 0 -> {
                nouveauMinX += dx;
                nouveauMinY += dy;
            }
            case 1 -> nouveauMinY += dy;
            case 2 -> {
                nouveauMaxX += dx;
                nouveauMinY += dy;
            }
            case 3 -> nouveauMaxX += dx;
            case 4 -> {
                nouveauMaxX += dx;
                nouveauMaxY += dy;
            }
            case 5 -> nouveauMaxY += dy;
            case 6 -> {
                nouveauMinX += dx;
                nouveauMaxY += dy;
            }
            case 7 -> nouveauMinX += dx;
            default -> {
                return;
            }
        }

        // Empêcher l'inversion / l'écrasement
        if (nouveauMaxX - nouveauMinX < TAILLE_MIN) {
            if (poigneeAffecteMinX(indexPoignee)) {
                nouveauMinX = nouveauMaxX - TAILLE_MIN;
            } else if (poigneeAffecteMaxX(indexPoignee)) {
                nouveauMaxX = nouveauMinX + TAILLE_MIN;
            }
        }

        if (nouveauMaxY - nouveauMinY < TAILLE_MIN) {
            if (poigneeAffecteMinY(indexPoignee)) {
                nouveauMinY = nouveauMaxY - TAILLE_MIN;
            } else if (poigneeAffecteMaxY(indexPoignee)) {
                nouveauMaxY = nouveauMinY + TAILLE_MIN;
            }
        }

        reconstruirePointsSelonBoundingBox(
                zone,
                points,
                ancienMinX, ancienMaxX, ancienMinY, ancienMaxY,
                nouveauMinX, nouveauMaxX, nouveauMinY, nouveauMaxY
        );
    }

    private static boolean poigneeAffecteMinX(int indexPoignee) {
        return indexPoignee == 0 || indexPoignee == 6 || indexPoignee == 7;
    }

    private static boolean poigneeAffecteMaxX(int indexPoignee) {
        return indexPoignee == 2 || indexPoignee == 3 || indexPoignee == 4;
    }

    private static boolean poigneeAffecteMinY(int indexPoignee) {
        return indexPoignee == 0 || indexPoignee == 1 || indexPoignee == 2;
    }

    private static boolean poigneeAffecteMaxY(int indexPoignee) {
        return indexPoignee == 4 || indexPoignee == 5 || indexPoignee == 6;
    }

    private static void reconstruirePointsSelonBoundingBox(
            Zone zone,
            List<Point2D.Double> points,
            double ancienMinX,
            double ancienMaxX,
            double ancienMinY,
            double ancienMaxY,
            double nouveauMinX,
            double nouveauMaxX,
            double nouveauMinY,
            double nouveauMaxY
    ) {
        double ancienneLargeur = Math.max(TAILLE_MIN, ancienMaxX - ancienMinX);
        double nouvelleLargeur = Math.max(TAILLE_MIN, nouveauMaxX - nouveauMinX);

        if (zone.getForme() instanceof Rectangle) {
            // Ordre attendu :
            // 0 haut-gauche, 1 haut-droit, 2 bas-droit, 3 bas-gauche
            points.get(0).x = nouveauMinX;
            points.get(0).y = nouveauMinY;

            points.get(1).x = nouveauMaxX;
            points.get(1).y = nouveauMinY;

            points.get(2).x = nouveauMaxX;
            points.get(2).y = nouveauMaxY;

            points.get(3).x = nouveauMinX;
            points.get(3).y = nouveauMaxY;
            return;
        }

        if (zone.getForme() instanceof Triangle) {
            // Ordre attendu :
            // 0 bas-gauche, 1 bas-droit, 2 sommet haut
            double ratioSommetX = (points.get(2).x - ancienMinX) / ancienneLargeur;
            ratioSommetX = borner(ratioSommetX, 0.0, 1.0);

            points.get(0).x = nouveauMinX;
            points.get(0).y = nouveauMaxY;

            points.get(1).x = nouveauMaxX;
            points.get(1).y = nouveauMaxY;

            points.get(2).x = nouveauMinX + ratioSommetX * nouvelleLargeur;
            points.get(2).y = nouveauMinY;
            return;
        }

        if (zone.getForme() instanceof TriangleTronque) {
            // Ordre attendu :
            // 0 bas-gauche, 1 bas-droit, 2 haut-droit, 3 haut-gauche
            double ratioHautDroit = (points.get(2).x - ancienMinX) / ancienneLargeur;
            double ratioHautGauche = (points.get(3).x - ancienMinX) / ancienneLargeur;

            ratioHautDroit = borner(ratioHautDroit, 0.0, 1.0);
            ratioHautGauche = borner(ratioHautGauche, 0.0, 1.0);

            if (ratioHautGauche > ratioHautDroit) {
                double tmp = ratioHautGauche;
                ratioHautGauche = ratioHautDroit;
                ratioHautDroit = tmp;
            }

            points.get(0).x = nouveauMinX;
            points.get(0).y = nouveauMaxY;

            points.get(1).x = nouveauMaxX;
            points.get(1).y = nouveauMaxY;

            points.get(2).x = nouveauMinX + ratioHautDroit * nouvelleLargeur;
            points.get(2).y = nouveauMinY;

            points.get(3).x = nouveauMinX + ratioHautGauche * nouvelleLargeur;
            points.get(3).y = nouveauMinY;
        }
    }

    private static double borner(double valeur, double min, double max) {
        return Math.max(min, Math.min(max, valeur));
    }
    
     
    private static void redimensionnerTriangleTronque( Zone zone, int indexPoignee, double dx,
        double dy)
 {
    List<Point2D.Double> points = zone.getPointsInternes();
    if (points == null || points.size() != 4) {
        return;
    }

    // Ordre attendu :
    // 0 bas-gauche, 1 bas-droit, 2 haut-droit, 3 haut-gauche
    Point2D.Double basGauche = points.get(0);
    Point2D.Double basDroit = points.get(1);
    Point2D.Double hautDroit = points.get(2);
    Point2D.Double hautGauche = points.get(3);

    double minLargeur = TAILLE_MIN;

    switch (indexPoignee) {
        case 0 -> {
            // coin haut-gauche de la bounding box :
            basGauche.x += dx;
            hautGauche.x += dx;
            hautGauche.y += dy;
            hautDroit.y += dy;
        }

        case 1 -> {
            // milieu haut :
            hautGauche.x += dx;
            hautDroit.x -= dx;
            hautGauche.y += dy;
            hautDroit.y += dy;
        }

        case 2 -> {
            // coin haut-droit :
            basDroit.x += dx;
            hautDroit.x += dx;
            hautGauche.y += dy;
            hautDroit.y += dy;
        }

        case 3 -> {
            // milieu droit
            basDroit.x += dx;
            hautDroit.x += dx;
        }

        case 4 -> {
            // coin bas-droit
            basDroit.x += dx;
            hautDroit.x += dx;
            basGauche.y += dy;
            basDroit.y += dy;
        }

        case 5 -> {
            // milieu bas
            basGauche.y += dy;
            basDroit.y += dy;
        }

        case 6 -> {
            // coin bas-gauche
            basGauche.x += dx;
            hautGauche.x += dx;
            basGauche.y += dy;
            basDroit.y += dy;
        }

        case 7 -> {
            // milieu gauche
            basGauche.x += dx;
            hautGauche.x += dx;
        }

        default -> {
            return;
        }
    }

    // Empêcher l'inversion de la petite base
    if (hautDroit.x - hautGauche.x < minLargeur) {
        double centre = (hautGauche.x + hautDroit.x) / 2.0;
        hautGauche.x = centre - minLargeur / 2.0;
        hautDroit.x = centre + minLargeur / 2.0;
    }

    // Empêcher l'inversion de la grande base
    if (basDroit.x - basGauche.x < minLargeur) {
        double centre = (basGauche.x + basDroit.x) / 2.0;
        basGauche.x = centre - minLargeur / 2.0;
        basDroit.x = centre + minLargeur / 2.0;
    }

    // Empêcher l'inversion verticale
    double yHaut = (hautGauche.y + hautDroit.y) / 2.0;
    double yBas = (basGauche.y + basDroit.y) / 2.0;

    if (yBas - yHaut < TAILLE_MIN) {
        double centreY = (yHaut + yBas) / 2.0;
        double nouveauHaut = centreY - TAILLE_MIN / 2.0;
        double nouveauBas = centreY + TAILLE_MIN / 2.0;

        hautGauche.y = nouveauHaut;
        hautDroit.y = nouveauHaut;
        basGauche.y = nouveauBas;
        basDroit.y = nouveauBas;
    }

    // S'assurer que la petite base reste à l'intérieur de la grande base
    hautGauche.x = Math.max(hautGauche.x, basGauche.x);
    hautDroit.x = Math.min(hautDroit.x, basDroit.x);

    if (hautDroit.x - hautGauche.x < minLargeur) {
        double centre = (hautGauche.x + hautDroit.x) / 2.0;
        hautGauche.x = centre - minLargeur / 2.0;
        hautDroit.x = centre + minLargeur / 2.0;

        if (hautGauche.x < basGauche.x) {
            double decalage = basGauche.x - hautGauche.x;
            hautGauche.x += decalage;
            hautDroit.x += decalage;
        }
        if (hautDroit.x > basDroit.x) {
            double decalage = hautDroit.x - basDroit.x;
            hautGauche.x -= decalage;
            hautDroit.x -= decalage;
        }
    }
}
}