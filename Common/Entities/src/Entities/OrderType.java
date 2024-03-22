package Entities;

/**
 * Represents the type of an order, either single or group.
 */
public enum OrderType {
    /**
     * Represents a single order type.
     */
    ORD_TYPE_SINGLE(1),

    /**
     * Represents a group order type.
     */
    ORD_TYPE_GROUP(2);

    private int orderType;

    /**
     * Constructs an OrderType enum with the specified order type value.
     *
     * @param orderType The value representing the order type.
     */
    OrderType(int orderType) {
        this.orderType = orderType;
    }

    /**
     * Retrieves the order type value.
     *
     * @return The order type value.
     */
    public int getOrderType() {
        return orderType;
    }
}
