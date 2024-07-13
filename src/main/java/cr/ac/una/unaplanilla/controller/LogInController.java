package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.util.AppContext;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogInController  extends Controller implements Initializable {
    @FXML
    private MFXButton btnIngresar;

    @FXML
    private AnchorPane root;

    @FXML
    private MFXButton btnSalir;

    @FXML
    private ImageView imvFondo;

    @FXML
    private MFXPasswordField psfPassword;

    @FXML
    private MFXTextField txfUser;

    @Override
    public void initialize() {
        imvFondo.fitHeightProperty().bind(root.heightProperty());
        imvFondo.fitWidthProperty().bind(root.widthProperty());
    }

    @FXML
    void onActionBtnIngresar(ActionEvent event) {
<<<<<<< HEAD
        try {
            if (txfUser.getText() == null|| txfUser.getText().isBlank()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion de  Usuario", getStage(), "Debe ingresar un usuario");
            } else if (psfPassword.getText() == null || psfPassword.getText().isBlank())
                new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion de  contraseña", (Stage) btnIngresar.getScene().getWindow(), "Debe ingresar una contraseña");
            else{
                FlowController.getInstance().goMain();
            }
        } catch (Exception ex) {
            Logger.getLogger(LogInController.class.getName()).log(Level.SEVERE, "Error al ingreso del Sistema", ex);
=======
        if (txfUser.getText().isBlank())
        {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion Usuario", getStage(), "Es necesario digitar un usuario para ingresar al sistema");
        } else if (psfPassword.getText().isBlank())
        {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion Usuario", getStage(), "Es necesario digitar una clave para ingresar al sistema");
        } else
        {
            EmpleadoService empleadoService = new EmpleadoService();
            Respuesta respuesta = empleadoService.getUsuario(txfUser.getText(), psfPassword.getText());
>>>>>>> dev

            if (respuesta.getEstado())
            {
                AppContext.getInstance().set("Usuario", respuesta.getResultado("Usuario"));
                FlowController.getInstance().goMain();
                Stage stage = (Stage) this.root.getScene().getWindow();
                stage.close();
            } else
            {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Validacion Usuario", getStage(), respuesta.getMensaje());
            }
        }
    }
    @FXML
    void onActionBtnSalir(ActionEvent event) {

        ((Stage) btnSalir.getScene().getWindow()).close();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
