package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The UsageReportDetail class represents a detail of a usage report.
 * It contains information about the date and whether the capacity was full or not.
 */
public class UsageReportDetail implements Serializable {
    private Timestamp date;
    private Boolean fullCapacity;

    /**
     * Constructs a UsageReportDetail object with the specified parameters.
     *
     * @param date         The timestamp representing the date of the report detail.
     * @param fullCapacity A boolean indicating whether the capacity was full at that time.
     */
    public UsageReportDetail(Timestamp date, Boolean fullCapacity) {
        this.date = date;
        this.fullCapacity = fullCapacity;
    }

    /**
     * Gets the date of the report detail.
     *
     * @return The timestamp representing the date.
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * Sets the date of the report detail.
     *
     * @param date The timestamp representing the date to set.
     */
    public void setDate(Timestamp date) {
        this.date = date;
    }

    /**
     * Checks whether the capacity was full at the time of the report detail.
     *
     * @return True if the capacity was full, false otherwise.
     */
    public Boolean getFullCapacity() {
        return fullCapacity;
    }

    /**
     * Sets whether the capacity was full at the time of the report detail.
     *
     * @param fullCapacity A boolean indicating whether the capacity was full.
     */
    public void setFullCapacity(Boolean fullCapacity) {
        this.fullCapacity = fullCapacity;
    }

    /**
     * Compares this UsageReportDetail to another object for equality.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof UsageReportDetail) {
            UsageReportDetail report = (UsageReportDetail) o;
            return this.date.equals(report.date) && this.fullCapacity.equals(report.fullCapacity);
        }
        return false;
    }
}
