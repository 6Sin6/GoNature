package Entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Represents a visitation report for a specific department.
 */
public class VisitationReport extends DepartmentReport implements Serializable {
    private ArrayList<UsageReport> reports;

    /**
     * Constructs a new VisitationReport object with the specified date and department.
     *
     * @param date       The date of the visitation report.
     * @param department The department associated with the report.
     */
    public VisitationReport(Timestamp date, Integer department) {
        super(date, department);
        reports = new ArrayList<>();
    }

    /**
     * Constructs a new VisitationReport object with the specified date, department, and reports.
     *
     * @param date       The date of the visitation report.
     * @param department The department associated with the report.
     * @param reports    The list of usage reports to include in the visitation report.
     */
    public VisitationReport(Timestamp date, Integer department, ArrayList<UsageReport> reports) {
        super(date, department);
        this.reports = reports;
    }

    /**
     * Adds a usage report to the visitation report.
     *
     * @param report The usage report to add.
     */
    public void addReport(UsageReport report) {
        reports.add(report);
    }

    /**
     * Retrieves the list of usage reports included in the visitation report.
     *
     * @return The list of usage reports.
     */
    public ArrayList<UsageReport> getReports() {
        return reports;
    }

    /**
     * Sets the list of usage reports included in the visitation report.
     *
     * @param reports The list of usage reports to set.
     */
    public void setReports(ArrayList<UsageReport> reports) {
        this.reports = reports;
    }
}
