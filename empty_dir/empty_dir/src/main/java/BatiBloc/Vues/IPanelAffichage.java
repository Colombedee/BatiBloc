package BatiBloc.Vues;

import BatiBloc.Domaine.Dto.VueDTO;
import java.awt.Rectangle;
import java.util.function.Consumer;


public interface IPanelAffichage {
    void afficherVue(VueDTO vue);
    void setModeRognage(boolean actif);
    void setModeCalibration(boolean actif);
    void setModeAjoutZone(boolean actif);
    void setOnRognageTermine(Consumer<Rectangle> callback);
    void setOnZoneAjoutee(Runnable callback);
    void setModeDeplacementZone(boolean actif);
    void setModeRedimensionnementZone(boolean actif);
    void zoomInCenter();
    void zoomOutCenter();
}