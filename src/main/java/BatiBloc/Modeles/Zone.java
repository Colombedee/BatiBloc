package BatiBloc.Modeles;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone 
{

    protected Forme forme; //Modifié Colombe. Zone contient forme
    protected double largeurBloc;
    protected List<Ligne> lignes = new ArrayList<>();
    protected double morceauRestant = 0;

    public Zone(Forme forme, double largeurBloc) 
    {
        this.forme = forme;
        this.largeurBloc = largeurBloc;
    }
    
    public Forme getForme()
    {
        return this.forme;
    }

    public void construireLigne() 
    {
        double largeurTotale = this.forme.getLargeur();
        Ligne ligne = new Ligne();

        if (this.morceauRestant > 0) 
        {
            ligne.ajouterBloc(new Bloc(this.morceauRestant));
            this.morceauRestant = 0;
        }

        while (ligne.getLargeurActuelle() + this.largeurBloc <= largeurTotale) 
        {
            ligne.ajouterBloc(new Bloc(this.largeurBloc));
        }

        double espaceRestant = largeurTotale - ligne.getLargeurActuelle();
        if (espaceRestant > 0) 
        {
            ligne.ajouterBloc(new Bloc(espaceRestant));
            this.morceauRestant = this.largeurBloc - espaceRestant;
        }

        this.lignes.add(ligne);
    }

    public boolean estComplete() 
    {
        double hauteurTotale = this.forme.getHauteur();
        return this.lignes.size() * this.getHauteurBloc() >= hauteurTotale;    
    }

    public abstract double getHauteurBloc();
}