package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a group order made by a client.
 */
public class GroupOrder extends Order implements Serializable {
    private Integer groupSize;
    private VisitorGroupGuide visitorGroupeGuide;

    /**
     * Constructs a new GroupOrder object with the specified attributes.
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
     * @param groupSize          The size of the group associated with the order.
     * @param visitorGroupeGuide The visitor or group guide associated with the order.
     */
    public GroupOrder(String parkID, Timestamp date, String clientEmailAddress, String phoneNumber, OrderStatus status, Timestamp enteredTime, Timestamp exitedTime, String orderID, OrderType orderType, Integer groupSize, VisitorGroupGuide visitorGroupeGuide) {
        super(parkID, date, clientEmailAddress, phoneNumber, status, enteredTime, exitedTime, orderID, orderType, 1);
        this.groupSize = groupSize;
        this.visitorGroupeGuide = visitorGroupeGuide;
    }

    /**
     * Retrieves the visitor or group guide associated with the order.
     *
     * @return The visitor or group guide associated with the order.
     */
    public VisitorGroupGuide getVisitorGroupeGuide() {
        return visitorGroupeGuide;
    }

    /**
     * Sets the visitor or group guide associated with the order.
     *
     * @param visitorGroupeGuide The visitor or group guide to be set.
     */
    public void setVisitorGroupeGuide(VisitorGroupGuide visitorGroupeGuide) {
        this.visitorGroupeGuide = visitorGroupeGuide;
    }

    /**
     * Retrieves the number of visitors associated with the order (group size).
     *
     * @return The number of visitors (group size).
     */
    @Override
    public Integer getNumOfVisitors() {
        return this.groupSize;
    }

    /**
     * Sets the number of visitors associated with the order (group size).
     *
     * @param groupSize The number of visitors (group size) to be set.
     */
    @Override
    public void setNumOfVisitors(Integer groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Retrieves the size of the group associated with the order.
     *
     * @return The size of the group.
     */
    public Integer getGroupSize() {
        return groupSize;
    }

    /**
     * Sets the size of the group associated with the order.
     *
     * @param groupSize The size of the group to be set.
     */
    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }
}
