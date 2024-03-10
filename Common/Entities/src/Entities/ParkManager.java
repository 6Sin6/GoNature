package Entities;

import java.io.Serializable;

/**
 * The ParkManager class represents a manager working in a park.
 * This class extends ParkEmployee and inherits basic employee functionality.
 */
public class ParkManager extends ParkEmployee implements Serializable {

    /**
     * Constructs a ParkManager object with the specified parameters.
     *
     * @param username    The username of the manager.
     * @param password    The password of the manager.
     * @param emailAdress The email address of the manager.
     * @param park        The park associated with the manager.
     */
    public ParkManager(String username, String password, String emailAdress, Park park) {
        super(username, password, emailAdress, park);
        role = Role.ROLE_PARK_MGR;

    }

    /**
     * Constructs a ParkManager object with the specified parameters.
     *
     * @param user        The user object representing the manager.
     * @param emailAdress The email address of the manager.
     * @param park        The park associated with the manager.
     */
    public ParkManager(User user, String emailAdress, Park park) {

        super(user, emailAdress, park);
        role = Role.ROLE_PARK_MGR;

    }
}

