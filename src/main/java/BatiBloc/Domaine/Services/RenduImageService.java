package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Bloc;
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
 * @author nicky modifier par @roneh 
 */
public class RenduImageService {

    private static final Color COULEUR_ARMATURE_CLASSIQUE = new Color(34, 139, 34, 80);
    private static final Color COULEUR_ARMATURE_BLOCS = new Color(0, 0, 255, 80);
    private static final Color COULEUR_OUVERTURE = new Color(255, 165, 0, 80);

    private static final Color COULEUR_BLOC_PAIR = new Color(255, 200, 100, 180);
    private static final Color COULEUR_BLOC_IMPAIR = new Color(255, 230, 150, 180);
    private static final Color COULEUR_CONTOUR_BLOC = new Color(150, 80, 0, 200);

    private static final int ZONE_STROKE_WIDTH = 2;

    public BufferedImage genererImageAvecZones(Vue vue) {
        BufferedImage original = vue.getImage();

        if (original == null) {
            return null;
        }

        BufferedImage resultat = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resultat.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.drawImage(original, 0, 0, null);

        for (Zone zone : vue.getZones()) {
            dessinerZone(g, zone);
        }

        g.dispose();
        return resultat;
    }

    private void dessinerZone(Graphics2D g, Zone zone) {
        List<Point2D.Double> pts = zone.getPoints();

        int[] xs = pts.stream().mapToInt(p -> (int) Math.round(p.x)).toArray();
        int[] ys = pts.stream().mapToInt(p -> (int) Math.round(p.y)).toArray();

        Color couleur = determinerCouleurZone(zone);

        g.setColor(couleur);
        g.fillPolygon(xs, ys, pts.size());

        if (zone instanceof ArmatureBlocs armatureBlocs) {
            dessinerBlocs(g, armatureBlocs);
        }

        g.setColor(couleur.darker());
        g.setStroke(new BasicStroke(ZONE_STROKE_WIDTH));
        g.drawPolygon(xs, ys, pts.size());
    }

    private void dessinerBlocs(Graphics2D g, ArmatureBlocs armatureBlocs) {
        List<Bloc> blocs = armatureBlocs.getBlocsSimules();

        if (blocs.isEmpty()) {
            return;
        }

        List<Point2D.Double> pts = armatureBlocs.getPoints();

        int[] xs = pts.stream().mapToInt(p -> (int) Math.round(p.x)).toArray();
        int[] ys = pts.stream().mapToInt(p -> (int) Math.round(p.y)).toArray();

        Polygon clipZone = new Polygon(xs, ys, pts.size());
        Shape ancienClip = g.getClip();

        g.setClip(clipZone);

        double dernierY = Double.NaN;
        int indexRangee = -1;

        for (Bloc bloc : blocs) {
            int bx = (int) Math.round(bloc.getX());
            int by = (int) Math.round(bloc.getY());
            int bw = (int) Math.round(bloc.getLargeur());
            int bh = (int) Math.round(bloc.getHauteur());

            if (bw <= 0 || bh <= 0) {
                continue;
            }

            if (Double.isNaN(dernierY) || Math.abs(bloc.getY() - dernierY) > 0.5) {
                dernierY = bloc.getY();
                indexRangee++;
            }

            boolean rangeePaire = indexRangee % 2 == 0;

            g.setColor(rangeePaire ? COULEUR_BLOC_PAIR : COULEUR_BLOC_IMPAIR);
            g.fillRect(bx, by, bw, bh);

            g.setColor(COULEUR_CONTOUR_BLOC);
            g.setStroke(new BasicStroke(1.0f));
            g.drawRect(bx, by, bw, bh);
        }

        g.setClip(ancienClip);
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