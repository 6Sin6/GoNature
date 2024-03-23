package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The ParkReport class represents a report associated with a park.
 * This class is abstract and serves as a base class for specific types of reports.
 */
public abstract class ParkReport extends ReportConstructor implements Serializable {
    private Integer parkID;



    /**
     * Constructs a ParkReport object with the specified parameters.
     *
     * @param parkID The park id associated with the report.
     */
    public ParkReport(Integer parkID)
    {
        this.parkID = parkID;
    }



    /**
     * Gets the park id associated with the report.
     *
     * @return The park id associated with the report.
     */
    public Integer getParkID() {
        return parkID;
    }



}
