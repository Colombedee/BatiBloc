package BatiBloc.Services;

import BatiBloc.Dto.*;
import BatiBloc.Modeles.Zone;

import java.util.ArrayList;
import java.util.List;

public class SimulationService {

    public static SimulationDTO simuler(Zone zone) {

        List<BlocDTO> blocs = new ArrayList<>();
        double hauteurBloc = zone.getHauteurBloc();
        double largeurBloc = zone.getLargeurBloc();

        double y = 0;
        int nbBlocs = 0;

        while (y < zone.getHauteurTotale()) {

            double x = 0;

            while (x < zone.getLargeurTotale()) {
                double largeur = Math.min(largeurBloc, zone.getLargeurTotale() - x);

                blocs.add(new BlocDTO(x, y, largeur, hauteurBloc));
                x += largeur;
                nbBlocs++;
            }

            y += hauteurBloc;
        }

        double cout = nbBlocs * 20;

        return new SimulationDTO(blocs, nbBlocs, cout);
    }
}