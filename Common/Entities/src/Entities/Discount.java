package Entities;

public enum Discount {
    /**
     * Represents the discount of a single visitor order.
     */
    PREORDERED_SINGLE_DISCOUNT(1),

    /**
     * Represents the discount of a family-sized order.
     */
    PREORDERED_FAMILY_DISCOUNT(2),

    /**
     * Represents the discount of a group order up to 15 visitors.
     */
    PREORDERED_GROUP_DISCOUNT(3),

    /**
     * Represents the discount of a spontaneous single visitor.
     */
    SPONTANEOUS_SINGLE_DISCOUNT(4),

    /**
     * Represents the discount of a spontaneous family-sized order.
     */
    SPONTANEOUS_FAMILY_DISCOUNT(5),

    /**
     * Represents the discount of a spontaneous group order.
     */
    SPONTANEOUS_GROUP_DISCOUNT(6),

    /**
     * Represents the discount of a prepaid group order that was booked before the visitation date.
     */
    PREPAID_PREORDERED_GROUP_DISCOUNT(7);

    private int discount;

    /**
     * Constructs a Discounts enum with the specified discount value.
     *
     * @param discount The value representing the discount.
     */
    Discount(int discount) {
        this.discount = discount;
    }


    public static Discount getDiscountType(OrderType type, OrderStatus status, boolean prepaid) {
        switch (type) {
            case ORD_TYPE_SINGLE:
                return status != OrderStatus.STATUS_SPONTANEOUS_ORDER_PENDING_PAYMENT ? Discount.PREORDERED_SINGLE_DISCOUNT : Discount.SPONTANEOUS_SINGLE_DISCOUNT;
            case ORD_TYPE_GROUP:
                if (status != OrderStatus.STATUS_SPONTANEOUS_ORDER_PENDING_PAYMENT)
                    return prepaid ? Discount.PREPAID_PREORDERED_GROUP_DISCOUNT : Discount.PREORDERED_GROUP_DISCOUNT;
                return Discount.SPONTANEOUS_GROUP_DISCOUNT;
        }
        return null;
    }

    /**
     * Applies the discount value associated with the enum constant
     * to the provided price.
     *
     * @param price The price to apply the discount to.
     * @return The price after the discount.
     */
    public static double applyDiscount(Double price, Discount discount) {
        switch (discount) {
            case PREORDERED_SINGLE_DISCOUNT:
            case PREORDERED_FAMILY_DISCOUNT:
                return price - price * 0.15;
            case PREORDERED_GROUP_DISCOUNT:
                return price - price * 0.25;
            case PREPAID_PREORDERED_GROUP_DISCOUNT:
                return applyDiscount(price, PREORDERED_GROUP_DISCOUNT) - price * 0.12;
            case SPONTANEOUS_SINGLE_DISCOUNT:
            case SPONTANEOUS_FAMILY_DISCOUNT:
                return price;
            case SPONTANEOUS_GROUP_DISCOUNT:
                return price - price * 0.1;
        }
        return 0;
    }

    public static String displayString(Discount discount) {
        switch (discount) {
            case PREORDERED_SINGLE_DISCOUNT:
            case PREORDERED_FAMILY_DISCOUNT:
                return "15% off";
            case PREORDERED_GROUP_DISCOUNT:
                return "25% off - group guide not included";
            case SPONTANEOUS_SINGLE_DISCOUNT:
            case SPONTANEOUS_FAMILY_DISCOUNT:
                return "None";
            case SPONTANEOUS_GROUP_DISCOUNT:
                return "10% off - group guide included";
            case PREPAID_PREORDERED_GROUP_DISCOUNT:
                return "37% off - group guide not included";
        }
        return "";
    }
}
