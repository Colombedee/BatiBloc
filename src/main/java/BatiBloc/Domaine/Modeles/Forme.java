package BatiBloc.Modeles;

import java.awt.Point;
import java.util.List;

public abstract class Forme {
    
    protected List<Point> position;
    
    protected Forme(List<Point> position){
        this.position = position;
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
    
    protected boolean contientPoint(Point p){
        return false;
    }
}