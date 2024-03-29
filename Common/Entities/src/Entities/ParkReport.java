package Entities;

import GoNatureServer.ServerEntities.ReportConstructor;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.Serializable;

/**
 * The ParkReport class represents a report associated with a park.
 * This class is abstract and serves as a base class for specific types of reports.
 */
public abstract class ParkReport extends ReportConstructor implements Serializable {
    private final Integer parkID;
    private final String parkName;


    /**
     * Constructs a ParkReport object with the specified parameters.
     * @param parkID The park id associated with the report.
     * @param parkName The name of the park associated with the report.
     * @throws DocumentException If an error occurs while creating the report.
     * @throws IOException If an error occurs while creating the report.
     */
    protected ParkReport(Integer parkID, String parkName) throws DocumentException, IOException {
        super();
        this.parkID = parkID;
        this.parkName = parkName;
    }


    /**
     * Gets the park id associated with the report.
     *
     * @return The park id associated with the report.
     */
    protected Integer getParkID() {
        return parkID;
    }


    /**
     * Returns the name of the park associated with the report.
     *
     * @return the name of the park associated with the report
     */
    protected String getParkName() {
        return parkName;
    }
}
