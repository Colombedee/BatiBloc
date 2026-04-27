package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Formes.Rectangle;
import BatiBloc.Domaine.Modeles.Formes.Triangle;
import BatiBloc.Domaine.Modeles.Formes.TriangleTronque;
import BatiBloc.Domaine.Modeles.Formes.Forme;
import BatiBloc.Domaine.Modeles.Vue;
import BatiBloc.Domaine.Modeles.Zones.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nicky
 */
public class CopieProfondeService {

    // 1. Copie des listes de vues
    public List<Vue> copierVues(List<Vue> vuesSource) {
        if (vuesSource == null) {
            return new ArrayList<>();
        }
        return vuesSource.stream().map(this::copierVue).toList();
    }

    // 2. Copie d'une vue
    public Vue copierVue(Vue vueSource) {
        Vue copie = new Vue(copierImage(vueSource.getImage()));
        copie.setEchelle(vueSource.getEchelle());
        copie.setZoom(vueSource.getZoom());

        Zone zoneSelectionneeSource = vueSource.getZoneSelectionnee();
        Zone zoneSelectionneeCopie = null;

        for (Zone zoneSource : vueSource.getZones()) {
            Zone zoneCopie = copierZone(zoneSource);
            if (zoneCopie != null) {
                copie.ajouterZone(zoneCopie);
                if (zoneSource == zoneSelectionneeSource) {
                    zoneSelectionneeCopie = zoneCopie;
                }
            }
        }
        copie.definirZoneSelectionnee(zoneSelectionneeCopie);
        return copie;
    }

    // 3. Copie d'une zone
    public Zone copierZone(Zone zoneSource) {
        if (zoneSource == null) {
            return null;
        }

        List<Point2D.Double> pts = copierPoints2D(zoneSource.getPoints());
        Forme formeCopie = copierForme(zoneSource.getForme(), pts);

        if (zoneSource instanceof ArmatureClassique armatureClassique) {
            return new ArmatureClassique(formeCopie, pts, armatureClassique.isAutomatique());
        }
        if (zoneSource instanceof ArmatureBlocs) {
            return new ArmatureBlocs(formeCopie, pts);
        }
        if (zoneSource instanceof Ouverture) {
            return new Ouverture(formeCopie, pts);
        }
        return null;
    }

    // 4. Copie d'une forme
    private Forme copierForme(Forme formeSource, List<Point2D.Double> pts) {
        if (formeSource instanceof Rectangle) {
            return new Rectangle(pts);
        }
        if (formeSource instanceof Triangle) {
            return new Triangle(pts);
        }
        // Par défaut, TriangleTronque
        return new TriangleTronque(pts);
    }

    // 5. Copie d'une image
    private BufferedImage copierImage(BufferedImage imageSource) {
        if (imageSource == null) {
            return null;
        }

        int type = imageSource.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : imageSource.getType();
        BufferedImage copie = new BufferedImage(imageSource.getWidth(), imageSource.getHeight(), type);
        Graphics2D g2d = copie.createGraphics();
        g2d.drawImage(imageSource, 0, 0, null);
        g2d.dispose();
        return copie;
    }

    // 6. Copie d'une liste de points 2D
    private List<Point2D.Double> copierPoints2D(List<Point2D.Double> src) {
        if (src == null) {
            return new ArrayList<>();
        }
        return src.stream().map(p -> new Point2D.Double(p.x, p.y)).toList();
    }
}
