package BatiBloc.Modeles;

import java.awt.Point;

public class Ouverture {
    
    private enum TypeOuverture {
        Porte,
        Fenetre
    }
    
    private TypeOuverture typeOuverture;
    
    public Ouverture(Point position, Dimension dimensions, TypeOuverture typeOuverture){

        this.typeOuverture = typeOuverture;
    }
}
