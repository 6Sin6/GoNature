package Entities;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The NumOfVisitorsReport class represents a report detailing the number of visitors to a park.
 * It extends the ParkReport class and contains information about group and single orders.
 */
public class NumOfVisitorsReport extends ParkReport implements Serializable
{
    private int[][] reportData;

    /**
     * Constructs a NumOfVisitorsReport object with the specified parameters.
     *
     * @param parkID The park associated with the report.
     * @param reportData The report data as a 2D array.
     */
    public NumOfVisitorsReport(Integer parkID, int[][] reportData)
    {
        super(parkID);
        this.reportData = reportData;
    }



    @Override
    /**
     * This method is intended to create a PDF Blob from the NumOfVisitorsReport.
     * Currently, this method is not implemented and returns null.
     *
     * @return A Blob object representing the PDF version of the report.
     * @throws DocumentException If there is an error while creating the PDF document.
     * @throws SQLException If there is an error while converting the PDF to a Blob.
     * @throws IOException If there is an error while handling the PDF file.
     * */
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        return null;
    }
}
