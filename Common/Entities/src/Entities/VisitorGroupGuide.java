package Entities;

import java.io.Serializable;

/**
 * Represents a visitor group guide.
 * A group guide can only create and manage group orders.
 */
public class VisitorGroupGuide extends AbstractVisitor implements Serializable {

    private final String ErrorMsg = "A visitor group guide can not order a single visit";

    /**
     * Constructs a VisitorGroupGuide with the provided user information.
     * Initializes the OrderBank for group orders.
     *
     * @param user        The user information.
     * @param emailAddress The email address of the group guide.
     * @param ID          The ID of the group guide.
     * @param firstName   The first name of the group guide.
     * @param lastName    The last name of the group guide.
     */
    public VisitorGroupGuide(User user, String emailAddress, String ID, String firstName, String lastName) {
        super(user, emailAddress, ID, firstName, lastName);
        orders = new OrderBank(OrderType.ORD_TYPE_GROUP);
        role = Role.ROLE_VISITOR_GROUP_GUIDE;
    }

    /**
     * Constructs a VisitorGroupGuide with the provided user information and existing orders.
     * Validates if the provided orders are for group type orders.
     *
     * @param user        The user information.
     * @param emailAddress The email address of the group guide.
     * @param ID          The ID of the group guide.
     * @param firstName   The first name of the group guide.
     * @param lastName    The last name of the group guide.
     * @param orders      The existing orders to be associated with the group guide.
     * @throws IllegalArgumentException if the provided orders are not for group type orders.
     */
    public VisitorGroupGuide(User user, String emailAddress, String ID, String firstName, String lastName, OrderBank orders) {
        super(user, emailAddress, ID, firstName, lastName, orders);
        if (orders.getOrdersType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        this.orders = orders;
        role = Role.ROLE_VISITOR_GROUP_GUIDE;

    }

    /**
     * Constructs a VisitorGroupGuide with the provided user information and a group order.
     * Validates if the provided order is for a group type order.
     *
     * @param user        The user information.
     * @param emailAddress The email address of the group guide.
     * @param ID          The ID of the group guide.
     * @param firstName   The first name of the group guide.
     * @param lastName    The last name of the group guide.
     * @param order       The group order associated with the group guide.
     * @throws IllegalArgumentException if the provided order is not for a group type order.
     */
    public VisitorGroupGuide(User user, String emailAddress, String ID, String firstName, String lastName, Order order) {
        super(user, emailAddress, ID, firstName, lastName, order);
        if (order.getOrderType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        orders = new OrderBank(order);
        role = Role.ROLE_VISITOR_GROUP_GUIDE;

    }

    /**
     * Constructs a VisitorGroupGuide with the provided user credentials and initializes an OrderBank for group orders.
     *
     * @param username    The username of the group guide.
     * @param password    The password of the group guide.
     * @param emailAddress The email address of the group guide.
     * @param ID          The ID of the group guide.
     * @param firstName   The first name of the group guide.
     * @param lastName    The last name of the group guide.
     */
    public VisitorGroupGuide(String username, String password, String emailAddress, String ID, String firstName, String lastName) {
        super(username, password, emailAddress, ID, firstName, lastName);
        orders = new OrderBank(OrderType.ORD_TYPE_GROUP);
        role = Role.ROLE_VISITOR_GROUP_GUIDE;

    }

    /**
     * Constructs a VisitorGroupGuide with the provided user credentials and existing orders.
     * Validates if the provided orders are for group type orders.
     *
     * @param username    The username of the group guide.
     * @param password    The password of the group guide.
     * @param emailAddress The email address of the group guide.
     * @param ID          The ID of the group guide.
     * @param firstName   The first name of the group guide.
     * @param lastName    The last name of the group guide.
     * @param orders      The existing orders to be associated with the group guide.
     * @throws IllegalArgumentException if the provided orders are not for group type orders.
     */
    public VisitorGroupGuide(String username, String password, String emailAddress, String ID, String firstName, String lastName, OrderBank orders) {
        super(username, password, emailAddress, ID, firstName, lastName, orders);
        if (orders.getOrdersType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        this.orders = orders;
        role = Role.ROLE_VISITOR_GROUP_GUIDE;

    }

    /**
     * Constructs a VisitorGroupGuide with the provided user credentials and a group order.
     * Validates if the provided order is for a group type order.
     *
     * @param username    The username of the group guide.
     * @param password    The password of the group guide.
     * @param emailAddress The email address of the group guide.
     * @param ID          The ID of the group guide.
     * @param firstName   The first name of the group guide.
     * @param lastName    The last name of the group guide.
     * @param order       The group order associated with the group guide.
     * @throws IllegalArgumentException if the provided order is not for a group type order.
     */
    public VisitorGroupGuide(String username, String password, String emailAddress, String ID, String firstName, String lastName, Order order) {
        super(username, password, emailAddress, ID, firstName, lastName, order);
        if (order.getOrderType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        orders = new OrderBank(order);
        role = Role.ROLE_VISITOR_GROUP_GUIDE;

    }

    /**
     * Inserts a group order for the visitor group guide.
     * Validates if the provided order is for a group type order.
     *
     * @param order The group order to be inserted.
     * @throws IllegalArgumentException if the provided order is not for a group type order.
     */
    public void InsertOrder(Order order) {
        if (order.getOrderType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        orders.insertOrder(order);
    }

    /**
     * Deletes a group order from the visitor group guide.
     * Validates if the provided order is for a group type order.
     *
     * @param order The group order to be deleted.
     * @throws IllegalArgumentException if the provided order is not for a group type order.
     */
    public void DeleteOrder(Order order) {
        if (order.getOrderType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        orders.deleteOrder(order);
    }

    /**
     * Changes the status of an existing group order associated with the visitor group guide.
     * Validates if the provided order is for a group type order.
     *
     * @param order     The group order whose status is to be changed.
     * @param newStatus The new status to set for the group order.
     * @throws IllegalArgumentException if the provided order is not for a group type order.
     */
    public void ChangeExistingOrderStatus(Order order, OrderStatus newStatus) {
        if (order.getOrderType() != OrderType.ORD_TYPE_GROUP)
            throw new IllegalArgumentException(ErrorMsg);
        orders.ChangeExistingOrderStatus(order, newStatus);
    }
}
