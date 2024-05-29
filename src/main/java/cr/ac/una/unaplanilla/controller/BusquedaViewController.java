package cr.ac.una.unaplanilla.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.service.EmpleadoService;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class BusquedaViewController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private MFXButton btnFilter;

    @FXML
    private MFXButton btnLoad;

    @FXML
    private TableView<EmpleadoDto> tbvResults;

    @FXML
    private MFXTextField txfCedula;

    @FXML
    private MFXTextField txfLastName1;

    @FXML
    private MFXTextField txfLastName2;

    @FXML
    private MFXTextField txfName;

    private EmpleadoService empleadoService;

    ObservableList<EmpleadoDto> empleados = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populateTableView();

        empleados = FXCollections.observableArrayList();
        tbvResults.setItems(empleados);

        empleadoService = new EmpleadoService();

        txfLastName1.delegateSetTextFormatter(Formato.getInstance().letrasFormat(15));
        txfLastName2.delegateSetTextFormatter(Formato.getInstance().letrasFormat(15));
        txfCedula.delegateSetTextFormatter(Formato.getInstance().cedulaFormat(10));
        txfName.delegateSetTextFormatter(Formato.getInstance().letrasFormat(15));

    }

    @Override
    public void initialize() {
    }

    private void populateTableView() {

        TableColumn<EmpleadoDto, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nombre);
        nameColumn.setPrefWidth(50);
        TableColumn<EmpleadoDto, String> cedColumn = new TableColumn<>("Cedula");
        cedColumn.setCellValueFactory(cellData -> cellData.getValue().cedula);
        cedColumn.setPrefWidth(50);
        TableColumn<EmpleadoDto, String> last1Column = new TableColumn<>("Primer Apellido");
        last1Column.setCellValueFactory(cellData -> cellData.getValue().primerApellido);
        last1Column.setPrefWidth(50);
        TableColumn<EmpleadoDto, String> last2Column = new TableColumn<>("Segundo Apellido");
        last2Column.setCellValueFactory(cellData -> cellData.getValue().segundoApellido);
        last2Column.setPrefWidth(50);

        tbvResults.getColumns().addAll(cedColumn, nameColumn, last1Column, last2Column);
        tbvResults.refresh();
    }

    @FXML
    void onActionBtnFilter(ActionEvent event) {

        String cedula = txfCedula.getText();
        String nombre = txfName.getText();
        String primerApellido = txfLastName1.getText();
        String segundoApellido = txfLastName2.getText();

        Respuesta respuesta = empleadoService.filtrarEmpleados(cedula, nombre, primerApellido, segundoApellido);
        if (respuesta.getEstado()) {
            empleados.setAll((List<EmpleadoDto>) respuesta.getResultado("Empleados"));
        } else {
            System.err.println("Error al obtener los empleados: " + respuesta.getMensajeInterno());
        }

    }

    @FXML
    void onKeyPressedCed(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfCedula.getText().isBlank()) {
            cargarEmpleados(String.valueOf(txfCedula.getText()), String.valueOf(txfName.getText()),
                    String.valueOf(txfLastName1.getText()), String.valueOf(txfLastName2.getText()));
        }

    }

    @FXML
    void onKeyPressedLast1(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfLastName1.getText().isBlank()) {
            cargarEmpleados(String.valueOf(txfLastName1.getText()), String.valueOf(txfCedula.getText()),
                    String.valueOf(txfName.getText()), String.valueOf(txfLastName2.getText()));
        }

    }

    @FXML
    void onKeyPressedLast2(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfLastName2.getText().isBlank()) {
            cargarEmpleados(String.valueOf(txfLastName2.getText()), String.valueOf(txfCedula.getText()),
                    String.valueOf(txfName.getText()), String.valueOf(txfLastName1.getText()));
        }

    }

    @FXML
    void onKeyPressedName(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER && !txfName.getText().isBlank()) {
            cargarEmpleados(String.valueOf(txfName.getText()), String.valueOf(txfLastName1.getText()),
                    String.valueOf(txfLastName2.getText()), String.valueOf(txfCedula.getText()));
        }

    }

    @FXML
    void onActionBtnLoad(ActionEvent event) {
        EmpleadoDto empleadoSeleccionado = tbvResults.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            EmpleadosViewController empleadosController = (EmpleadosViewController) FlowController.getInstance().getController("EmpleadosView");
            if (empleadosController != null) {
                empleadosController.setEmpleado(empleadoSeleccionado);
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            } else {
                System.err.println("Error: No se pudo obtener el controlador de la vista anterior.");
            }
        } else {
            System.err.println("Error: No se ha seleccionado ningún empleado.");
        }

    }
    
      @FXML
    void onMousePressedTbvResultados(MouseEvent event) {
        
         if(event.isPrimaryButtonDown()&& event.getClickCount()==2){
             onActionBtnLoad(null);
        }

        
    }

    private void cargarEmpleados(String nombre, String pApellido, String sApellido, String cedula) {
        try {
            EmpleadoService empleadoService = new EmpleadoService();
            Respuesta respuesta = empleadoService.filtrarEmpleados(cedula, nombre, pApellido, sApellido);

            if (respuesta.getEstado()) {
                empleados.clear();
                empleados.addAll((List<EmpleadoDto>) respuesta.getResultado("Empleados"));
                tbvResults.setItems(empleados);
                System.out.println(empleados);
                tbvResults.refresh();
                System.out.println("Empleados cargados");
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Empleado", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosViewController.class.getName()).log(Level.SEVERE,
                    "Error consultando el empleado.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Empleado", getStage(),
                    "Ocurrio un error consultando el empleado.");
        }
    }

}
