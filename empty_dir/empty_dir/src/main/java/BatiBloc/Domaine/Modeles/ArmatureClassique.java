package BatiBloc.Domaine.Modeles;

public class ArmatureClassique extends Zone 
{

    public ArmatureClassique(Forme forme, double largeurBloc) 
    {
        super(forme, largeurBloc);
    }

    @Override
    public double getHauteurBloc() 
    {
        return 20; 
    }
}