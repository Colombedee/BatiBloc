package BatiBloc.Affichage;

import BatiBloc.Dto.VueDTO;
import BatiBloc.Dto.ZoneDTO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Afficheur {

    public void dessiner(Graphics2D g2, VueDTO vue, List<ZoneDTO> zones) {

        // Dessin du PDF
        BufferedImage img = vue.getImage();
        if (img != null) {
            g2.drawImage(img, 0, 0, null);
        }

        if (zones == null) return;

        // Dessin des zones
        for (ZoneDTO z : zones) {

            java.util.List<Point> pts = z.getPoints();
            int[] xs = pts.stream().mapToInt(p -> p.x).toArray();
            int[] ys = pts.stream().mapToInt(p -> p.y).toArray();

            // Couleur selon type
            Color couleur = switch (z.getType()) {
                case ARMATURE_CLASSIQUE -> new Color(34, 139, 34, 80);
                case ARMATURE_BLOCS -> new Color(0, 0, 255, 80);
                default -> new Color(255, 165, 0, 80);
            };

            // Remplissage
            g2.setColor(couleur);
            g2.fillPolygon(xs, ys, pts.size());

            // Contour
            g2.setStroke(new BasicStroke(z.isSelectionnee() ? 3 : 2));
            g2.setColor(z.isSelectionnee() ? Color.YELLOW : couleur.darker());
            g2.drawPolygon(xs, ys, pts.size());
        }
    }
}