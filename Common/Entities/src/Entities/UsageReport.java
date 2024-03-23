package Entities;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The UsageReport class represents a report detailing the usage of a park.
 * It extends the ParkReport class and contains a bank of UsageReportDetail objects.
 */
public class UsageReport extends ParkReport implements Serializable {

    private UsageReportDetailBank usageReportDetailBank;

    /**
     * Constructs a UsageReport object with the specified parameters.
     *
     * @param date                  The timestamp representing the date of the report.
     * @param park                  The park associated with the report.
     * @param usageReportDetailBank The bank containing the details of the usage report.
     */
    public UsageReport(Timestamp date, Park park, UsageReportDetailBank usageReportDetailBank) {
        super(date, park);
        this.usageReportDetailBank = usageReportDetailBank;
    }

    /**
     * Constructs a UsageReport object with the specified parameters.
     *
     * @param date The timestamp representing the date of the report.
     * @param park The park associated with the report.
     */
    public UsageReport(Timestamp date, Park park) {
        super(date, park);
        this.usageReportDetailBank = new UsageReportDetailBank();
    }

    /**
     * Constructs a UsageReport object with the specified parameters.
     *
     * @param date   The timestamp representing the date of the report.
     * @param park   The park associated with the report.
     * @param report The initial report detail to add to the report bank.
     */
    public UsageReport(Timestamp date, Park park, UsageReportDetail report) {
        super(date, park);
        this.usageReportDetailBank = new UsageReportDetailBank(report);
    }

    /**
     * Adds a report detail to the report.
     *
     * @param report The report detail to add.
     */
    public void addReportDetail(UsageReportDetail report) {
        usageReportDetailBank.addReport(report);
    }

    /**
     * Removes a report detail from the report.
     *
     * @param report The report detail to remove.
     */
    public void removeReportDetail(UsageReportDetail report) {
        usageReportDetailBank.removeReport(report);
    }

    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        return null;
    }
}
