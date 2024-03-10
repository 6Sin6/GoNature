package Entities;

import CommonUtils.CommonUtils;

import java.io.Serializable;

/**
 * Represents a park employee entity, extending the User class.
 */
public class ParkEmployee extends User implements Serializable {
    private String EmailAdress;
    private Park Park;

    /**
     * Constructs a new ParkEmployee object with the provided user, email address, and park.
     *
     * @param user        The user object containing username and password.
     * @param EmailAdress The email address of the park employee.
     * @param Park        The park associated with the employee.
     */
    public ParkEmployee(User user, String EmailAdress, Park Park) {
        super(user.getUsername(), user.getPassword());
        this.EmailAdress = EmailAdress;
        this.Park = Park;
        role = Role.ROLE_PARK_EMPLOYEE;
    }

    /**
     * Constructs a new ParkEmployee object with the provided username, password, email address, and park.
     *
     * @param username    The username of the park employee.
     * @param password    The password of the park employee.
     * @param EmailAdress The email address of the park employee.
     * @param Park        The park associated with the employee.
     */
    public ParkEmployee(String username, String password, String EmailAdress, Park Park) {
        super(username, password);
        this.EmailAdress = EmailAdress;
        this.Park = Park;
        role = Role.ROLE_PARK_EMPLOYEE;

    }

    /**
     * Sets the park associated with the employee.
     *
     * @param park The park to be set.
     */
    public void setPark(Park park) {
        Park = park;
    }

    /**
     * Sets the email address of the park employee.
     *
     * @param emailAdress The email address to be set.
     */
    public void setEmailAdress(String emailAdress) {
        if (CommonUtils.isEmailAddressValid(emailAdress)) {
            EmailAdress = emailAdress;
        }
    }

    /**
     * Retrieves the email address of the park employee.
     *
     * @return The email address of the park employee.
     */
    public String getEmailAdress() {
        return EmailAdress;
    }

    /**
     * Retrieves the park associated with the employee.
     *
     * @return The park associated with the employee.
     */
    public Park getPark() {
        return Park;
    }
}
