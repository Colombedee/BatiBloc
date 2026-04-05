package BatiBloc.Domaine.Modeles;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Vue 
{
    private BufferedImage image;
    private double echelle = 1.0; // 1 pixel = 1 unité interne
    private double zoom = 1.0;
    private double origineX = 0;
    private double origineY = 0;

    private final List<Zone> zones = new ArrayList<>();
    private Zone zoneSelectionnee = null;

    public Vue(BufferedImage image) 
    {
        this.image = image;
    }

    public BufferedImage getImage() 
    {
        return image;
    }

    public void setImage(BufferedImage image) 
    {
        this.image = image;
    }

    public double getEchelle() 
    {
        return echelle;
    }

    public void setEchelle(double echelle) 
    {
        this.echelle = echelle;
    }

    public double getZoom() 
    {
        return zoom;
    }

    public void setZoom(double zoom) 
    {
        this.zoom = zoom;
    }

    public double getOrigineX() 
    {
        return origineX;
    }

    public double getOrigineY() 
    {
        return origineY;
    }

    public void deplacerOrigine(double dx, double dy) 
    {
        this.origineX += dx;
        this.origineY += dy;
    }

    public List<Zone> getZones() //Colombe
    {
        return List.copyOf(zones);
    }

    public void ajouterZone(Zone z) 
    {
        zones.add(z);
    }

    public void supprimerZone(int index) //Colombe 
    {
        if (index >= 0 && index < zones.size())
            {
                if (zones.get(index) == zoneSelectionnee)
                {
                    zoneSelectionnee = null;
                }
                zones.remove(index);
            }
    }
    
     public void selectionnerZone(Point point) //Colombe 
    {
        zoneSelectionnee = null;
        for (Zone z : zones)
            {
                if (z.getForme().contientPoint(point)) //Colombe 
                    {
                        zoneSelectionnee = z;
                        break;
                    }
            }
    }
     
      public void deplacerZoneSelectionnee(int dx, int dy) //Colombe  
    {
        if (zoneSelectionnee != null)
            {
                zoneSelectionnee.getForme().deplacement(dx, dy);
            }
    }
      //Colombe 
      public void redimensionnerZoneSelectionnee(int indexPoignee, int dx, int dy) 
    {
        if (zoneSelectionnee != null) 
        {
            zoneSelectionnee.getForme().redimensionner(indexPoignee, dx, dy);
        }
    }
      public void redimensionnerParBoundingBox(int indexPoignee, int dx, int dy) 
    {
        Zone zone = getZoneSelectionnee();
        if (zone == null) return;

        Forme forme = zone.getForme();
        List<java.awt.geom.Point2D.Double> pts = forme.positionPrecise;

        double xMin = pts.stream().mapToDouble(p -> p.x).min().orElse(0);
        double yMin = pts.stream().mapToDouble(p -> p.y).min().orElse(0);
        double xMax = pts.stream().mapToDouble(p -> p.x).max().orElse(0);
        double yMax = pts.stream().mapToDouble(p -> p.y).max().orElse(0);

        double largeur = xMax - xMin;
        double hauteur = yMax - yMin;

        boolean affecteGauche = indexPoignee == 0 || indexPoignee == 6 || indexPoignee == 7;
        boolean affecteDroite = indexPoignee == 2 || indexPoignee == 3 || indexPoignee == 4;
        boolean affecteHaut   = indexPoignee == 0 || indexPoignee == 1 || indexPoignee == 2;
        boolean affecteBas    = indexPoignee == 4 || indexPoignee == 5 || indexPoignee == 6;

        double nouvelleLargeur = largeur;
        double nouvelleHauteur = hauteur;

        if (affecteDroite) nouvelleLargeur = largeur + dx;
        if (affecteGauche) nouvelleLargeur = largeur - dx;
        if (affecteBas)    nouvelleHauteur = hauteur + dy;
        if (affecteHaut)   nouvelleHauteur = hauteur - dy;

        if (nouvelleLargeur < 10 || nouvelleHauteur < 10) return;

        double scaleX = nouvelleLargeur / largeur;
        double scaleY = nouvelleHauteur / hauteur;

        double ancreX = affecteGauche ? xMax : xMin;
        double ancreY = affecteHaut   ? yMax : yMin;

        for (int i = 0; i < pts.size(); i++) {
            java.awt.geom.Point2D.Double p = pts.get(i);

            if (affecteDroite || affecteGauche) {
                p.x = ancreX + (p.x - ancreX) * scaleX;
            }
            if (affecteBas || affecteHaut) {
                p.y = ancreY + (p.y - ancreY) * scaleY;
            }

            // Synchroniser le Point entier avec la valeur précise
            forme.getPointsInternes().get(i).x = (int) Math.round(p.x);
            forme.getPointsInternes().get(i).y = (int) Math.round(p.y);
        }
    }
       public Zone getZoneSelectionnee() //Colombe  
    {
        return zoneSelectionnee;
    }
}