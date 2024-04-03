package cr.ac.una.unaplanilla.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.unaplanilla.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class PrincipalViewController extends Controller implements Initializable{

    @FXML
    private MFXButton btnEmpleados;

    @FXML
    private MFXButton btnSalir;

    @FXML
    private MFXButton btnTiposPlanilla;

    @FXML
    private BorderPane root;

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }
    
    @FXML
    void onActionBtnEmpleados(ActionEvent event) {

        FlowController.getInstance().goView("EmpleadosView");

    }

    @FXML
    void onActionBtnSalir(ActionEvent event) {

        FlowController.getInstance().salir();

    }

    @FXML
    void onActionBtnTiposPlanillas(ActionEvent event) {

        FlowController.getInstance().goView("TipoPlanillaView");

    }


}
