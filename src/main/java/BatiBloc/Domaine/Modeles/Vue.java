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
      
       public Zone getZoneSelectionnee() //Colombe  
    {
        return zoneSelectionnee;
    }
}