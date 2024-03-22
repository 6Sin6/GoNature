package Entities;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;


/**
 * Represents an order made by a client.
 */
public class Order implements Serializable {
    private String ParkID;
    private Timestamp VisitationDate;
    private String ClientEmailAddress;
    private String PhoneNumber;
    private OrderStatus orderStatus;
    private Timestamp EnteredTime;
    private Timestamp ExitedTime;
    private String OrderID;
    private OrderType OrderType;
    private Integer NumOfVisitors;
    private String VisitorID;

    public static final Double pricePerVisitor = 60.0;

    /**
     * Constructs a new Order object with the specified attributes.
     *
     * @param VisitorID          The ID of the visitor associated with the order.
     * @param parkID             The ID of the park associated with the order.
     * @param visitationDate     The date and time of the order.
     * @param clientEmailAddress The email address of the client who made the order.
     * @param phoneNumber        The phone number of the client who made the order.
     * @param orderStatus             The status of the order.
     * @param enteredTime        The time the client entered the park.
     * @param exitedTime         The time the client exited the park.
     * @param orderID            The unique ID of the order.
     * @param orderType          The type of the order.
     * @param numOfVisitors      The number of visitors associated with the order.
     */
    public Order(String VisitorID, String parkID, Timestamp visitationDate, String clientEmailAddress, String phoneNumber, OrderStatus orderStatus, Timestamp enteredTime, Timestamp exitedTime, String orderID, OrderType orderType, Integer numOfVisitors) {
        this.VisitorID = VisitorID;
        ParkID = parkID;
        VisitationDate = visitationDate;
        ClientEmailAddress = clientEmailAddress;
        PhoneNumber = phoneNumber;
        this.orderStatus = orderStatus;
        EnteredTime = enteredTime;
        ExitedTime = exitedTime;
        OrderID = orderID;
        OrderType = orderType;
        NumOfVisitors = numOfVisitors;
    }
//    public Order(Order o, Timestamp setNewStartTime, Timestamp setNewEndTime) {
//        this.VisitorID = o.getVisitorID();
//        ParkID = o.getParkID();
//        VisitationDate = o.getVisitationDate();
//        ClientEmailAddress = o.getClientEmailAddress();
//        PhoneNumber = o.getPhoneNumber();
//        this.orderStatus = o.getOrderStatus();
//        EnteredTime = setNewStartTime;
//        ExitedTime = setNewEndTime;
//        OrderID = o.getOrderID();
//        OrderType = o.getOrderType();
//        NumOfVisitors = o.getNumOfVisitors();
//    }

    /**
     * Retrieves the number of visitors associated with the order.
     *
     * @return The number of visitors.
     */
    public Integer getNumOfVisitors() {
        return NumOfVisitors;
    }

    /**
     * Sets the number of visitors associated with the order.
     *
     * @param numOfVisitors The number of visitors to be set.
     */
    public void setNumOfVisitors(Integer numOfVisitors) {
        NumOfVisitors = numOfVisitors;
    }

    /**
     * Retrieves the ID of the park associated with the order.
     *
     * @return The ID of the park.
     */
    public String getParkID() {
        return ParkID;
    }

    /**
     * Sets the ID of the park associated with the order.
     *
     * @param parkID The ID of the park to be set.
     */
    public void setParkID(String parkID) {
        ParkID = parkID;
    }

    /**
     * Retrieves the date and time of the order.
     *
     * @return The date and time of the order.
     */
    public Timestamp getVisitationDate() {
        return VisitationDate;
    }

    /**
     * Sets the date and time of the order.
     *
     * @param visitationDate The date and time to be set.
     */
    public void setVisitationDate(Timestamp visitationDate) {
        VisitationDate = visitationDate;
    }

    /**
     * Retrieves the email address of the client who made the order.
     *
     * @return The email address of the client.
     */
    public String getClientEmailAddress() {
        return ClientEmailAddress;
    }

    /**
     * Sets the email address of the client who made the order.
     *
     * @param clientEmailAddress The email address to be set.
     */
    public void setClientEmailAddress(String clientEmailAddress) {
        ClientEmailAddress = clientEmailAddress;
    }

    /**
     * Retrieves the phone number of the client who made the order.
     *
     * @return The phone number of the client.
     */
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    /**
     * Sets the phone number of the client who made the order.
     *
     * @param phoneNumber The phone number to be set.
     */
    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    /**
     * Retrieves the status of the order.
     *
     * @return The status of the order.
     */
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    /**
     * Sets the status of the order.
     *
     * @param orderStatus The status to be set.
     */
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Retrieves the time the client entered the park.
     *
     * @return The time the client entered the park.
     */
    public Timestamp getEnteredTime() {
        return EnteredTime;
    }

    /**
     * Sets the time the client entered the park.
     *
     * @param enteredTime The time to be set.
     */
    public void setEnteredTime(Timestamp enteredTime) {
        EnteredTime = enteredTime;
    }

    /**
     * Retrieves the time the client exited the park.
     *
     * @return The time the client exited the park.
     */
    public Timestamp getExitedTime() {
        return ExitedTime;
    }

    /**
     * Sets the time the client exited the park.
     *
     * @param exitedTime The time to be set.
     */
    public void setExitedTime(Timestamp exitedTime) {
        ExitedTime = exitedTime;
    }

    /**
     * Retrieves the unique ID of the order.
     *
     * @return The unique ID of the order.
     */
    public String getOrderID() {
        return OrderID;
    }

    /**
     * Sets the unique ID of the order.
     *
     * @param orderID The unique ID to be set.
     */
    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    /**
     * Retrieves the type of the order.
     *
     * @return The type of the order.
     */
    public OrderType getOrderType() {
        return OrderType;
    }

    /**
     * Sets the type of the order.
     *
     * @param orderType The type of the order to be set.
     */
    public void setOrderType(OrderType orderType) {
        OrderType = orderType;
    }

    /**
     * Retrieves the ID of the visitor associated with the order.
     *
     * @return The ID of the visitor.
     */
    public String getVisitorID() {
        return VisitorID;
    }

    /**
     * Sets the ID of the visitor associated with the order.
     *
     * @param visitorID The ID of the visitor to be set.
     */
    public void setVisitorID(String visitorID) {
        VisitorID = visitorID;
    }

    /**
     * Overrides the equals method to compare Order objects based on their OrderID.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal (have the same OrderID), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Order) {
            Order order = (Order) o;
            return order.getOrderID().equals(this.OrderID);
        }
        return false;
    }
}
