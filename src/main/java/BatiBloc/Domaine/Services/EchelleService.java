package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Modeles.Echelle;

public final class EchelleService {

    private EchelleService() {}

    public static Echelle parse(String texte) {
        return Echelle.parse(texte);
    }

    public static double convertirEnPixels(Echelle echelle, double zoom, double valeurEnPouces) {
        return valeurEnPouces * zoom * echelle.getPouces();
    }
}