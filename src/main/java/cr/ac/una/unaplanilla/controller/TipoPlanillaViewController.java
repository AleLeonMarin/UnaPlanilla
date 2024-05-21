package cr.ac.una.unaplanilla.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
import cr.ac.una.unaplanilla.service.TipoPlanillaService;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TipoPlanillaViewController extends Controller implements Initializable {

    @FXML
    private MFXButton btnBuscar;

    @FXML
    private MFXButton btnEliminar;

    @FXML
    private MFXButton btnGuardar;

    @FXML
    private MFXButton btnNuevo;

    @FXML
    private MFXTextField txfCodigo;

    @FXML
    private MFXTextField txfId;

    @FXML
    private MFXTextField txfDescripcion;

    @FXML
    private MFXCheckbox chkActivo;

    @FXML
    private MFXTextField txfPlanillaXMes;

    private TipoPlanillaDto tipoPlanillaDto;
    List<Node> requiredNodes = new ArrayList<>();

    @FXML
    void onActionBtnBuscar(ActionEvent event) {

    }

    @FXML
    void onActionBtnEliminar(ActionEvent event) {
        try {
            if(tipoPlanillaDto.getId()== null){
                new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar Tipo Planilla", this.getStage(), "No se ha cargado un tipo de planilla para eliminar.");
            }else{

                TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
                Respuesta respuesta = tipoPlanillaService.eliminarTipoPlanilla(tipoPlanillaDto.getId());
                if(!respuesta.getEstado()){
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar Tipo Planilla", this.getStage(), respuesta.getMensaje());
            }else{
                new Mensaje().showModal(Alert.AlertType.INFORMATION, "Eliminar Tipo Planilla", this.getStage(), "Tipo de Planilla eliminado correctamente.");
                newPlanillaType();
            }
        }
        } catch (Exception e) {
            // TODO: handle exception
            Logger.getLogger(TipoPlanillaViewController.class.getName()).log(Level.SEVERE, "Error al eliminar el tipo de planilla.", e);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar Tipo Planilla", this.getStage(), "Ocurrió un error al eliminar el tipo de planilla.");
        }

    }

    @FXML
    void onActionBtnGuardar(ActionEvent event) {

        try {
            String invalidos = validarRequeridos();
            if(!invalidos.isBlank()){
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", this.getStage(), invalidos);
            }else{
                TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
                Respuesta respuesta = tipoPlanillaService.guardarTipoPlanilla(tipoPlanillaDto);
                if(respuesta.getEstado()){
                    unbindTipoPlanilla();
                    this.tipoPlanillaDto = (TipoPlanillaDto) respuesta.getResultado("TipoPlanilla");
                    bindTipoPlanilla(false);
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Guardar Tipo Planilla", this.getStage(), "Tipo de Planilla guardado correctamente.");
            }else{
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(), respuesta.getMensaje());
            }
        }
        } catch (Exception e) {
            Logger.getLogger(TipoPlanillaViewController.class.getName()).log(Level.SEVERE, "Error al guardar el tipo de planilla.", e);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(), "Ocurrió un error al guardar el tipo de planilla.");
        }

    }

    @FXML
    void onActionBtnNuevo(ActionEvent event) {
        if (new Mensaje().showConfirmation("Limpiar Tipo Planilla", getStage(),
                "Desea limpiar el resgistro de Planilla?")) {
            newPlanillaType();
        }
    }

    @FXML
    void onKeyPressedTxtId(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfId.getText().isEmpty()) {
            chargePlanillaType(Long.valueOf(txfId.getText()));
        }
    }

    private void chargePlanillaType(Long id) {
        try {
            TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
            Respuesta respuesta = tipoPlanillaService.getTipoPlanilla(id);

            if (respuesta.getEstado()) {
                unbindTipoPlanilla();
                this.tipoPlanillaDto = (TipoPlanillaDto) respuesta.getResultado("TipoPlanilla");
                bindTipoPlanilla(false);
                validarRequeridos();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Planilla", this.getStage(),
                        respuesta.getMensaje());
            }
        } catch (Exception e) {
            Logger.getLogger(TipoPlanillaViewController.class.getName()).log(Level.SEVERE,
                    "Error al cargar el tipo de planilla.", e);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Planilla", this.getStage(),
                    "Ocurrió un error al cargar el tipo de planilla.");
        }
    }

    public void unbindTipoPlanilla() {
        txfId.textProperty().unbind();
        txfCodigo.textProperty().unbindBidirectional(tipoPlanillaDto.codigo);
        txfPlanillaXMes.textProperty().unbindBidirectional(tipoPlanillaDto.planillasPorMes);
        txfDescripcion.textProperty().unbindBidirectional(tipoPlanillaDto.descripcion);
        chkActivo.selectedProperty().unbindBidirectional(tipoPlanillaDto.estado);
    }

    private void bindTipoPlanilla(Boolean nuevo) {
        if (!nuevo) {
            txfId.textProperty().bindBidirectional(tipoPlanillaDto.id);
        }
        txfCodigo.textProperty().bindBidirectional(tipoPlanillaDto.codigo);
        txfPlanillaXMes.textProperty().bindBidirectional(tipoPlanillaDto.planillasPorMes);
        txfDescripcion.textProperty().bindBidirectional(tipoPlanillaDto.descripcion);
        chkActivo.selectedProperty().bindBidirectional(tipoPlanillaDto.estado);
    }

    public String validarRequeridos() {
        Boolean validos = true;
        String invalidos = "";
        for (Node node : requiredNodes) {
            if (node instanceof MFXTextField
                    && (((MFXTextField) node).getText() == null || ((MFXTextField) node).getText().isBlank())) {
                if (validos) {
                    invalidos += ((MFXTextField) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXTextField) node).getFloatingText();
                }
                validos = false;
            } else if (node instanceof MFXPasswordField
                    && (((MFXPasswordField) node).getText() == null || ((MFXPasswordField) node).getText().isBlank())) {
                if (validos) {
                    invalidos += ((MFXPasswordField) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXPasswordField) node).getFloatingText();
                }
                validos = false;
            } else if (node instanceof MFXDatePicker && ((MFXDatePicker) node).getValue() == null) {
                if (validos) {
                    invalidos += ((MFXDatePicker) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXDatePicker) node).getFloatingText();
                }
                validos = false;
            } else if (node instanceof MFXComboBox && ((MFXComboBox) node).getSelectionModel().getSelectedIndex() < 0) {
                if (validos) {
                    invalidos += ((MFXComboBox) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXComboBox) node).getFloatingText();
                }
                validos = false;
            }
        }
        if (validos) {
            return "";
        } else {
            return "Campos requeridos o con problemas de formato [" + invalidos + "].";
        }
    }

    public void requiredNodesIndicate() {
        requiredNodes.clear();
        requiredNodes.addAll(Arrays.asList(txfCodigo, txfDescripcion, txfPlanillaXMes));
    }

    private void newPlanillaType() {
        unbindTipoPlanilla();
        tipoPlanillaDto = new TipoPlanillaDto();
        bindTipoPlanilla(true);
        txfId.clear();
        chkActivo.requestFocus();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        txfId.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txfPlanillaXMes.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txfCodigo.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(4));
        txfDescripcion.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(50));
        tipoPlanillaDto = new TipoPlanillaDto();
        newPlanillaType();
        requiredNodesIndicate();
        // TODO Auto-generated method stub
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
    }

}
