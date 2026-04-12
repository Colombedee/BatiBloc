package BatiBloc.Domaine.Dto;

public class SimulationResultatDTO {
    private final int totalBlocs;
    private final double prixTotal;

    public SimulationResultatDTO(int totalBlocs, double prixTotal) {
        this.totalBlocs = totalBlocs;
        this.prixTotal = prixTotal;
    }

    public int getTotalBlocs() {
        return totalBlocs;
    }

    public double getPrixTotal() {
        return prixTotal;
    }
}