package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents an order made by a client.
 */
public class Order implements Serializable {
    private String ParkID;
    private Timestamp Date;
    private String ClientEmailAddress;
    private String PhoneNumber;
    private OrderStatus Status;
    private Timestamp EnteredTime;
    private Timestamp ExitedTime;
    private String OrderID;
    private OrderType OrderType;
    private Integer NumOfVisitors;

    /**
     * Constructs a new Order object with the specified attributes.
     *
     * @param parkID             The ID of the park associated with the order.
     * @param date               The date and time of the order.
     * @param clientEmailAddress The email address of the client who made the order.
     * @param phoneNumber        The phone number of the client who made the order.
     * @param status             The status of the order.
     * @param enteredTime        The time the client entered the park.
     * @param exitedTime         The time the client exited the park.
     * @param orderID            The unique ID of the order.
     * @param orderType          The type of the order.
     * @param numOfVisitors      The number of visitors associated with the order.
     */
    public Order(String parkID, Timestamp date, String clientEmailAddress, String phoneNumber, OrderStatus status, Timestamp enteredTime, Timestamp exitedTime, String orderID, OrderType orderType, Integer numOfVisitors) {
        ParkID = parkID;
        Date = date;
        ClientEmailAddress = clientEmailAddress;
        PhoneNumber = phoneNumber;
        Status = status;
        EnteredTime = enteredTime;
        ExitedTime = exitedTime;
        OrderID = orderID;
        OrderType = orderType;
        NumOfVisitors = numOfVisitors;
    }

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

    // Getters and setters for the attributes

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
    public Timestamp getDate() {
        return Date;
    }

    /**
     * Sets the date and time of the order.
     *
     * @param date The date and time to be set.
     */
    public void setDate(Timestamp date) {
        Date = date;
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
    public OrderStatus getStatus() {
        return Status;
    }

    /**
     * Sets the status of the order.
     *
     * @param status The status to be set.
     */
    public void setStatus(OrderStatus status) {
        Status = status;
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
