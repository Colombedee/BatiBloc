package BatiBloc.Modeles;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone {

    protected Forme forme;
    protected double largeurBloc;

    protected List<Ligne> lignes = new ArrayList<>();
    protected double morceauRestant = 0;

    public Zone(Forme forme, double largeurBloc) {
        this.forme = forme;
        this.largeurBloc = largeurBloc;
    }

    public Forme getForme() {
        return this.forme;
    }

    public double getLargeurBloc() {
        return this.largeurBloc;
    }

    public double getLargeurTotale() {
        return this.forme.getLargeur();
    }

    public double getHauteurTotale() {
        return this.forme.getHauteur();
    }

    public List<Ligne> getLignes() {
        return List.copyOf(this.lignes);
    }

    public void construireLigne() {
        Ligne ligne = new Ligne();

        if (morceauRestant > 0) {
            ligne.ajouterBloc(new Bloc(morceauRestant));
            morceauRestant = 0;
        }

        while (ligne.getLargeurActuelle() + largeurBloc <= getLargeurTotale()) {
            ligne.ajouterBloc(new Bloc(largeurBloc));
        }

        double espaceRestant = getLargeurTotale() - ligne.getLargeurActuelle();
        if (espaceRestant > 0) {
            ligne.ajouterBloc(new Bloc(espaceRestant));
            morceauRestant = largeurBloc - espaceRestant;
        }

        lignes.add(ligne);
    }

    public boolean estComplete() {
        return lignes.size() * getHauteurBloc() >= getHauteurTotale();
    }

    public abstract double getHauteurBloc();
}