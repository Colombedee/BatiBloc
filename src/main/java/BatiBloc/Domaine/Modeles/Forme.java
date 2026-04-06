package BatiBloc.Domaine.Modeles;

import java.awt.Point;
import java.util.List;

public abstract class Forme {
    
    protected List<Point> position;
    
    protected List<java.awt.geom.Point2D.Double> positionPrecise;//Colombe
    //Colombe
    protected Forme(List<Point> position) 
    {
        this.position = new java.util.ArrayList<>(position);
        // Initialiser la version précise
        this.positionPrecise = new java.util.ArrayList<>();
        for (Point p : position) {
            this.positionPrecise.add(new java.awt.geom.Point2D.Double(p.x, p.y));
        }
    }
    
    protected List<Point> getPointsInternes() {
        return this.position;
    }

    public List<Point> getPosition() {
        return List.copyOf(this.position);
    }

    public abstract double getLargeur();
    
    public abstract double getHauteur();
    
    public abstract double getAire();

    protected void deplacement(int dx, int dy) {
        for (Point p : position) {
            p.translate(dx, dy);
        }
    }
    //Colombe
    public void redimensionner(int indexPoignee, int dx, int dy) {
        if (indexPoignee >= 0 && indexPoignee < this.position.size()) {
            this.position.get(indexPoignee).translate(dx, dy);
        }
    }
    
    // @roneh pour la simulation livrable4
    public double getLargeurAHauteur(double distanceDepuisLaBase) {
        return getLargeur();
    }

    // @roneh pour la simulation livrable4
    public double getXOffsetAHauteur(double distanceDepuisLaBase) {
        return 0.0;
    }
    
    protected boolean contientPoint(Point p){
        return false;
    }
}