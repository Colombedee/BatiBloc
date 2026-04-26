package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Echelle;

public final class EchelleService {

    private EchelleService() {}

    public static Echelle parse(String texte) {
        return Echelle.parse(texte);
    }

}