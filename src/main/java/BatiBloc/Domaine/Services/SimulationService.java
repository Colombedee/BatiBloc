package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Constantes.ConstantesBloc;
import BatiBloc.Domaine.Dto.SimulationResultatDTO;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.util.List;

/**
 *
 * @author nicky
 *
 * Corrections apportées :
 * - suppression des appels inexistants getHauteurBloc() et getLargeurBloc() sur Zone
 * - les dimensions des blocs sont reçues en paramètres, conformément à l'architecture actuelle
 * - prise en compte des ouvertures de la vue lors de la simulation des ArmatureBlocs
 * - surcharge conservée uniquement avec dimensions explicites + prix par défaut
 */
public class SimulationService {

    private final ArmatureBlocsService armatureService = new ArmatureBlocsService();
    private final FusionService fusionService = new FusionService();

    /**
     * Version avec prix par défaut, mais dimensions explicites.
     */
    public SimulationResultatDTO simulerToutesLesZones(
            List<Zone> zones,
            double hauteurBloc,
            double largeurBloc
    ) {
        return simulerToutesLesZones(
                zones,
                hauteurBloc,
                largeurBloc,
                ConstantesBloc.PRIX_DEFAUT
        );
    }

    /**
     * Version complète utilisée par le contrôleur.
     */
    public SimulationResultatDTO simulerToutesLesZones(
            List<Zone> zones,
            double hauteurBloc,
            double largeurBloc,
            double prixUnitaireBloc
    ) {
        if (zones == null || zones.isEmpty()) {
            return new SimulationResultatDTO(0, 0.0);
        }

        if (hauteurBloc <= 0 || largeurBloc <= 0 || prixUnitaireBloc < 0) {
            return new SimulationResultatDTO(0, 0.0);
        }

        List<Ouverture> ouvertures = zones.stream()
                .filter(Ouverture.class::isInstance)
                .map(Ouverture.class::cast)
                .toList();

        List<List<ArmatureBlocs>> groupes =
                fusionService.trouverGroupesFusionnables(zones, hauteurBloc);

        int totalBlocs = 0;
        double prixTotal = 0.0;

        for (List<ArmatureBlocs> groupe : groupes) {
            List<Bloc> blocs = armatureService.simulerGroupe(
                    groupe,
                    ouvertures,
                    hauteurBloc,
                    largeurBloc,
                    prixUnitaireBloc
            );

            totalBlocs += blocs.size();
            prixTotal += blocs.stream()
                    .mapToDouble(Bloc::getPrix)
                    .sum();
        }

        return new SimulationResultatDTO(totalBlocs, prixTotal);
    }
}