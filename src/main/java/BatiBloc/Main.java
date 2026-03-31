package BatiBloc;

import BatiBloc.Controleur.MainController;
import BatiBloc.Vues.MainWindow;
import BatiBloc.Vues.NewMainWindow;

public class Main {
    
    public static void main(String[] args) {

        MainController controller = new MainController();
        NewMainWindow mainWindow = new NewMainWindow(controller);

        mainWindow.setVisible(true);
    }
}