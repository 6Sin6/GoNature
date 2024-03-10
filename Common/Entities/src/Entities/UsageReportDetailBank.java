package Entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The UsageReportDetailBank class represents a bank of usage report details.
 * It stores a collection of UsageReportDetail objects.
 */
public class UsageReportDetailBank implements Serializable {
    private ArrayList<UsageReportDetail> usageReportDetails;

    /**
     * Constructs an empty UsageReportDetailBank.
     * The bank initially contains no report details.
     */
    public UsageReportDetailBank() {
        usageReportDetails = new ArrayList<>();
    }

    /**
     * Constructs a UsageReportDetailBank with an initial report detail.
     *
     * @param report The initial report detail to add to the bank.
     */
    public UsageReportDetailBank(UsageReportDetail report) {
        usageReportDetails = new ArrayList<>();
        usageReportDetails.add(report);
    }

    /**
     * Adds a report detail to the bank.
     *
     * @param report The report detail to add.
     */
    public void addReport(UsageReportDetail report) {
        usageReportDetails.add(report);
    }

    /**
     * Removes a report detail from the bank.
     *
     * @param report The report detail to remove.
     * @return True if the report was successfully removed, false otherwise.
     */
    public Boolean removeReport(UsageReportDetail report) {
        if (usageReportDetails.contains(report)) {
            return usageReportDetails.remove(report);
        }
        return false;
    }
}
