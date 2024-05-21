package cr.ac.una.unaplanilla;

import cr.ac.una.unaplanilla.util.FlowController;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goViewInWindow("LogIn");
    }


    public static void main(String[] args) {
        launch();
    }

}