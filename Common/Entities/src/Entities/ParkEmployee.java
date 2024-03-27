package Entities;

import CommonUtils.CommonUtils;

import java.io.Serializable;

/**
 * Represents a park employee entity, extending the User class.
 */
public class ParkEmployee extends User implements Serializable {
    private String firstName;
    private String lastName;

    private String EmailAddress;
    private Park Park;

    /**
     * Constructs a new ParkEmployee object with the provided user, email address, and park.
     *
     * @param user         The user object containing username and password.
     * @param EmailAddress The email address of the park employee.
     * @param Park         The park associated with the employee.
     */
    public ParkEmployee(User user, String EmailAddress, Park Park, String firstName, String lastName) {
        super(user.getUsername(), user.getPassword());
        this.firstName = firstName;
        this.lastName = lastName;
        this.EmailAddress = EmailAddress;
        this.Park = Park;
        role = Role.ROLE_PARK_EMPLOYEE;
    }

    /**
     * Constructs a new ParkEmployee object with the provided username, password, email address, and park.
     *
     * @param username     The username of the park employee.
     * @param password     The password of the park employee.
     * @param EmailAddress The email address of the park employee.
     * @param Park         The park associated with the employee.
     */
    public ParkEmployee(String firstName, String lastName, String username, String password, String EmailAddress, Park Park) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.EmailAddress = EmailAddress;
        this.Park = Park;
        role = Role.ROLE_PARK_EMPLOYEE;

    }


    public void setPark(Park park) {
        Park = park;
    }


    public void setEmailAddress(String emailAddress) {
        if (CommonUtils.isEmailAddressValid(emailAddress)) {
            EmailAddress = emailAddress;
        }
    }


    public String getEmailAddress() {
        return EmailAddress;
    }


    public Park getPark() {
        return Park;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}


