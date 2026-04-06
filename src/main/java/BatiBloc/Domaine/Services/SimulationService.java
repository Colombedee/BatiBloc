package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.BlocDTO;
import BatiBloc.Domaine.Dto.SimulationDTO;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Forme;
import BatiBloc.Domaine.Modeles.Ligne;
import BatiBloc.Domaine.Modeles.Ouverture;
import BatiBloc.Domaine.Modeles.RectangleForme;
import BatiBloc.Domaine.Modeles.Zone;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de simulation de placement de blocs.
 *
 * Supporte les trois formes de zones : Rectangle, Triangle et Triangle tronqué.
 *
 */
public final class SimulationService {

    private static final double LONGUEUR_MINIMALE_BLOC   = 6.0;
    private static final double COUT_UNITAIRE_DEFAUT     = 20.0;
    private static final int    MAX_ITERATIONS           = 10_000;

    private SimulationService() { }


    public static SimulationDTO simuler(Zone zone) {
        return simuler(zone, COUT_UNITAIRE_DEFAUT);
    }

    public static SimulationDTO simuler(Zone zone, double prixUnitaireBloc) {
        if (zone == null)              return null;
        if (zone instanceof Ouverture) return null;

        if (zone.getLargeurBloc() < LONGUEUR_MINIMALE_BLOC || zone.getHauteurBloc() <= 0) {
            return vide();
        }
        if (prixUnitaireBloc < 0) prixUnitaireBloc = 0.0;

        // Réinitialise l'état de simulation dans le modèle
        zone.reinitialiserSimulation();

        SimulationDTO result;
        if (zone.getForme() instanceof RectangleForme) {
            result = simulerRectangle(zone, prixUnitaireBloc);
        } else {
            result = simulerFormeNonRectangulaire(zone, prixUnitaireBloc);
        }

        // Stocker les Blocs dans le modèle Zone pour l'affichage
        zone.setBlocsSimules(result.blocs);
        return result;
    }


    // pour les rectangles
    private static SimulationDTO simulerRectangle(Zone zone, double prixUnitaireBloc) {
        List<BlocDTO> blocsDTO    = new ArrayList<>();
        int           totalBlocs  = 0;
        double        yCourante   = 0.0;
        int           iterations  = 0;

        while (!zone.estComplete() && iterations < MAX_ITERATIONS) {
            int avantNbLignes = zone.getLignes().size();
            zone.construireLigne();                               // ajoute à zone.lignes
            if (zone.getLignes().size() == avantNbLignes) break; // sécurité anti-boucle

            Ligne derniereLigne = zone.getLignes().get(zone.getLignes().size() - 1);
            double xCourante = 0.0;
            for (Bloc bloc : derniereLigne.getBlocs()) {
                blocsDTO.add(new BlocDTO(xCourante, yCourante,
                        bloc.getLargeur(), zone.getHauteurBloc()));
                xCourante += bloc.getLargeur();
                totalBlocs++;
            }
            yCourante += zone.getHauteurBloc();
            iterations++;
        }

        if (iterations >= MAX_ITERATIONS) {
            System.err.println("[SimulationService] Simulation rectangulaire interrompue après "
                    + MAX_ITERATIONS + " itérations.");
            return vide();
        }

        return new SimulationDTO(blocsDTO, totalBlocs, totalBlocs * prixUnitaireBloc);
    }


    // pour les forme triangles et rectangles 
    private static SimulationDTO simulerFormeNonRectangulaire(Zone zone, double prixUnitaireBloc) {
        Forme  forme        = zone.getForme();
        double totalHauteur = forme.getHauteur(); // hauteur réelle, sans arrondi
        double hauteurBloc  = zone.getHauteurBloc();

        List<BlocDTO> blocsDTO   = new ArrayList<>();
        int           totalBlocs = 0;
        int           rowIndex   = 0;

        while (rowIndex < MAX_ITERATIONS) {
            // Distance depuis la base au BAS de la rangée courante
            double distBaseBasRangee = rowIndex * hauteurBloc;

            // La rangée dépasse la hauteur de la zone → on arrête
            if (distBaseBasRangee >= totalHauteur) break;

            // Largeur disponible au BAS de la rangée (point le plus large pour cette rangée)
            double largeurDispo = forme.getLargeurAHauteur(distBaseBasRangee);
            if (largeurDispo < LONGUEUR_MINIMALE_BLOC) break; // trop étroit

            double xOffset = forme.getXOffsetAHauteur(distBaseBasRangee);

            // Construction de la rangée
            Ligne ligne = zone.construireLigneAvecLargeur(largeurDispo);
            if (ligne == null || ligne.getBlocs().isEmpty()) break;

            // Coordonnée Y du bord SUPÉRIEUR de la rangée
            // Convention : y=0 en haut de la bounding box, y croît vers le bas
            double yHautRangee = totalHauteur - (rowIndex + 1) * hauteurBloc;
            if (yHautRangee < 0) yHautRangee = 0.0;

            double xCourante = xOffset;
            for (Bloc bloc : ligne.getBlocs()) {
                blocsDTO.add(new BlocDTO(xCourante, yHautRangee,
                        bloc.getLargeur(), hauteurBloc));
                xCourante += bloc.getLargeur();
                totalBlocs++;
            }

            rowIndex++;
        }

        return new SimulationDTO(blocsDTO, totalBlocs, totalBlocs * prixUnitaireBloc);
    }

    private static SimulationDTO vide() {
        return new SimulationDTO(new ArrayList<>(), 0, 0.0);
    }
}
