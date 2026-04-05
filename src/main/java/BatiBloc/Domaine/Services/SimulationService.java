package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.BlocDTO;
import BatiBloc.Domaine.Dto.SimulationDTO;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Ligne;
import BatiBloc.Domaine.Modeles.Ouverture;
import BatiBloc.Domaine.Modeles.Zone;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de simulation de placement de blocs.
 *
 * Respecte la logique MVC :
 * - le service orchestre la simulation ;
 * - le modèle Zone reste responsable de l'algorithme de construction
 *   des rangées via construireLigne() ;
 * - la vue se contente d'afficher le résultat.
 *
 * Version compatible livrable 4 :
 * - pas de simulation pour les ouvertures ;
 * - validation des dimensions ;
 * - délégation complète au modèle ;
 * - conversion du résultat en DTO pour l'affichage.
 */
public final class SimulationService {

    /** Longueur minimale d'un bloc selon l'énoncé */
    private static final double LONGUEUR_MINIMALE_BLOC = 6.0;

    /** Prix par défaut utilisé pour compatibilité immédiate */
    private static final double COUT_UNITAIRE_PAR_BLOC_DEFAUT = 20.0;

    /** Protection contre les boucles infinies */
    private static final int MAX_ITERATIONS = 10000;

    private SimulationService() {
        // Classe utilitaire : pas d'instance
    }

    /**
     * Version compatible avec votre code actuel.
     * Utilise un prix par défaut de 20.00 $.
     *
     * @param zone la zone à simuler
     * @return le résultat de simulation, ou null si la zone ne peut pas être simulée
     */
    public static SimulationDTO simuler(Zone zone) {
        return simuler(zone, COUT_UNITAIRE_PAR_BLOC_DEFAUT);
    }

    /**
     * Version extensible : permet de passer un prix unitaire configurable.
     *
     * @param zone la zone à simuler
     * @param prixUnitaireBloc prix d'un bloc
     * @return le résultat de simulation, ou null si la zone ne peut pas être simulée
     */
    public static SimulationDTO simuler(Zone zone, double prixUnitaireBloc) {
        // 1. Validation d'entrée
        if (zone == null) {
            return null;
        }

        // 2. Livrable 4 : on ne simule pas les ouvertures
        if (zone instanceof Ouverture) {
            return null;
        }

        // 3. Validation des dimensions de bloc
        if (zone.getLargeurBloc() < LONGUEUR_MINIMALE_BLOC || zone.getHauteurBloc() <= 0) {
            return new SimulationDTO(new ArrayList<>(), 0, 0.0);
        }

        // 4. Validation des dimensions de zone
        if (zone.getLargeurTotale() <= 0 || zone.getHauteurTotale() <= 0) {
            return new SimulationDTO(new ArrayList<>(), 0, 0.0);
        }

        // 5. Sécurisation du prix
        if (prixUnitaireBloc < 0) {
            prixUnitaireBloc = 0.0;
        }

        // 6. Réinitialisation de l'état de simulation
        //    On s'appuie sur le comportement déjà présent dans Zone :
        //    setLargeurBloc(...) vide les lignes et remet morceauRestant à 0.
        zone.setLargeurBloc(zone.getLargeurBloc());

        // 7. Construction ligne par ligne déléguée au modèle
        int compteurSecurite = 0;

        while (!zone.estComplete() && compteurSecurite < MAX_ITERATIONS) {
            int nombreLignesAvant = zone.getLignes().size();

            zone.construireLigne();
            compteurSecurite++;

            // Sécurité supplémentaire :
            // si construireLigne() n'ajoute rien, on arrête pour éviter une boucle infinie
            if (zone.getLignes().size() == nombreLignesAvant) {
                break;
            }
        }

        // 8. Vérification finale de sécurité
        if (compteurSecurite >= MAX_ITERATIONS) {
            System.err.println("ATTENTION : Simulation interrompue après "
                    + MAX_ITERATIONS + " itérations.");
            return new SimulationDTO(new ArrayList<>(), 0, 0.0);
        }

        // 9. Conversion du modèle vers les DTO d'affichage
        List<BlocDTO> blocsDTO = new ArrayList<>();
        int nombreTotalBlocs = 0;
        double hauteurCourante = 0.0;

        for (Ligne ligne : zone.getLignes()) {
            double largeurCourante = 0.0;

            for (Bloc bloc : ligne.getBlocs()) {
                if (bloc == null || bloc.getLargeur() <= 0) {
                    continue;
                }

                blocsDTO.add(new BlocDTO(
                    largeurCourante,        // position X
                    hauteurCourante,        // position Y
                    bloc.getLargeur(),      // largeur réelle du bloc ou morceau
                    zone.getHauteurBloc()   // hauteur configurée
                ));

                largeurCourante += bloc.getLargeur();
                nombreTotalBlocs++;
            }

            hauteurCourante += zone.getHauteurBloc();
        }

        // 10. Calcul du coût total
        double coutTotal = nombreTotalBlocs * prixUnitaireBloc;

        return new SimulationDTO(blocsDTO, nombreTotalBlocs, coutTotal);
    }
}
