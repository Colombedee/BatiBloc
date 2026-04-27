package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.SimulationResultatDTO;
import BatiBloc.Domaine.Modeles.Vue;
import BatiBloc.Domaine.Modeles.Zones.*;

import java.util.List;

/**
 *
 * @author nicky
 */
public class SimulationFacadeService {

    private final SimulationService simulationService = new SimulationService();

    public SimulationResultatDTO simulerVue(Vue vue, double hauteurBloc, double largeurBloc, double prixUnitaire) {
        if (vue == null) {
            return null;
        }
        return simulationService.simulerToutesLesZones(vue.getZones(), hauteurBloc, largeurBloc, prixUnitaire);
    }

    public SimulationResultatDTO simulerProjet(List<Vue> vues, double hauteurBloc, double largeurBloc, double prixUnitaire) {
        List<List<Zone>> zonesParVue = vues.stream().map(Vue::getZones).toList();
        return simulationService.simulerProjet(zonesParVue, hauteurBloc, largeurBloc, prixUnitaire);
    }
}
