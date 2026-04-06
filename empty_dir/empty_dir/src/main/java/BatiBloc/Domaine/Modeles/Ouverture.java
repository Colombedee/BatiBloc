package BatiBloc.Domaine.Modeles;

public class Ouverture extends Zone 
{

    public enum Type 
    { 
        PORTE, FENETRE 
    }

    private Type type;

    public Ouverture(Forme forme, double largeurBloc, Type type) 
    {
        super(forme, largeurBloc);
        this.type = type;
    }
    
    public Type getType()
    {
        return this.type;        
    }

    @Override
    public double getHauteurBloc() 
    {
        return 0;
    }
}