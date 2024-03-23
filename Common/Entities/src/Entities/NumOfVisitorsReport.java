package Entities;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The NumOfVisitorsReport class represents a report detailing the number of visitors to a park.
 * It extends the ParkReport class and contains information about group and single orders.
 */
public class NumOfVisitorsReport extends ParkReport implements Serializable {

    private OrderBank GroupOrders;
    private OrderBank SingleOrders;

    /**
     * Constructs a NumOfVisitorsReport object with the specified parameters.
     * Initializes group and single order banks with empty lists.
     *
     * @param date The timestamp representing the date of the report.
     * @param park The park associated with the report.
     */
    public NumOfVisitorsReport(Timestamp date, Park park) {
        super(date, park);
        GroupOrders = new OrderBank(OrderType.ORD_TYPE_GROUP);
        SingleOrders = new OrderBank(OrderType.ORD_TYPE_SINGLE);
    }

    /**
     * Constructs a NumOfVisitorsReport object with the specified parameters.
     *
     * @param date   The timestamp representing the date of the report.
     * @param park   The park associated with the report.
     * @param orders The order bank containing group or single orders.
     */
    public NumOfVisitorsReport(Timestamp date, Park park, OrderBank orders) {
        super(date, park);
        if (orders.getOrdersType() == OrderType.ORD_TYPE_GROUP) {
            GroupOrders = orders;
            SingleOrders = new OrderBank(OrderType.ORD_TYPE_SINGLE);
        } else {
            SingleOrders = orders;
            GroupOrders = new OrderBank(OrderType.ORD_TYPE_GROUP);
        }
    }

    /**
     * Constructs a NumOfVisitorsReport object with the specified parameters.
     * Throws an exception if both orders are of the same type.
     *
     * @param date    The timestamp representing the date of the report.
     * @param park    The park associated with the report.
     * @param orders1 The first order bank.
     * @param orders2 The second order bank.
     * @throws IllegalArgumentException If both orders are of the same type.
     */
    public NumOfVisitorsReport(Timestamp date, Park park, OrderBank orders1, OrderBank orders2) {
        super(date, park);
        if (orders1.getOrdersType() == orders1.getOrdersType()) {
            throw new IllegalArgumentException("Orders must be of different types");
        }
        GroupOrders = orders1.getOrdersType() == OrderType.ORD_TYPE_GROUP ? orders1 : orders2;
        SingleOrders = orders1.getOrdersType() == OrderType.ORD_TYPE_SINGLE ? orders1 : orders2;
    }

    /**
     * Adds a group order to the report.
     *
     * @param order The group order to add.
     * @throws IllegalArgumentException If the order type is not group.
     */
    public void addGroupOrder(Order order) {
        if (order.getOrderType() == OrderType.ORD_TYPE_GROUP)
            GroupOrders.insertOrder(order);
        else
            throw new IllegalArgumentException("Order type must be group");
    }

    /**
     * Adds a single order to the report.
     *
     * @param order The single order to add.
     * @throws IllegalArgumentException If the order type is not single.
     */
    public void addSingleOrder(Order order) {
        if (order.getOrderType() == OrderType.ORD_TYPE_SINGLE)
            SingleOrders.insertOrder(order);
        else
            throw new IllegalArgumentException("Order type must be single");
    }


    @Override
    /**
     * This method is intended to create a PDF Blob from the NumOfVisitorsReport.
     * Currently, this method is not implemented and returns null.
     *
     * @return A Blob object representing the PDF version of the report.
     * @throws DocumentException If there is an error while creating the PDF document.
     * @throws SQLException If there is an error while converting the PDF to a Blob.
     * @throws IOException If there is an error while handling the PDF file.
     * */
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        return null;
    }
}
