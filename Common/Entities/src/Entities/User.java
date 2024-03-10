package Entities;

import java.io.Serializable;

/**
 * Represents a user entity with a username and password.
 */
public class User implements Serializable {

    private String username;
    private String password;
    protected Role role;

    /**
     * Constructs a new User object with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }
}
