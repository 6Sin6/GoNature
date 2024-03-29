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


    /**
     * Returns a string representation of the order type.
     * This method overrides the toString method in the Object class.
     * It uses a switch statement to determine the string representation based on the enum constant.
     * If the enum constant is ORD_TYPE_SINGLE, it returns "Single/Family-Sized".
     * If the enum constant is ORD_TYPE_GROUP, it returns "Group".
     * If the enum constant is not recognized, it returns an empty string.
     *
     * @return A string representation of the order type.
     */
    @Override
    public String toString() {
        switch (this) {
            case ORD_TYPE_SINGLE:
                return "Single/Family-Sized";
            case ORD_TYPE_GROUP:
                return "Group";
            default:
                return "";
        }
    }
}
