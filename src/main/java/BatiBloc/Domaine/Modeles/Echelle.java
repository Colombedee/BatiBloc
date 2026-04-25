package BatiBloc.Domaine.Modeles;

import BatiBloc.Domaine.Constantes.ConstantesEchelle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Echelle {

    private final double pouces;

    public Echelle(double pouces) {
        this.pouces = pouces;
    }

    public static Echelle parse(String texte) {

        if (texte == null || texte.isBlank()) {
            throw new IllegalArgumentException("exception.scaleEmpty");
        }

        // Nettoyage : enlever espaces et caractères parasites
        texte = texte.replaceAll("[^0-9/'\"]", ""); // garde seulement chiffres, ', ", /
        texte = texte.replaceAll("\\s+", "");       // supprime espaces

        double totalPouces = 0;

        // 1. Extraire les pieds (ex: 2')
        Matcher mPieds = Pattern.compile("(\\d+)'").matcher(texte);
        if (mPieds.find()) {
            int pieds = Integer.parseInt(mPieds.group(1));
            totalPouces += pieds * ConstantesEchelle.POUCES_PAR_PIED;
            texte = texte.replaceFirst("\\d+'", "");
        }

        // 2. Extraire les pouces (ex: 5")
        Matcher mPouces = Pattern.compile("(\\d+)\"").matcher(texte);
        if (mPouces.find()) {
            int pouces = Integer.parseInt(mPouces.group(1));
            totalPouces += pouces;
            texte = texte.replaceFirst("\\d+\"", "");
        }

        // 3. Extraire fraction (ex: 1/4)
        Matcher mFraction = Pattern.compile("(\\d+)/(\\d+)").matcher(texte);
        if (mFraction.find()) {
            double num = Double.parseDouble(mFraction.group(1));
            double den = Double.parseDouble(mFraction.group(2));
            if (den == 0) throw new IllegalArgumentException("exception.scaleFraction");
            totalPouces += num / den;
            texte = texte.replaceFirst("\\d+/\\d+", "");
        }

        // 4. S'il reste des chiffres → erreur
        if (texte.matches(".*\\d.*")) {
            throw new IllegalArgumentException("exception.scaleFormat");
        }

        // 5. Normalisation automatique
        // Exemple : 15 pouces → 1' 3"
        int pieds = (int)(totalPouces / ConstantesEchelle.POUCES_PAR_PIED);
        double poucesRestants = totalPouces - pieds * ConstantesEchelle.POUCES_PAR_PIED;

        // Normaliser les fractions comme 4/4 → 1"
        if (Math.abs(poucesRestants - Math.round(poucesRestants)) < ConstantesEchelle.EPSILON) {
            poucesRestants = Math.round(poucesRestants);
        }

        totalPouces = pieds * ConstantesEchelle.POUCES_PAR_PIED + poucesRestants;

        return new Echelle(totalPouces);
    }

    public double getPouces() {
        return this.pouces;
    }

    public double getPieds() {
        return this.pouces / ConstantesEchelle.POUCES_PAR_PIED;
    }

    @Override
    public String toString() {
        int pieds = (int)(this.pouces / ConstantesEchelle.POUCES_PAR_PIED);
        double reste = this.pouces - pieds * ConstantesEchelle.POUCES_PAR_PIED;

        int poucesEntiers = (int) reste;
        double fraction = reste - poucesEntiers;

        StringBuilder sb = new StringBuilder();

        if (pieds > 0) sb.append(pieds).append("' ");
        if (poucesEntiers > 0) sb.append(poucesEntiers).append("\" ");

        // fraction en 1/2, 1/4, 1/8 si applicable
        if (fraction > ConstantesEchelle.EPSILON) {
            int num = (int)Math.round(fraction * 8);
            int den = 8;
            sb.append(num).append("/").append(den).append("\"");
        }

        return sb.toString().trim();
    }
}