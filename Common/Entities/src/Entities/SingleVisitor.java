package Entities;

import java.io.Serializable;

/**
 * Represents a single visitor.
 * A single visitor can only create and manage single orders.
 */
public class SingleVisitor extends AbstractVisitor implements Serializable {

    private final String ErrorMsg = "A single Visitor can not create a group order";

    /**
     * Constructs a SingleVisitor with the provided user information.
     * Initializes the OrderBank for single orders.
     *
     * @param user        The user information.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     */
    public SingleVisitor(User user, String emailAdress, String ID, String firstName, String lastName) {
        super(user, emailAdress, ID, firstName, lastName);
        orders = new OrderBank(OrderType.ORD_TYPE_SINGLE);
        role = Role.ROLE_SINGLE_VISITOR;
    }

    /**
     * Constructs a SingleVisitor with the provided user information and existing orders.
     * Validates if the provided orders are for single type orders.
     *
     * @param user        The user information.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param orders      The existing orders to be associated with the visitor.
     * @throws IllegalArgumentException if the provided orders are not for single type orders.
     */
    public SingleVisitor(User user, String emailAdress, String ID, String firstName, String lastName, OrderBank orders) {
        super(user, emailAdress, ID, firstName, lastName, orders);
        if (orders.getOrdersType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        this.orders = orders;
        role = Role.ROLE_SINGLE_VISITOR;
    }

    /**
     * Constructs a SingleVisitor with the provided user information and a single order.
     * Validates if the provided order is for a single type order.
     *
     * @param user        The user information.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param order       The single order associated with the visitor.
     * @throws IllegalArgumentException if the provided order is not for a single type order.
     */
    public SingleVisitor(User user, String emailAdress, String ID, String firstName, String lastName, Order order) {
        super(user, emailAdress, ID, firstName, lastName, order);
        if (order.getOrderType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        orders = new OrderBank(order);
        role = Role.ROLE_SINGLE_VISITOR;
    }

    /**
     * Constructs a SingleVisitor with the provided user credentials and initializes an OrderBank for single orders.
     *
     * @param username    The username of the visitor.
     * @param password    The password of the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     */
    public SingleVisitor(String username, String password, String emailAdress, String ID, String firstName, String lastName) {
        super(username, password, emailAdress, ID, firstName, lastName);
        orders = new OrderBank(OrderType.ORD_TYPE_SINGLE);
        role = Role.ROLE_SINGLE_VISITOR;
    }

    /**
     * Constructs a SingleVisitor with the provided user credentials and existing orders.
     * Validates if the provided orders are for single type orders.
     *
     * @param username    The username of the visitor.
     * @param password    The password of the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param orders      The existing orders to be associated with the visitor.
     * @throws IllegalArgumentException if the provided orders are not for single type orders.
     */
    public SingleVisitor(String username, String password, String emailAdress, String ID, String firstName, String lastName, OrderBank orders) {
        super(username, password, emailAdress, ID, firstName, lastName, orders);
        if (orders.getOrdersType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        this.orders = orders;
        role = Role.ROLE_SINGLE_VISITOR;
    }

    /**
     * Constructs a SingleVisitor with the provided user credentials and a single order.
     * Validates if the provided order is for a single type order.
     *
     * @param username    The username of the visitor.
     * @param password    The password of the visitor.
     * @param emailAdress The email address of the visitor.
     * @param ID          The ID of the visitor.
     * @param firstName   The first name of the visitor.
     * @param lastName    The last name of the visitor.
     * @param order       The single order associated with the visitor.
     * @throws IllegalArgumentException if the provided order is not for a single type order.
     */
    public SingleVisitor(String username, String password, String emailAdress, String ID, String firstName, String lastName, Order order) {
        super(username, password, emailAdress, ID, firstName, lastName, order);
        if (order.getOrderType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        orders = new OrderBank(order);
        role = Role.ROLE_SINGLE_VISITOR;
    }

    /**
     * Inserts a single order for the single visitor.
     * Validates if the provided order is for a single type order.
     *
     * @param order The single order to be inserted.
     * @throws IllegalArgumentException if the provided order is not for a single type order.
     */
    public void insertOrder(Order order) {
        if (order.getOrderType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        orders.insertOrder(order);
    }

    /**
     * Deletes a single order associated with the single visitor.
     * Validates if the provided order is for a single type order.
     *
     * @param order The single order to be deleted.
     * @throws IllegalArgumentException if the provided order is not for a single type order.
     */
    public void deleteOrder(Order order) {
        if (order.getOrderType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        orders.deleteOrder(order);
    }

    /**
     * Changes the status of an existing single order associated with the single visitor.
     * Validates if the provided order is for a single type order.
     *
     * @param order     The single order whose status is to be changed.
     * @param newStatus The new status to set for the single order.
     * @throws IllegalArgumentException if the provided order is not for a single type order.
     */
    public void ChangeExistingOrderStatus(Order order, OrderStatus newStatus) {
        if (order.getOrderType() != OrderType.ORD_TYPE_SINGLE)
            throw new IllegalArgumentException(ErrorMsg);
        orders.ChangeExistingOrderStatus(order, newStatus);
    }
}
