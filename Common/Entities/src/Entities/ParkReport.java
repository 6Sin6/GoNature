package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The ParkReport class represents a report associated with a park.
 * This class is abstract and serves as a base class for specific types of reports.
 */
public abstract class ParkReport implements Serializable {
    private Timestamp Date;
    private Park park;

    /**
     * Constructs a ParkReport object with the specified parameters.
     *
     * @param date The date of the report.
     * @param park The park associated with the report.
     */
    public ParkReport(Timestamp date, Park park) {
        Date = date;
        this.park = park;
    }

    /**
     * Gets the date of the report.
     *
     * @return The date of the report.
     */
    public Timestamp getDate() {
        return Date;
    }

    /**
     * Sets the date of the report.
     *
     * @param date The date of the report to set.
     */
    public void setDate(Timestamp date) {
        Date = date;
    }

    /**
     * Gets the park associated with the report.
     *
     * @return The park associated with the report.
     */
    public Park getPark() {
        return park;
    }

    /**
     * Sets the park associated with the report.
     *
     * @param park The park associated with the report to set.
     */
    public void setPark(Park park) {
        this.park = park;
    }
}
