package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.RangeeBloc;

public class ResultatRangee {

    private final double restePourProchaineRangee;
    private final RangeeBloc rangee;

    public ResultatRangee(double restePourProchaineRangee, RangeeBloc rangee) {
        this.restePourProchaineRangee = restePourProchaineRangee;
        this.rangee = rangee;
    }
    
        public double restePourProchaineRangee() {
        return this.restePourProchaineRangee;
    }

    public RangeeBloc rangee() {
        return this.rangee;
    }
}