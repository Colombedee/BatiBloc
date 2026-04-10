package BatiBloc;

import BatiBloc.Domaine.Controleur.MainController;
import BatiBloc.Vues.MainWindow;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    private static void startApp() {
        MainController controller = new MainController();
        MainWindow window = new MainWindow(controller);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        UIManager.put("TitledBorder.font", new Font("segoeui", Font.BOLD, 14));
        FlatMacDarkLaf.setup();

        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 15);
        UIManager.put("TextComponent.arc", 10);

        SwingUtilities.invokeLater(Main::startApp);
    }
}