package cr.ac.una.unaplanilla.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
import cr.ac.una.unaplanilla.service.TipoPlanillaService;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class BusquedaTipoPlanillaController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAceptar;

    @FXML
    private MFXButton btnFiltrar;

    @FXML
    private TableView<TipoPlanillaDto> tbvResultados;

    @FXML
    private TableColumn<TipoPlanillaDto, String> tbcCodigo;

    @FXML
    private TableColumn<TipoPlanillaDto, String> tbcDescripcion;

    @FXML
    private TableColumn<TipoPlanillaDto, String> tbcPlanillasXMes;

    @FXML
    private MFXTextField txfCodigo;

    @FXML
    private MFXTextField txfDescripcion;

    @FXML
    private MFXTextField txfPlanillas;

    ObservableList<TipoPlanillaDto> tipoPlanillas = FXCollections.observableArrayList();

    private TipoPlanillaService tipoPlanillaService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tbcCodigo.setCellValueFactory(cd -> cd.getValue().codigo);
        tbcDescripcion.setCellValueFactory(cd -> cd.getValue().descripcion);
        tbcPlanillasXMes.setCellValueFactory(cd -> cd.getValue().planillasPorMes);

        tipoPlanillas = FXCollections.observableArrayList();
        tbvResultados.setItems(tipoPlanillas);

        tipoPlanillaService = new TipoPlanillaService();

        txfCodigo.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(4));
        txfDescripcion.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(40));
        txfPlanillas.delegateSetTextFormatter(Formato.getInstance().integerFormat());
    }

    @FXML
    void onActionBtnAceptar(ActionEvent event) {

        TipoPlanillaDto tipoPlanillaDto = tbvResultados.getSelectionModel().getSelectedItem();

        if (tipoPlanillaDto != null) {
            TipoPlanillaViewController controller = (TipoPlanillaViewController) FlowController.getInstance()
                    .getController("TipoPlanillaView");
            if (controller != null) {
                controller.setTipoPlanilla(tipoPlanillaDto);
                Stage stage = (Stage) btnAceptar.getScene().getWindow();
                stage.close();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Aceptar", getStage(),
                        "Error al cargar la vista TipoPlanillaView.");
            }
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Aceptar", getStage(),
                    "Debe seleccionar un tipo de planilla.");
        }
    }

    @FXML
    void onActionBtnFiltrar(ActionEvent event) {

        tbvResultados.getItems().clear();

        String codigo = txfCodigo.getText();
        String descripcion = txfDescripcion.getText();
        String plaXmes = txfPlanillas.getText();

        Respuesta respuesta = tipoPlanillaService.filtrarPlanillas(codigo, descripcion, plaXmes);
        if (respuesta.getEstado()) {
            tipoPlanillas.addAll((List<TipoPlanillaDto>) respuesta.getResultado("TipoPlanillas"));
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Planilla", getStage(), respuesta.getMensaje());
        }

    }

    @FXML
    void onKeyPressedTxfCodigo(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfCodigo.getText().isBlank()) {
            cargarTipoPlanilla(txfCodigo.getText(), txfDescripcion.getText(), txfPlanillas.getText());
        }

    }

    @FXML
    void onKeyPressedTxfDescripcion(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfDescripcion.getText().isBlank()) {
            cargarTipoPlanilla(txfCodigo.getText(), txfDescripcion.getText(), txfPlanillas.getText());
        }

    }

    @FXML
    void onKeyPressedTxfPlanillas(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfPlanillas.getText().isBlank()) {
            cargarTipoPlanilla(txfCodigo.getText(), txfDescripcion.getText(), txfPlanillas.getText());
        }

    }

    @FXML
    void onMousePressedTbvResultados(MouseEvent event) {

        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            onActionBtnAceptar(null);
        }

    }

    private void cargarTipoPlanilla(String codigo, String descripcion, String plaXmes) {
        try {
            TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
            Respuesta respuesta = tipoPlanillaService.filtrarPlanillas(codigo, descripcion, plaXmes);

            if (respuesta.getEstado()) {
                tipoPlanillas.clear();
                tipoPlanillas.addAll((List<TipoPlanillaDto>) respuesta.getResultado("TipoPlanillas"));
                tbvResultados.setItems(tipoPlanillas);
                System.out.println(tipoPlanillas);
                tbvResultados.refresh();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Planilla", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaViewController.class.getName()).log(Level.SEVERE,
                    "Error consultando tipos de planillas.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Planillas", getStage(),
                    "Ocurrio un error consultando la Tipo Planilla.");
        }
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        tbvResultados.getItems().clear();

    }

}