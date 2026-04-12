package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.SimulationResultatDTO;
import BatiBloc.Domaine.Modeles.Bloc;
import BatiBloc.Domaine.Modeles.Zones.ArmatureBlocs;
import BatiBloc.Domaine.Modeles.Zones.Zone;
import java.util.List;

public class SimulationService {

    private final ArmatureBlocsService armatureService = new ArmatureBlocsService();

    public SimulationResultatDTO simulerToutesLesZones(List<Zone> zones) {

        int totalBlocs = 0;
        double prixTotal = 0;

        for (Zone z : zones) {
            if (z instanceof ArmatureBlocs armature) {

                List<Bloc> blocs = armatureService.simuler(armature);

                totalBlocs += blocs.size();
                prixTotal += blocs.stream()
                                  .mapToDouble(Bloc::getPrix)
                                  .sum();
            }
        }

        return new SimulationResultatDTO(totalBlocs, prixTotal);
    }
}