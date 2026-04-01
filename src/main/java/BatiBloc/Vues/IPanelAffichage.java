/**
 *
 * @author Emmauella Iris Andréa Lehe
 * crée le 19 mars 2026 à 12h30
 * Imterface qui va acceuillir la 3D
 */

package BatiBloc.Vues;

import BatiBloc.Dto.VueDTO;
import java.awt.Rectangle;
import java.util.function.Consumer;


public interface IPanelAffichage {
    void afficherVue(VueDTO vue);
    void setModeRognage(boolean actif);
    void setModeCalibration(boolean actif);
    void setModeAjoutZone(boolean actif);
    void setOnRognageTermine(Consumer<Rectangle> callback);
    void setOnZoneAjoutee(Runnable callback);//Colombe
    void zoomInCenter();
    void zoomOutCenter();
}