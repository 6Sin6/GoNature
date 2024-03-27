package Entities;

import java.io.Serializable;

/**
 * The ParkManager class represents a manager working in a park.
 * This class extends ParkEmployee and inherits basic employee functionality.
 */
public class ParkManager extends ParkEmployee implements Serializable {
    private String ParkID;

    /**
     * Constructs a ParkManager object with the specified parameters.
     *
     * @param firstName    The first name of the manager.
     * @param lastName     The last name of the manager.
     * @param username     The username of the manager.
     * @param password     The password of the manager.
     * @param emailAddress The email address of the manager.
     * @param ParkID       The parkID associated with the manager.
     */
    public ParkManager(String username, String password, String emailAddress, String ParkID, String firstName, String lastName) {
        super(firstName, lastName, username, password, emailAddress, null);
        this.ParkID = ParkID;
        role = Role.ROLE_PARK_MGR;
    }

    /**
     * Constructs a ParkManager object with the specified parameters.
     *
     * @param firstName    The first name of the manager.
     * @param lastName     The last name of the manager.
     * @param user         The user object representing the manager.
     * @param emailAddress The email address of the manager.
     * @param ParkID       The parkID associated with the manager.
     */
    public ParkManager(User user, String emailAddress, String ParkID, String firstName, String lastName) {
        super(user, emailAddress, null, firstName, lastName);
        this.ParkID = ParkID;
        role = Role.ROLE_PARK_MGR;
    }

    /**
     * Gets the park ID associated with the manager.
     *
     * @return The park ID associated with the manager.
     */
    public String getParkID() {
        return ParkID;
    }
}

