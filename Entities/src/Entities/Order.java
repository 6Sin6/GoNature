package Entities;

import java.sql.Date;

public class Order {
    private String ParkName;
    private String OrderNo;
    private Date VisitationTime;
    private Integer NumberOfVisitors;
    private String TelephoneNumber;

    public Order(String ParkName, String OrderNo, Date VisitationTime, Integer NumberOfVisitors, String TelephoneNumber) {
        this.ParkName = ParkName;
        this.OrderNo = OrderNo;
        this.VisitationTime = VisitationTime;
        this.NumberOfVisitors = NumberOfVisitors;
        this.TelephoneNumber = TelephoneNumber;
    }

    public String getParkName() {
        return this.ParkName;
    }

    public String getOrderNo() {
        return this.OrderNo;
    }

    public Date getVisitationTime() {
        return this.VisitationTime;
    }

    public Integer getNumberOfVisitors() {
        return this.NumberOfVisitors;
    }

    public String getTelephoneNumber() {
        return this.TelephoneNumber;
    }

    public void setParkName(String ParkName) {
        this.ParkName = ParkName;
    }

    public void setTelephoneNumber(String TelephoneNumber) {
        this.TelephoneNumber = TelephoneNumber;
    }

    public String toString() {
        return String.format("%s %s %s %d %s\n", ParkName, OrderNo, VisitationTime, NumberOfVisitors, TelephoneNumber);
    }
}
