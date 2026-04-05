package BatiBloc.Domaine.Modeles;

import java.util.ArrayList;
import java.util.List;

public abstract class Zone {

    protected static final double LONGUEUR_MINIMALE_BLOC = 6.0;
    protected static final double HAUTEUR_BLOC_DEFAUT = 12.0;
    protected static final double EPSILON = 0.0001;

    protected Forme forme;
    protected double largeurBloc;
    protected double hauteurBloc;

    protected List<Ligne> lignes = new ArrayList<>();
    protected double morceauRestant = 0.0;

    public Zone(Forme forme, double largeurBloc) {
        this(forme, largeurBloc, HAUTEUR_BLOC_DEFAUT);
    }

    public Zone(Forme forme, double largeurBloc, double hauteurBloc) {
        this.forme = forme;
        this.largeurBloc = largeurBloc > 0 ? largeurBloc : LONGUEUR_MINIMALE_BLOC;
        this.hauteurBloc = hauteurBloc > 0 ? hauteurBloc : HAUTEUR_BLOC_DEFAUT;
    }

    public Forme getForme() {
        return this.forme;
    }

    public double getLargeurBloc() {
        return this.largeurBloc;
    }

    public double getHauteurBloc() {
        return this.hauteurBloc;
    }

    public void setLargeurBloc(double largeurBloc) {
        if (largeurBloc >= LONGUEUR_MINIMALE_BLOC) {
            this.largeurBloc = largeurBloc;
            this.lignes.clear();
            this.morceauRestant = 0.0;
        }
    }

    public void setHauteurBloc(double hauteurBloc) {
        if (hauteurBloc > 0) {
            this.hauteurBloc = hauteurBloc;
            this.lignes.clear();
            this.morceauRestant = 0.0;
        }
    }

    public double getLargeurTotale() {
        return this.forme.getLargeur();
    }

    /**
     * Pour le livrable 4, on ajuste la hauteur au multiple supérieur
     * de la hauteur de bloc pour les zones simulées.
     */
    public double getHauteurTotale() {
        double hauteurReelle = this.forme.getHauteur();

        if (this.hauteurBloc <= 0) {
            return hauteurReelle;
        }

        return Math.ceil(hauteurReelle / this.hauteurBloc) * this.hauteurBloc;
    }

    public List<Ligne> getLignes() {
        return List.copyOf(this.lignes);
    }

    /**
     * Détermine la largeur du premier bloc "neuf" à placer
     * afin d'éviter un dernier morceau inférieur à 6 pouces.
     */
    private double calculerLargeurPremierBloc(double largeurDisponible) {
        if (largeurDisponible < LONGUEUR_MINIMALE_BLOC) {
            return 0.0;
        }

        if (largeurDisponible <= this.largeurBloc + EPSILON) {
            return largeurDisponible;
        }

        double reste = largeurDisponible % this.largeurBloc;

        if (reste < EPSILON || reste + EPSILON >= LONGUEUR_MINIMALE_BLOC) {
            return this.largeurBloc;
        }

        double delta = LONGUEUR_MINIMALE_BLOC - reste;
        double largeurAjustee = this.largeurBloc - delta;

        if (largeurAjustee + EPSILON >= LONGUEUR_MINIMALE_BLOC) {
            return largeurAjustee;
        }

        return this.largeurBloc;
    }

    /**
     * Version minimale  pour le livrable 4 :
     * - placement gauche -> droite
     * - réutilisation du morceau restant
     * - aucun morceau < 6 pouces
     *
     * Cette logique est adaptée à une simulation rectangulaire.
     * Les triangles / tronqués devront être traités plus finement plus tard.
     */
    public void construireLigne() {
        double largeurZone = getLargeurTotale();

        if (largeurZone < LONGUEUR_MINIMALE_BLOC || this.largeurBloc < LONGUEUR_MINIMALE_BLOC) {
            return;
        }

        Ligne ligne = new Ligne();
        double largeurUtilisee = 0.0;

        // 1. Réutilisation de la chute précédente si elle est valide
        if (this.morceauRestant + EPSILON >= LONGUEUR_MINIMALE_BLOC) {
            double largeurReprise = Math.min(this.morceauRestant, largeurZone);

            if (largeurReprise + EPSILON >= LONGUEUR_MINIMALE_BLOC) {
                ligne.ajouterBloc(new Bloc(largeurReprise));
                largeurUtilisee += largeurReprise;
            }

            this.morceauRestant = 0.0;
        } else {
            this.morceauRestant = 0.0;
        }

        double largeurDisponible = largeurZone - largeurUtilisee;

        if (largeurDisponible + EPSILON < LONGUEUR_MINIMALE_BLOC) {
            if (!ligne.getBlocs().isEmpty()) {
                this.lignes.add(ligne);
            }
            return;
        }

        // 2. Premier bloc "neuf", potentiellement ajusté
        double premiereLargeur = calculerLargeurPremierBloc(largeurDisponible);
        if (premiereLargeur > 0) {
            ligne.ajouterBloc(new Bloc(premiereLargeur));
            largeurUtilisee += premiereLargeur;
        }

        // 3. Blocs complets suivants
        while (largeurUtilisee + this.largeurBloc <= largeurZone + EPSILON) {
            double resteApresAjout = largeurZone - (largeurUtilisee + this.largeurBloc);

            if (resteApresAjout > EPSILON && resteApresAjout < LONGUEUR_MINIMALE_BLOC) {
                break;
            }

            ligne.ajouterBloc(new Bloc(this.largeurBloc));
            largeurUtilisee += this.largeurBloc;
        }

        // 4. Dernier morceau éventuel
        double espaceRestant = largeurZone - largeurUtilisee;

        if (espaceRestant + EPSILON >= LONGUEUR_MINIMALE_BLOC) {
            ligne.ajouterBloc(new Bloc(espaceRestant));

            double chute = this.largeurBloc - espaceRestant;
            if (chute + EPSILON >= LONGUEUR_MINIMALE_BLOC) {
                this.morceauRestant = chute;
            } else {
                this.morceauRestant = 0.0;
            }
        } else {
            this.morceauRestant = 0.0;
        }

        if (!ligne.getBlocs().isEmpty()) {
            this.lignes.add(ligne);
        }
    }

    public boolean estComplete() {
        return this.lignes.size() * getHauteurBloc() >= getHauteurTotale();
    }
}
   