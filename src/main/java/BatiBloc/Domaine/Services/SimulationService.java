package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Constantes.ConstantesBloc;
import BatiBloc.Domaine.Dto.SimulationResultatDTO;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Ouverture;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.util.List;


public class SimulationService {

    private final ArmatureBlocsService armatureService = new ArmatureBlocsService();
    private final FusionService fusionService = new FusionService();


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


    public SimulationResultatDTO simulerToutesLesZones(
            List<Zone> zones,
            double hauteurBloc,
            double largeurBloc,
            double prixUnitaireBloc
    ) {
        if (zones == null || zones.isEmpty()) {
            return new SimulationResultatDTO(0.0, 0);
        }

        if (hauteurBloc <= 0 || largeurBloc <= 0 || prixUnitaireBloc < 0) {
            return new SimulationResultatDTO(0.0, 0);
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

        return new SimulationResultatDTO(prixTotal, totalBlocs);
    }
    
    /** //@roneh
 * Simule toutes les vues du projet et retourne le total cumulé.
 * Chaque élément de la liste représente les zones d'une vue.
 */
    public SimulationResultatDTO simulerProjet(
        List<List<Zone>> zonesParVue,
        double hauteurBloc,
        double largeurBloc,
        double prixUnitaireBloc) 
    {
    if (zonesParVue == null || zonesParVue.isEmpty()) {
        return new SimulationResultatDTO(0.0, 0);
    }

    int totalBlocs = 0;
    double prixTotal = 0.0;

    for (List<Zone> zones : zonesParVue) {
        SimulationResultatDTO r = simulerToutesLesZones(
                zones, hauteurBloc, largeurBloc, prixUnitaireBloc);
        totalBlocs += r.getTotalBlocs();
        prixTotal  += r.getPrixTotal();
    }

    return new SimulationResultatDTO(prixTotal, totalBlocs);
   }
}