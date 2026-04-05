package BatiBloc.Domaine.Modeles;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone {

    protected Forme forme;
    protected double largeurBloc;
    protected double hauteurBloc; 
    // AJOUT : champ pour hauteur configurable (Nicky-Davis N.)

    protected List<Ligne> lignes = new ArrayList<>();
    protected double morceauRestant = 0;

    public Zone(Forme forme, double largeurBloc) {
        this.forme = forme;
        this.largeurBloc = largeurBloc;
        this.hauteurBloc = largeurBloc; 
       // Valeur par défaut : hauteur = largeur (bloc carré)
    }

    public Forme getForme() {
        return this.forme;
    }

    public double getLargeurBloc() {
        return this.largeurBloc;
    }
    
        // Mutateur pour la largeur avec réinitialisation
    public void setLargeurBloc(double largeurBloc) {
        if (largeurBloc > 0) {
            this.largeurBloc = largeurBloc;
            // Réinitialiser car les dimensions ont changé
            this.lignes.clear();
            this.morceauRestant = 0;
        }
    }

    // Mutateur pour la hauteur avec réinitialisation  
    public void setHauteurBloc(double hauteurBloc) {
        if (hauteurBloc > 0) {
            this.hauteurBloc = hauteurBloc;
            // Réinitialiser car les dimensions ont changé
            this.lignes.clear();
            this.morceauRestant = 0;
        }
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

    // MODIFIÉ : Plus abstraite, retourne le champ (les sous-classes peuvent surcharger)
    public double getHauteurBloc() {
        return this.hauteurBloc;
    }
}