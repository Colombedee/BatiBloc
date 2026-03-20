package BatiBloc.Modeles;

public class ArmatureEnBlocs extends Zone 
{

    public ArmatureEnBlocs(Forme forme,double largeurBloc) 
    {
        super(forme, largeurBloc);
    }

    @Override
    public double getHauteurBloc() 
    {
        return 30;
    }
}