package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The CancellationReport class represents a report detailing cancelled orders associated with a department.
 * It extends the DepartmentReport class and contains information about cancelled group and single orders.
 */
public class CancellationReport extends DepartmentReport implements Serializable {

    private OrderBank GroupOrders;
    private OrderBank SingleOrders;

    /**
     * Constructs a CancellationReport object with the specified parameters.
     * Initializes group and single order banks with cancelled orders.
     *
     * @param date       The timestamp representing the date of the report.
     * @param department The ID of the department associated with the report.
     */
    public CancellationReport(Timestamp date, Integer department) {
        super(date, department);
        GroupOrders = new OrderBank(OrderType.ORD_TYPE_GROUP);
        SingleOrders = new OrderBank(OrderType.ORD_TYPE_SINGLE);
    }

    /**
     * Constructs a CancellationReport object with the specified parameters.
     * Retrieves cancelled orders from the provided order bank.
     *
     * @param date       The timestamp representing the date of the report.
     * @param department The ID of the department associated with the report.
     * @param orders     The order bank containing cancelled orders.
     */
    public CancellationReport(Timestamp date, Integer department, OrderBank orders) {
        super(date, department);
        if (orders.getOrdersType() == OrderType.ORD_TYPE_GROUP) {
            GroupOrders = orders.getOrdersByStatus(OrderStatus.STATUS_CANCELLED);
            SingleOrders = new OrderBank(OrderType.ORD_TYPE_SINGLE);
        } else {
            SingleOrders = orders.getOrdersByStatus(OrderStatus.STATUS_CANCELLED);
            GroupOrders = new OrderBank(OrderType.ORD_TYPE_GROUP);
        }
    }

    /**
     * Constructs a CancellationReport object with the specified parameters.
     * Throws an exception if both orders are of the same type.
     *
     * @param date       The timestamp representing the date of the report.
     * @param department The ID of the department associated with the report.
     * @param orders1    The first order bank.
     * @param orders2    The second order bank.
     * @throws IllegalArgumentException If both orders are of the same type.
     */
    public CancellationReport(Timestamp date, Integer department, OrderBank orders1, OrderBank orders2) {
        super(date, department);
        if (orders1.getOrdersType() == orders1.getOrdersType()) {
            throw new IllegalArgumentException("Orders must be of different types");
        }
        GroupOrders = orders1.getOrdersType() == OrderType.ORD_TYPE_GROUP ? orders1.getOrdersByStatus(OrderStatus.STATUS_CANCELLED) : orders2.getOrdersByStatus(OrderStatus.STATUS_CANCELLED);
        SingleOrders = orders1.getOrdersType() == OrderType.ORD_TYPE_SINGLE ? orders1.getOrdersByStatus(OrderStatus.STATUS_CANCELLED) : orders2.getOrdersByStatus(OrderStatus.STATUS_CANCELLED);
    }

    /**
     * Adds a cancelled group order to the report.
     *
     * @param order The group order to add.
     * @throws IllegalArgumentException If the order type is not group.
     */
    public void addGroupOrder(Order order) {
        if (order.getOrderType() == OrderType.ORD_TYPE_GROUP) {
            if (order.getStatus() == OrderStatus.STATUS_CANCELLED) {
                GroupOrders.insertOrder(order);
            }
        } else
            throw new IllegalArgumentException("Order type must be group");
    }

    /**
     * Adds a cancelled single order to the report.
     *
     * @param order The single order to add.
     * @throws IllegalArgumentException If the order type is not single.
     */
    public void addSingleOrder(Order order) {
        if (order.getOrderType() == OrderType.ORD_TYPE_SINGLE) {
            if (order.getStatus() == OrderStatus.STATUS_CANCELLED) {
                SingleOrders.insertOrder(order);
            }
        } else
            throw new IllegalArgumentException("Order type must be single");
    }

}
