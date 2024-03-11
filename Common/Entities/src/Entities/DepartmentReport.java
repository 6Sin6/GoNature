package Entities;

import java.sql.Timestamp;

/**
 * The DepartmentReport class represents a report associated with a department.
 * It contains information about the date and the department ID.
 */
public class DepartmentReport {
    private Timestamp Date;
    private Integer department;

    /**
     * Constructs a DepartmentReport object with the specified parameters.
     *
     * @param date       The timestamp representing the date of the report.
     * @param department The ID of the department associated with the report.
     */
    public DepartmentReport(Timestamp date, Integer department) {
        Date = date;
        this.department = department;
    }

    /**
     * Retrieves the date of the report.
     *
     * @return The timestamp representing the date of the report.
     */
    public Timestamp getDate() {
        return Date;
    }

    /**
     * Sets the date of the report.
     *
     * @param date The timestamp representing the date to set.
     */
    public void setDate(Timestamp date) {
        Date = date;
    }

    /**
     * Retrieves the ID of the department associated with the report.
     *
     * @return The ID of the department.
     */
    public Integer getDepartmentID() {
        return department;
    }

    /**
     * Sets the ID of the department associated with the report.
     *
     * @param department The ID of the department to set.
     */
    public void setDepartment(Integer department) {
        this.department = department;
    }
}
