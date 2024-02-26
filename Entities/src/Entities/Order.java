package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Serializable {
    private String ParkName;
    private String OrderNo;
    private Timestamp VisitationTime;
    private Integer NumberOfVisitors;
    private String TelephoneNumber;
    private String EmailAddress;

    public Order(String ParkName, String OrderNo, Timestamp VisitationTime, Integer NumberOfVisitors, String TelephoneNumber, String EmailAddress) {
        this.ParkName = ParkName;
        this.OrderNo = OrderNo;
        this.VisitationTime = VisitationTime;
        this.NumberOfVisitors = NumberOfVisitors;
        this.TelephoneNumber = TelephoneNumber;
        this.EmailAddress = EmailAddress;
    }

    public String getParkName() {
        return this.ParkName;
    }

    public String getOrderNo() {
        return this.OrderNo;
    }

    public Timestamp getVisitationTime() {
        return this.VisitationTime;
    }

    public Integer getNumberOfVisitors() {
        return this.NumberOfVisitors;
    }

    public String getTelephoneNumber() {
        return this.TelephoneNumber;
    }

    public String getEmailAddress() {
        return this.EmailAddress;
    }

    public void setParkName(String ParkName) {
        this.ParkName = ParkName;
    }

    public void setTelephoneNumber(String TelephoneNumber) {
        this.TelephoneNumber = TelephoneNumber;
    }

    public String toString() {
        return String.format("%s %s %s %d %s %s\n", ParkName, OrderNo, VisitationTime, NumberOfVisitors, TelephoneNumber, EmailAddress);
    }


}