package BatiBloc;

import BatiBloc.Controller.MainController;
import BatiBloc.View.MainWindow;

public class Main {
    
    public static void main(String[] args) {

        MainController controller = new MainController();
        MainWindow mainWindow = new MainWindow(controller);

        mainWindow.setVisible(true);
    }
}
