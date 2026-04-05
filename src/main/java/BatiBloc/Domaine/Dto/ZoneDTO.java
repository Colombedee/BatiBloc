package BatiBloc.Domaine.Dto;

import java.awt.Point;
import java.util.List;

public class ZoneDTO 
{
    private final TypeZone type;
    private final TypeForme forme;
    private final List<Point> points;
    private final boolean selectionnee;
    
    public ZoneDTO(TypeZone type, TypeForme forme, 
            List<Point> points, boolean selectionnee)
            {
                this.type = type;
                this.forme = forme;
                this.points = List.copyOf(points);
                this.selectionnee = selectionnee;
            }
    
    public TypeZone getType()
            {
                return this.type;
            }
    
    public TypeForme getForme()
            {
                return this.forme;
            }
    
    public List<Point> getPoints()
            {
                return this.points;
            }
    
    public boolean isSelectionnee()
            {
                return this.selectionnee;
            }
    public boolean contientPoint(Point p) 
    {
        java.awt.Polygon poly = new java.awt.Polygon();
        for (Point pt : this.points) {
            poly.addPoint(pt.x, pt.y);
        }
        return poly.contains(p);
    }
}