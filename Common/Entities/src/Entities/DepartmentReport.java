package Entities;

import java.sql.Timestamp;

public class DepartmentReport {
    private Timestamp Date;
    private Integer department;

    public DepartmentReport(Timestamp date, Integer department) {
        Date = date;
        this.department = department;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public Integer getDepatmentID() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }
}
