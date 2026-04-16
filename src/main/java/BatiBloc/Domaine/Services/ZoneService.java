package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Service utilitaire pour les opérations de base sur les zones.
 *
 * Corrections apportées :
 * - ajout de validations défensives pour éviter les NullPointerException ;
 * - sécurisation de selectionnerZone(...) si la liste de zones ou le point est null ;
 * - sécurisation de la boucle si une zone null est présente dans la liste ;
 * - sécurisation de deplacer(...) et redimensionner(...) si la zone reçue est null ;
 * - aucune modification de la logique métier : les appels à GeometryService,
 *   deplacement(...) et redimensionner(...) restent inchangés.
 */
public final class ZoneService {

    private ZoneService() {}

    /**
     * Retourne la première zone contenant le point donné.
     *
     * Correction :
     * - retourne null immédiatement si la liste ou le point est null ;
     * - ignore les zones null pour éviter un plantage.
     */
    public static Zone selectionnerZone(List<Zone> zones, Point2D.Double p) {
        if (zones == null || p == null) {
            return null;
        }

        for (Zone z : zones) {
            // Correction : vérification défensive si une zone null se retrouve dans la liste
            if (z != null && GeometryService.pointDansZone(p, z)) {
                return z;
            }
        }

        return null;
    }

    /**
     * Déplace une zone selon un décalage dx/dy.
     *
     * Correction :
     * - ne fait rien si la zone est null.
     */
    public static void deplacer(Zone zone, double dx, double dy) {
        if (zone == null) {
            return;
        }
        zone.deplacement(dx, dy);
    }

    /**
     * Redimensionne une zone selon une poignée et un déplacement.
     *
     * Correction :
     * - ne fait rien si la zone est null.
     */
    public static void redimensionner(Zone zone, int indexPoignee, double dx, double dy) {
        if (zone == null) {
            return;
        }
        zone.redimensionner(indexPoignee, dx, dy);
    }
}
