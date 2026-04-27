package BatiBloc.Domaine.Dto;

public class SimulationResultatDTO {
    
    private final double prixTotal;
    private final int totalBlocs;

    public SimulationResultatDTO(double prixTotal, int totalBlocs) {
        this.prixTotal = prixTotal;
        this.totalBlocs = totalBlocs;
    }
    
    public double getPrixTotal() {
        return this.prixTotal;
    }

    public int getTotalBlocs() {
        return this.totalBlocs;
    }
}