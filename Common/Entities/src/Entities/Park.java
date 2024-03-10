package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The Park class represents a park entity with its properties.
 */
public class Park implements Serializable {
    private String ParkID;
    private String ParkName;
    private Integer capacity;
    private Integer GapVisitorsCapacity;
    private Timestamp DefaultVisitationTime;
    private Integer Department;
    private ParkManager ParkManager;

    /**
     * Constructs a new Park object with the specified parameters.
     *
     * @param parkID                The unique identifier of the park.
     * @param parkName              The name of the park.
     * @param capacity              The maximum capacity of the park.
     * @param gapVisitorsCapacity   The capacity for visitors during gaps.
     * @param defaultVisitationTime The default visitation time for the park.
     * @param department            The department to which the park belongs.
     * @param parkManager           The manager of the park.
     */
    public Park(String parkID, String parkName, Integer capacity, Integer gapVisitorsCapacity, Timestamp defaultVisitationTime, Integer department, ParkManager parkManager) {
        ParkID = parkID;
        ParkName = parkName;
        this.capacity = capacity;
        GapVisitorsCapacity = gapVisitorsCapacity;
        DefaultVisitationTime = defaultVisitationTime;
        Department = department;
        ParkManager = parkManager;
    }

    /**
     * Gets the park ID.
     *
     * @return The park ID.
     */
    public String getParkID() {
        return ParkID;
    }

    /**
     * Sets the park ID.
     *
     * @param parkID The park ID to set.
     */
    public void setParkID(String parkID) {
        ParkID = parkID;
    }

    /**
     * Gets the park name.
     *
     * @return The park name.
     */
    public String getParkName() {
        return ParkName;
    }

    /**
     * Sets the park name.
     *
     * @param parkName The park name to set.
     */
    public void setParkName(String parkName) {
        ParkName = parkName;
    }

    /**
     * Gets the maximum capacity of the park.
     *
     * @return The maximum capacity of the park.
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Sets the maximum capacity of the park.
     *
     * @param capacity The maximum capacity to set.
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the capacity for visitors during gaps.
     *
     * @return The gap visitors capacity.
     */
    public Integer getGapVisitorsCapacity() {
        return GapVisitorsCapacity;
    }

    /**
     * Sets the capacity for visitors during gaps.
     *
     * @param gapVisitorsCapacity The gap visitors capacity to set.
     */
    public void setGapVisitorsCapacity(Integer gapVisitorsCapacity) {
        GapVisitorsCapacity = gapVisitorsCapacity;
    }

    /**
     * Gets the default visitation time for the park.
     *
     * @return The default visitation time.
     */
    public Timestamp getDefaultVisitationTime() {
        return DefaultVisitationTime;
    }

    /**
     * Sets the default visitation time for the park.
     *
     * @param defaultVisitationTime The default visitation time to set.
     */
    public void setDefaultVisitationTime(Timestamp defaultVisitationTime) {
        DefaultVisitationTime = defaultVisitationTime;
    }

    /**
     * Gets the department to which the park belongs.
     *
     * @return The department ID.
     */
    public Integer getDepartment() {
        return Department;
    }

    /**
     * Sets the department to which the park belongs.
     *
     * @param department The department ID to set.
     */
    public void setDepartment(Integer department) {
        Department = department;
    }

    /**
     * Gets the manager of the park.
     *
     * @return The park manager.
     */
    public ParkManager getParkManager() {
        return ParkManager;
    }

    /**
     * Sets the manager of the park.
     *
     * @param parkManager The park manager to set.
     */
    public void setParkManager(ParkManager parkManager) {
        ParkManager = parkManager;
    }
}
