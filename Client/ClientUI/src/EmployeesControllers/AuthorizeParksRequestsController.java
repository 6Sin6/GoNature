package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class AuthorizeParksRequestsController extends BaseController {
    @FXML
    private MFXButton authBtn;

    @FXML
    private TableColumn<RequestChangingParkParameters, String> statusVal;

    @FXML
    private Text errorTxt;

    @FXML
    private TableColumn<RequestChangingParkParameters, String> newValCol;

    @FXML
    private TableColumn<RequestChangingParkParameters, String> paramCol;

    @FXML
    private TableColumn<RequestChangingParkParameters, String> parkMgrCol;

    @FXML
    private TableColumn<RequestChangingParkParameters, String> parkNameCol;

    @FXML
    private TableView<RequestChangingParkParameters> tableRequests;

    @FXML
    private MFXButton unAuthBtn;

    private ArrayList<RequestChangingParkParameters> requests;
    private int rowIndex = -1;


    @FXML
    void handleRequest(ActionEvent event) {
        if (rowIndex == -1) {
            errorTxt.setText("Please select a request to authorize.");
            return;
        }

        OpCodes opCode = event.getSource() == authBtn ? OpCodes.OP_AUTHORIZE_PARK_REQUEST : OpCodes.OP_DECLINE_PARK_REQUEST;

        RequestChangingParkParameters request = requests.get(rowIndex);
        request.setStatus(RequestStatus.REQUEST_ACCEPTED);
        Object msg = new Message(opCode, applicationWindowController.getUser().getUsername(), request);
        ClientUI.client.accept(msg);
        Message response = ClientCommunicator.msg;

        if (response.getMsgOpcode() != opCode || !((Boolean) response.getMsgData())) {
            errorTxt.setText("Something went wrong... Try again later.");
            return;
        }

        if (event.getSource() == authBtn)
            requests.remove(rowIndex);
        else
            requests.get(rowIndex).setStatus(RequestStatus.REQUEST_DECLINED);

        setupTable();
    }

    private void setupTable() {
        tableRequests.getItems().clear();

        parkMgrCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequesterName()));
        parkNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPark().getParkName()));
        paramCol.setCellValueFactory(cellData -> new SimpleStringProperty(ParkParameters.parameterToString(cellData.getValue().getParameter())));
        statusVal.setCellValueFactory(cellData -> new SimpleStringProperty(RequestStatus.statusToString(cellData.getValue().getStatus())));
        newValCol.setCellValueFactory(cellData -> new SimpleStringProperty(((Integer) cellData.getValue().getRequestedValue().intValue()).toString()));

        // Set row factory to handle clicks on rows
        tableRequests.setRowFactory(tv -> {
            TableRow<RequestChangingParkParameters> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    RequestChangingParkParameters clickedRowData = row.getItem();
                    rowIndex = row.getIndex();
                    // Handle the clicked row data, e.g., display it
                    System.out.println("Selected row data: " + clickedRowData);
                }
            });
            return row;
        });

        ObservableList<RequestChangingParkParameters> items = FXCollections.observableArrayList(requests);
        tableRequests.getItems().addAll(items);
    }

    private void getRequests() {
        ParkDepartmentManager parkDepartmentManager = (ParkDepartmentManager) applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER, parkDepartmentManager.getUsername(), parkDepartmentManager.getDepartmentID());

        ClientUI.client.accept(msg);
        Message response = ClientCommunicator.msg;

        if (response.getMsgOpcode() != OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER) {
            errorTxt.setText("Something went wrong... Try again later.");
            return;
        }

        requests = (ArrayList<RequestChangingParkParameters>) response.getMsgData();
    }

    public void start() {
        getRequests();
        setupTable();
    }
}
