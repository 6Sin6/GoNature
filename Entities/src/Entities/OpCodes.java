package Entities;

public enum OpCodes {
    GETALLORDERS(0), /* Expect Data instanceof null, returns ArrayList<Order> */
    GETORDERBYID(1), /* Expect Data instanceof Integer, returns Order */
    UPDATEORDER(2), /* Expect Data instanceof Order, returns Boolean */
    DBERROR(9999); /* Returns Data instanceof String */

    private final int opCodeValue;

    OpCodes(int opCodeValue) {
        this.opCodeValue = opCodeValue;
    }

    public int getOpCodeValue() {
        return this.opCodeValue;
    }
}