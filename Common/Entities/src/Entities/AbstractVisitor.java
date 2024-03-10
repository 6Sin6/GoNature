package Entities;

import java.io.Serializable;

/**
 * Represents an abstract visitor.
 */
public abstract class AbstractVisitor extends User implements Serializable {
    protected String EmailAdress;
    protected String ID;
    protected String FirstName;
    protected String LastName;
    protected OrderBank orders;

    /**
     * Constructs a new AbstractVisitor object with the specified attributes.
     *
     * @param user        The user associated with the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     */
    public AbstractVisitor(User user, String emailAdress, String ID, String firstName, String lastName) {
        super(user.getUsername(), user.getPassword());
        EmailAdress = emailAdress;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
    }

    /**
     * Constructs a new AbstractVisitor object with the specified attributes and orders.
     *
     * @param user        The user associated with the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param orders      The orders associated with the visitor.
     */
    public AbstractVisitor(User user, String emailAdress, String ID, String firstName, String lastName, OrderBank orders) {
        super(user.getUsername(), user.getPassword());
        EmailAdress = emailAdress;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        this.orders = orders;
    }

    /**
     * Constructs a new AbstractVisitor object with the specified attributes and order.
     *
     * @param user        The user associated with the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param order       The order associated with the visitor.
     */
    public AbstractVisitor(User user, String emailAdress, String ID, String firstName, String lastName, Order order) {
        super(user.getUsername(), user.getPassword());
        EmailAdress = emailAdress;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        orders = new OrderBank(order);
    }

    /**
     * Constructs a new AbstractVisitor object with the specified attributes.
     *
     * @param username    The username of the visitor.
     * @param password    The password of the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     */
    public AbstractVisitor(String username, String password, String emailAdress, String ID, String firstName, String lastName) {
        super(username, password);
        EmailAdress = emailAdress;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
    }

    /**
     * Constructs a new AbstractVisitor object with the specified attributes and orders.
     *
     * @param username    The username of the visitor.
     * @param password    The password of the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param orders      The orders associated with the visitor.
     */
    public AbstractVisitor(String username, String password, String emailAdress, String ID, String firstName, String lastName, OrderBank orders) {
        super(username, password);
        EmailAdress = emailAdress;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        this.orders = orders;
    }

    /**
     * Constructs a new AbstractVisitor object with the specified attributes and order.
     *
     * @param username    The username of the visitor.
     * @param password    The password of the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param order       The order associated with the visitor.
     */
    public AbstractVisitor(String username, String password, String emailAdress, String ID, String firstName, String lastName, Order order) {
        super(username, password);
        EmailAdress = emailAdress;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        orders = new OrderBank(order);
    }
}
