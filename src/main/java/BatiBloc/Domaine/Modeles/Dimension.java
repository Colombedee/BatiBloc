package BatiBloc.Modeles;

public class Dimension {
    
    private int largeurPieds;
    
    private int largeurPouces;
    
    private float largeurFractionnaire;
    
    private int hauteurPieds;
    
    private int hauteurPouces;
    
    private float hauteurFractionnaire;
    
    public Dimension(int largeurPieds, int largeurPouces, float largeurFractionnaire, int hauteurPieds, int hauteurPouces, float hauteurFractionnaire){
        this.largeurPieds = largeurPieds;
        this.largeurPouces = largeurPouces;
        this.largeurFractionnaire = largeurFractionnaire;
        this.hauteurPieds = hauteurPieds;
        this.hauteurPouces = hauteurPouces;
        this.hauteurFractionnaire = hauteurFractionnaire;
    }
    
    public void SetLargeur(int largeurPieds, int largeurPouces, float largeurFractionnaire){
        this.largeurPieds = largeurPieds;
        this.largeurPouces = largeurPouces;
        this.largeurFractionnaire = largeurFractionnaire;
    }
    
    public void SetHauteur(int hauteurPieds, int hauteurPouces, float hauteurFractionnaire){
        this.hauteurPieds = hauteurPieds;
        this.hauteurPouces = hauteurPouces;
        this.hauteurFractionnaire = hauteurFractionnaire;
    }
    
    public String GetLargeur(){
        return this.largeurPieds + "'" + this.largeurPouces + "\"" + this.largeurFractionnaire;
    }
    
    public String GetHauteur(){
        return this.hauteurPieds + "'" + this.hauteurPouces + "\"" + this.hauteurFractionnaire;
    }
} 