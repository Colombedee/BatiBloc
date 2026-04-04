package BatiBloc;

import BatiBloc.Domaine.Controleur.MainController;
import BatiBloc.Vues.majLangueDrawingPanel;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;

public class Main {

    private static void startApp() {
        MainController controller = new MainController();
        majLangueDrawingPanel window = new majLangueDrawingPanel(controller);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(Main::startApp);
    }
}