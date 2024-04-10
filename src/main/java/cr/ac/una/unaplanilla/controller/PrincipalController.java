package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.util.FlowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.BorderPane;

public class PrincipalController extends Controller implements Initializable {

    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void initialize() {
        //TODO
    }

    @FXML
    private void onActionBtnEmpleados(ActionEvent event){
        FlowController.getInstance().goView("EmpleadosView");
    }

    @FXML
    private void onActionBtnTiposPlanilla(ActionEvent event){
        FlowController.getInstance().goView("TiposPlanillaView");
    }
}
