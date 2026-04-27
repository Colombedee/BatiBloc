package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Vue;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.ArmatureClassique;
import BatiBloc.Domaine.Modeles.Zones.Zone;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
/**
 *
 * @author nicky
 */
public class RenduImageService {

    private static final Color COULEUR_ARMATURE_CLASSIQUE = new Color(34, 139, 34, 80);
    private static final Color COULEUR_ARMATURE_BLOCS = new Color(0, 0, 255, 80);
    private static final Color COULEUR_OUVERTURE = new Color(255, 165, 0, 80);
    private static final int ZONE_STROKE_WIDTH = 2;

    public BufferedImage genererImageAvecZones(Vue vue) {
        BufferedImage original = vue.getImage();
        if (original == null) {
            return null;
        }

        BufferedImage resultat = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultat.createGraphics();
        g.drawImage(original, 0, 0, null);

        for (Zone z : vue.getZones()) {
            dessinerZone(g, z);
        }
        g.dispose();
        return resultat;
    }

    private void dessinerZone(Graphics2D g, Zone zone) {
        List<Point2D.Double> pts = zone.getPoints();
        int[] xs = pts.stream().mapToInt(p -> (int) p.x).toArray();
        int[] ys = pts.stream().mapToInt(p -> (int) p.y).toArray();

        Color couleur = determinerCouleurZone(zone);
        g.setColor(couleur);
        g.fillPolygon(xs, ys, pts.size());
        g.setColor(couleur.darker());
        g.setStroke(new BasicStroke(ZONE_STROKE_WIDTH));
        g.drawPolygon(xs, ys, pts.size());
    }

    private Color determinerCouleurZone(Zone zone) {
        if (zone instanceof ArmatureClassique) {
            return COULEUR_ARMATURE_CLASSIQUE;
        } else if (zone instanceof ArmatureBlocs) {
            return COULEUR_ARMATURE_BLOCS;
        } else {
            return COULEUR_OUVERTURE;
        }
    }
}
