package Entities;


import com.itextpdf.text.DocumentException;
import org.jfree.chart.JFreeChart;

import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The DepartmentReport class represents a report associated with a department.
 * It contains information about the date and the department ID.
 */
public abstract class DepartmentReport extends ReportConstructor {
    private Integer departmentID;

    /**
     * Constructs a DepartmentReport object with the specified parameters.
     * @param departmentID The ID of the department associated with the report.
     */
    public DepartmentReport(Integer departmentID) {
        this.departmentID = departmentID;
    }

    /**
     * Retrieves the ID of the department associated with the report.
     *
     * @return The ID of the department.
     */
    public Integer getDepartmentID() {
        return departmentID;
    }

    /**
     * Sets the ID of the department associated with the report.
     *
     * @param departmentID The ID of the department to set.
     */
    public void setDepartmentID(Integer departmentID) {
        this.departmentID = departmentID;
    }
}
