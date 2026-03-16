package BatiBloc.Modeles;

import java.awt.Point;

abstract class Zone {
    
    private Point position;
    
    private Dimension dimension;
    
    private int id;
    
    private Forme forme;
    
    public Zone(Point position, Dimension dimension, Forme formt){
        this.position = position;
        this.dimension = dimension;
    }
    
    public float CalculerSurface (){
        return 0;
    }
}
