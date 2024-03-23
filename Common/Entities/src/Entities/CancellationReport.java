package Entities;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The CancellationReport class represents a report detailing cancelled orders associated with a department.
 * It extends the DepartmentReport class and contains information about cancelled group and single orders.
 */
public class CancellationReport extends DepartmentReport implements Serializable {

    /**
     * Constructs a CancellationReport object with the specified parameters.
     * Initializes group and single order banks with cancelled orders.
     * @param department The ID of the department associated with the report.
     */
    public CancellationReport(Integer department) {
        super(department);
    }

    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        return null;
    }
}
