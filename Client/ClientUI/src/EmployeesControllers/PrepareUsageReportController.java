package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class PrepareUsageReportController extends BaseController
{

    @FXML
    private MFXButton btn;

    @FXML
    private TableView<?> tableReportDetails;

    public void cleanup()
    {

    }

    @FXML
    void OnClickSubmitButton(ActionEvent event) {

    }

    /*
    private ArrayList<Order> getAllOrderFromMonth(String month)
    {
        ArrayList<Order> orders = new ArrayList<>();
        String parkID = ((ParkManager) this.applicationWindowController.getUser()).getParkID();


        return orders;
    }


    private ArrayList<AcceptedRequest> getAllRequestsFromMonth(String month)
    {
        String parkID = ((ParkManager) this.applicationWindowController.getUser()).getParkID();
        String capacityParameter = String.valueOf(ParkParameters.PARK_CAPACITY.getParameterVal());
        String[] parametersForSQL = new String[]{month, parkID, capacityParameter};

        Object message = new Message(OP_GET_REQUESTS_OF_MONTH, this.applicationWindowController.getUser().getUsername(), parametersForSQL);
        ClientUI.client.accept(message);

        Message response = ClientCommunicator.msg;
        ArrayList<RequestChangingParkParameters> requests = (ArrayList<RequestChangingParkParameters>) response.getMsgData();


        return null;
    }


    private ArrayList<AcceptedRequest> BuildAcceptedRequests(ArrayList<RequestChangingParkParameters> requests)
    {
        ArrayList<AcceptedRequest> acceptedRequests = new ArrayList<>();

        for (RequestChangingParkParameters request : requests)
        {
            acceptedRequests.add(new AcceptedRequest(request.getRequestedValue(), request.));
        }

        return acceptedRequests;
    }
*/
}