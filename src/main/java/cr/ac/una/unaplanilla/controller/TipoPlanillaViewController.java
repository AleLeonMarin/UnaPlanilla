package cr.ac.una.unaplanilla.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.service.TipoPlanillaService;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private MFXCheckbox chkActivo;

    @FXML
    private TabPane tPlanillas;

    @FXML
    private TableColumn<EmpleadoDto, String> tbcCodigo;

    @FXML
    private TableColumn<EmpleadoDto, Boolean> tbcEliminar;

    @FXML
    private TableColumn<EmpleadoDto, String> tbcNombre;

    @FXML
    private TableView<EmpleadoDto> tbvEmpleados;

    @FXML
    private Tab tptInclusion;

    @FXML
    private Tab tptTipoPlanilla;

    @FXML
    private MFXTextField txfCodigo;

    @FXML
    private MFXTextField txfDescripcion;

    @FXML
    private MFXTextField txfId;

    @FXML
    private MFXTextField txfIdEmpleado;

    @FXML
    private MFXTextField txfNombreEmpleado;

    @FXML
    private MFXTextField txfPlanillaXMes;

    private TipoPlanillaDto tipoPlanillaDto;
    private EmpleadoDto empleadoDto;
    List<Node> requiredNodes = new ArrayList<>();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        txfIdEmpleado.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txfId.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txfPlanillaXMes.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txfCodigo.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(4));
        txfDescripcion.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(40));
        tipoPlanillaDto = new TipoPlanillaDto();
        empleadoDto = new EmpleadoDto();
        newPlanillaType();
        requiredNodesIndicate();

        tbcCodigo.setCellValueFactory(cd -> cd.getValue().id);
        tbcNombre.setCellValueFactory(cd -> cd.getValue().nombre);
        tbcEliminar.setCellValueFactory(
                (TableColumn.CellDataFeatures<EmpleadoDto, Boolean> p) -> new SimpleBooleanProperty(
                        p.getValue() != null));
        tbcEliminar.setCellFactory((TableColumn<EmpleadoDto, Boolean> p) -> new ButtonCell());

        tbvEmpleados.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends EmpleadoDto> observable, EmpleadoDto oldValue, EmpleadoDto newValue) -> {
                    unbindEmpleado();
                    if (newValue != null) {
                        this.empleadoDto = newValue;
                        bindEmpleado(false);
                    }
                });

    }

    @FXML
    void onActionBtnBuscar(ActionEvent event) {

        FlowController.getInstance().goViewInWindowModal("BusquedaViewPlanilla", getStage(), true);

    }

    @FXML
    void onActionBtnEliminar(ActionEvent event) {
        try {
            if (tipoPlanillaDto.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar Tipo Planilla", this.getStage(),
                        "No se ha cargado un tipo de planilla para eliminar.");
            } else {

                TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
                Respuesta respuesta = tipoPlanillaService.eliminarTipoPlanilla(tipoPlanillaDto.getId());
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar Tipo Planilla", this.getStage(),
                            respuesta.getMensaje());
                } else {
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Eliminar Tipo Planilla", this.getStage(),
                            "Tipo de Planilla eliminado correctamente.");
                    newPlanillaType();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            Logger.getLogger(TipoPlanillaViewController.class.getName()).log(Level.SEVERE,
                    "Error al eliminar el tipo de planilla.", e);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar Tipo Planilla", this.getStage(),
                    "Ocurrió un error al eliminar el tipo de planilla.");
        }

    }

    @FXML
    void onActionBtnGuardar(ActionEvent event) {

        try {
            String invalidos = validarRequeridos();
            if (!invalidos.isBlank()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(), invalidos);
            } else {
                TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
                Respuesta respuesta = tipoPlanillaService.guardarTipoPlanilla(this.tipoPlanillaDto);
                if (respuesta.getEstado()) {
                    unbindTipoPlanilla();
                    this.tipoPlanillaDto = (TipoPlanillaDto) respuesta.getResultado("TipoPlanilla");
                    bindTipoPlanilla(false);
                    chargeEmpleados();
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Guardar Tipo Planilla", getStage(),
                            "Tipo de Planilla guardado correctamente.");
                } else {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Planilla", getStage(),
                            respuesta.getMensaje());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaViewController.class.getName()).log(Level.SEVERE,
                    "Error al guardar el tipo de planilla.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(),
                    "Ocurrió un error al guardar el tipo de planilla.");
        }

    }

    @FXML
    void onActionBtnNuevo(ActionEvent event) {
        if (tptInclusion.isSelected()) {
            newEmpleado();
        } else if (tptTipoPlanilla.isSelected()) {
            if (new Mensaje().showConfirmation("Limpiar tipo planilla", getStage(),
                    "¿Esta seguro que desea limpiar el registro?")) {
                newPlanillaType();
            }
        }
    }

    @FXML
    void onActionBtnAgregarEmpleado(ActionEvent event) {

        if (empleadoDto.getId() == null || empleadoDto.getNombre().isEmpty()) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Agregar empleado", getStage(),
                    "Es necesario cargar un empleado para agregarlo a la lista.");
        } else if (tbvEmpleados.getItems() == null
                || tbvEmpleados.getItems().stream().noneMatch(a -> a.equals(this.empleadoDto))) {
            empleadoDto.setModificado(true);
            tbvEmpleados.getItems().add(this.empleadoDto);
            tbvEmpleados.refresh();
        }
        newEmpleado();

    }

    @FXML
    void onKeyPressedTxtId(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfId.getText().isEmpty()) {
            chargePlanillaType(Long.valueOf(txfId.getText()));
        }
    }

    @FXML
    void onKeyPressedIdEmpleados(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !txfIdEmpleado.getText().isEmpty()) {
            cargarEmpleado(Long.valueOf(txfIdEmpleado.getText()));
        }

    }

    @FXML
    void onSelectionEmpleados(Event event) {

        if (tptInclusion.isSelected()) {
            if (tipoPlanillaDto.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Tipo planilla", getStage(),
                        "Debe cargar el tipo de planilla al que desea agregar empleados.");
                tPlanillas.getSelectionModel().select(tptTipoPlanilla);
            }
        }

    }

    @FXML
    void onSelectionTipoPlanilla(Event event) {

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

    private void cargarEmpleado(Long id) {
        EmpleadoService service = new EmpleadoService();
        Respuesta respuesta = service.getEmpleado(id);

        if (respuesta.getEstado()) {
            unbindEmpleado();
            empleadoDto = (EmpleadoDto) respuesta.getResultado("Empleado");
            bindEmpleado(false);
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar empleado", getStage(), respuesta.getMensaje());
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

    public void chargeEmpleados() {
        tbvEmpleados.getItems().clear();
        tbvEmpleados.setItems(this.tipoPlanillaDto.getEmpleados());
        tbvEmpleados.refresh();
    }

    private void bindEmpleado(Boolean nuevo) {
        if (!nuevo) {
            txfIdEmpleado.textProperty().bind(this.empleadoDto.id);
        }

        txfNombreEmpleado.textProperty().bindBidirectional(this.empleadoDto.nombre);
    }

    private void unbindEmpleado() {
        txfIdEmpleado.textProperty().unbind();
        txfNombreEmpleado.textProperty().unbindBidirectional(this.empleadoDto.nombre);
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
        txfId.requestFocus();
        newEmpleado();
        chargeEmpleados();
        chkActivo.requestFocus();
    }

    private void newEmpleado() {
        unbindEmpleado();
        // tbvEmpleados.getSelectionModel().clearSelection();
        tbvEmpleados.getSelectionModel().select(null);
        this.empleadoDto = new EmpleadoDto();
        bindEmpleado(true);
        txfIdEmpleado.clear();
        txfIdEmpleado.requestFocus();

    }

    private class ButtonCell extends TableCell<EmpleadoDto, Boolean> {

        final Button cellButton = new Button();

        ButtonCell() {
            cellButton.setPrefWidth(500);
            cellButton.getStyleClass().add("jfx-btnimg-tbveliminar");

            cellButton.setOnAction(t -> {
                EmpleadoDto emp = (EmpleadoDto) ButtonCell.this.getTableView().getItems()
                        .get(ButtonCell.this.getIndex());
                tipoPlanillaDto.getEmpleadosEliminados().add(emp);
                tbvEmpleados.getItems().remove(emp);
                tbvEmpleados.refresh();
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            }
        }

    }

    public void setTipoPlanilla(TipoPlanillaDto tipoPlanillaDto) {
        unbindTipoPlanilla();
        this.tipoPlanillaDto = tipoPlanillaDto;
        bindTipoPlanilla(false);
        validarRequeridos();
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
    }
}
