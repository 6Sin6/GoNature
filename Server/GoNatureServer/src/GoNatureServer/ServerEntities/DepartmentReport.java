package GoNatureServer.ServerEntities;


import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * The DepartmentReport class represents a report associated with a department.
 * It contains information about the date and the department ID.
 */
public abstract class DepartmentReport extends ReportConstructor {
    private final Integer departmentID;


    /**
     * Constructs a DepartmentReport object with the specified parameters.
     *
     * @param departmentID The ID of the department associated with the report.
     */
    protected DepartmentReport(Integer departmentID) throws DocumentException, IOException {
        super();
        this.departmentID = departmentID;
    }


    /**
     * Creates a PDF file based on the data in the specified ResultSet.
     *
     * @return A Blob object representing the PDF file.
     */
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        return null;
    }


    /**
     * Retrieves the ID of the department associated with the report.
     *
     * @return The ID of the department.
     */
    protected Integer getDepartmentID() {
        return departmentID;
    }

}
