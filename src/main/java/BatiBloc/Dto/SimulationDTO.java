package BatiBloc.Dto;

import java.util.List;

public class SimulationDTO {

    public final List<BlocDTO> blocs;
    public final int nombreBlocs;
    public final double coutTotal;

    public SimulationDTO(List<BlocDTO> blocs, int nombreBlocs, double coutTotal) {
        this.blocs = blocs;
        this.nombreBlocs = nombreBlocs;
        this.coutTotal = coutTotal;
    }
}