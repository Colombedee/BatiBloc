package BatiBloc.Domaine.Affichage;

import BatiBloc.Domaine.Dto.VueDTO;
import BatiBloc.Domaine.Dto.ZoneDTO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Afficheur {

    public void dessiner(Graphics2D g2, VueDTO vue, List<ZoneDTO> zones) {
        BufferedImage img = vue.getImage();
        if (img != null) {
            g2.drawImage(img, 0, 0, null);
        }

        if (zones == null) {
            return;
        }

        for (ZoneDTO z : zones) {
            List<Point> pts = z.getPoints();
            int[] xs = pts.stream().mapToInt(p -> p.x).toArray();
            int[] ys = pts.stream().mapToInt(p -> p.y).toArray();

            Color couleur = switch (z.getType()) {
                case ARMATURE_CLASSIQUE -> new Color(34, 139, 34, 80);
                case ARMATURE_BLOCS -> new Color(0, 0, 255, 80);
                default -> new Color(255, 165, 0, 80);
            };

            g2.setColor(couleur);
            g2.fillPolygon(xs, ys, pts.size());
            g2.setStroke(new BasicStroke(z.estSelectionne() ? 3 : 2));
            g2.setColor(z.estSelectionne() ? Color.YELLOW : couleur.darker());
            g2.drawPolygon(xs, ys, pts.size());
        }
    }
}