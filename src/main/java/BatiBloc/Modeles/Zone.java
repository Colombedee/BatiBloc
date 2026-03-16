package BatiBloc.Modeles;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone {

    protected double largeurTotale;
    protected double hauteurTotale;
    protected double largeurBloc;

    protected List<Ligne> lignes = new ArrayList<>();
    protected double morceauRestant = 0;

    public Zone(double largeurTotale, double hauteurTotale, double largeurBloc) {
        this.largeurTotale = largeurTotale;
        this.hauteurTotale = hauteurTotale;
        this.largeurBloc = largeurBloc;
    }

    public void construireLigne() {
        Ligne ligne = new Ligne();

        if (this.morceauRestant > 0) {
            ligne.ajouterBloc(new Bloc(this.morceauRestant));
            this.morceauRestant = 0;
        }

        while (ligne.getLargeurActuelle() + this.largeurBloc <= this.largeurTotale) {
            ligne.ajouterBloc(new Bloc(this.largeurBloc));
        }

        double espaceRestant = largeurTotale - ligne.getLargeurActuelle();
        if (espaceRestant > 0) {
            ligne.ajouterBloc(new Bloc(espaceRestant));
            this.morceauRestant = this.largeurBloc - espaceRestant;
        }

        this.lignes.add(ligne);
    }

    public boolean estComplete() {
        return this.lignes.size() * this.getHauteurBloc() >= this.hauteurTotale;
    }

    public abstract double getHauteurBloc();
}