package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javax.naming.CommunicationException;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IssueVisitationReportController extends BaseController {
    @FXML
    private MFXButton btnGenerateReport;

    @FXML
    private Label errMsg;

    @FXML
    private Label generateResultMsg;

    @FXML
    private ComboBox<?> monthCmb;

    @FXML
    private ComboBox<?> parkCmb;

    @FXML
    private ComboBox<?> reportCmb;

    @FXML
    private ComboBox<?> yearCmb;

    public void cleanup() {
        generateResultMsg.setText("");
        errMsg.setText("");
        monthCmb.getSelectionModel().clearSelection();
        yearCmb.getSelectionModel().clearSelection();
        parkCmb.getSelectionModel().clearSelection();
        reportCmb.getSelectionModel().clearSelection();
    }

    @FXML
    public void onClickGenerateReport(ActionEvent event) {
        // Generate report and save to DB.
    }
}
