package Entities;

/**
 * The OpCodes enum represents the different operation codes that can be used in the system.
 * Each enum constant represents a specific operation and is associated with a unique integer value.
 * The enum also provides a method to get the integer value associated with the enum constant.
 */
public enum OpCodes {
    /**
     * Represents the operation code for synchronizing handshake.
     */
    OP_SYNC_HANDSHAKE(0),

    /**
     * Represents the operation code for getting visitor orders.
     * Expects a String representing the VisitorID.
     * Returns an ArrayList of Order objects.
     */
    OP_GET_VISITOR_ORDERS(1), /* Expect: String - VisitorID, Return: ArrayList<Order> */

    /**
     * Represents the operation code for getting visitor group guide orders.
     * Uses the same method as OP_GET_VISITOR_ORDERS.
     */
    OP_GET_VISITOR_GROUP_GROUP_GUIDE_ORDERS(2), /* Same method as above */

    /**
     * Represents the operation code for updating order details by order ID.
     * Expects a String representing the OrderID and a String representing the email address.
     * Returns a boolean indicator.
     */
    OP_UPDATE_ORDER_DETAILS_BY_ORDERID(3), /* Expect: String - OrderID, String - emailAddress. Returns: boolean indicator*/

    /**
     * Represents the operation code for handling visitation cancel order.
     * Expects a String representing the OrderID.
     * Returns a boolean indicator.
     */
    OP_HANDLE_VISITATION_CANCEL_ORDER(4), /* Expect: String - OrderID. Returns: boolean indicator */

    /**
     * UNUSED
     * Represents the operation code for updating group order details by order ID.
     * Uses the same method as OP_UPDATE_ORDER_DETAILS_BY_ORDERID.
     */
    OP_UPDATE_GROUP_ORDER_DETAILS_BY_ORDERID(5), /* Same as HANDLE_VISITATION_EMAIL_CHANGE */

    /**
     * UNUSED
     * Represents the operation code for handling group visitation cancel order.
     * Uses the same method as OP_HANDLE_VISITATION_CANCEL_ORDER.
     */
    OP_HANDLE_GROUP_VISITATION_CANCEL_ORDER(6), /* Same as HANDLE_VISITATION_CANCEL_ORDER */

    /**
     * Represents the operation code for creating a new visitation.
     * Expects an Order object.
     * Returns a boolean indicator.
     */
    OP_CREATE_NEW_VISITATION(7), /* Expect: Order, Returns: boolean indicator */

    /**
     * UNUSED
     * Represents the operation code for creating a new group visitation.
     * Uses the same method as OP_CREATE_NEW_VISITATION.
     * Performs an upcast to Order and reworks group size and group guide ID.
     */
    OP_CREATE_NEW_GROUP_VISITATION(8), /* Same method as above - Perform an upcast to Order!!! Rework group size + group guide ID... */

    /**
     * Represents the operation code for inserting visitation to waitlist.
     */
    OP_INSERT_VISITATION_TO_WAITLIST(9),

    /**
     * Represents the operation code for getting available spots.
     */
    OP_GET_AVAILABLE_SPOTS(10),

    /**
     * UNUSED
     * Represents the operation code for handling visitation status.
     * Expects a String representing the OrderID and an OrderStatus object.
     * Returns a boolean indicator.
     */
    OP_HANDLE_VISITATION_STATUS(11), /* Expect: String - OrderID, OrderStatus status. Returns: boolean indicator*/

    /**
     * UNUSED
     * Represents the operation code for handling group visitation status.
     * Uses the same method as OP_HANDLE_VISITATION_STATUS.
     */
    OP_HANDLE_GROUP_VISITATION_STATUS(12), /* Same method as above */

    /**
     * Represents the operation code for getting order by ID.
     * Expects a String representing the OrderID.
     * Returns a Map with "isPaid" as payment status and "order" as Order.
     * Returns an Order with null data if order is not found.
     * Returns null if an exception was thrown.
     */
    OP_GET_ORDER_BY_ID(13), /* Expect: String - OrderID, Returns: Map<String, Order> - "isPaid" - payment status, "order" - Order. Order with null data if order is not found. Null if exception was thrown */

    /**
     * UNUSED
     * Represents the operation code for getting group visitation by ID.
     * Uses the same method as OP_GET_ORDER_BY_ID.
     */
    OP_GET_GROUP_VISITATION_BY_ID(14), /* Same method as above */

    /**
     * Represents the operation code for getting park details by park ID.
     * Expects a String representing the ParkID.
     * Returns a Park instance with data if park is found.
     * Returns a Park with null data if park is not found.
     * Returns null if an exception was thrown.
     */
    OP_GET_PARK_DETAILS_BY_PARK_ID(15), /* Expect: String - ParkID, Returns: Park instance with data if park is found. Park with null data if park is not found. Null if exception was thrown */

    /**
     * Represents the operation code for signing in.
     * Expects a String representing the username and a String representing the password.
     * Returns a User with data if success.
     * Returns a User with null data and role guest if failure.
     * Returns null if an exception was thrown.
     */
    OP_SIGN_IN(16), /* Expect: String - username, String - password. Returns: User with data if success. User with null data if failure. Null if exception was thrown */

    /**
     * Represents the operation code for signing in when already logged in.
     * Expects a String representing the username and null.
     * Does not return anything (One way from Server).
     */
    OP_SIGN_IN_ALREADY_LOGGED_IN(17), /* Expect: String - username, null. No return (One way from Server) */

    /**
     * Represents the operation code for logging out.
     * Expects a String representing the username and null.
     */
    OP_LOGOUT(18), /* Expect: String - username, null. Returns: */

    /**
     * Represents the operation code for quitting.
     */
    OP_QUIT(19),

    /**
     * Represents the operation code for getting department manager parks.
     */
    OP_GET_DEPARTMENT_MANAGER_PARKS(20),

    /**
     * Represents the operation code for checking if an order already exists.
     */
    OP_ORDER_ALREADY_EXIST(21),

    /**
     * Represents the operation code for getting user orders by user ID and order ID.
     * Expects a String array with userID at index 0 and orderID at index 1.
     * Returns an Order object.
     */
    OP_GET_USER_ORDERS_BY_USERID_ORDERID(22), /* Expect: String[] - userID 0, orderID 1. Returns: Order */
    /**
     * Represents the operation code for activating a group guide.
     * Expects a String representing the GroupGuideID.
     * Returns null if success.
     * Returns a String with reason of fail message otherwise.
     */
    OP_ACTIVATE_GROUP_GUIDE(23),

    /**
     * Represents the operation code for getting park name by park ID.
     * Expects a String representing the ParkID.
     * Returns a String representing the ParkName, null if failed.
     */
    OP_GET_PARK_NAME_BY_PARK_ID(24),

    /**
     * Represents the operation code for getting parks by department.
     * Expects a String representing the DepartmentID.
     * Returns a HashMap of ID and ParkName, key value respectively.
     */
    OP_GET_PARKS_BY_DEPARTMENT(25),

    /**
     * Represents the operation code for getting requests from park manager.
     * Expects a String representing the DepartmentID.
     * Returns an ArrayList of RequestChangingParkParameters.
     */
    OP_GET_REQUESTS_FROM_PARK_MANAGER(27),

    /**
     * Represents the operation code for authorizing park request.
     * Expects a RequestChangingParkParameters object.
     * Returns a boolean indicator.
     */
    OP_AUTHORIZE_PARK_REQUEST(28),

    /**
     * Represents the operation code for declining park request.
     * Uses the same method as OP_AUTHORIZE_PARK_REQUEST.
     */
    OP_DECLINE_PARK_REQUEST(29),

    /**
     * Represents the operation code for submitting requests to department.
     * Expects a Map of ParkParameters and RequestChangingParkParameters.
     * Returns a boolean indicator.
     */
    OP_SUBMIT_REQUESTS_TO_DEPARTMENT(30),

    /**
     * Represents the operation code for updating exit time of order.
     * Expects a String representing the OrderID.
     * Returns null in case of success.
     * Returns a String of error relevant to failure in case of failure.
     */
    OP_UPDATE_EXIT_TIME_OF_ORDER(31),

    /**
     * Represents the operation code for marking an order as paid.
     * Expects an Order object.
     * Returns a boolean indicator.
     */
    OP_MARK_ORDER_AS_PAID(32),

    /**
     * Represents the operation code for viewing report blob.
     * Expects a String array with isDepartmentReport boolean at index 0, reportType ["visitations", "cancellations"] at index 1, month at index 2, year at index 3, and ParkID or DepartmentID at index 4.
     * Returns a byte array representing the PDF Blob.
     */
    OP_VIEW_REPORT_BLOB(33),

    /**
     * Represents the operation code for generating report blob.
     * Expects a String representing the reportType ["visitations", "cancellations"].
     * Returns a boolean indicator.
     */
    OP_GENERATE_REPORT_BLOB(34),

    /**
     * Represents the operation code for checking if there is no available spot.
     */
    OP_NO_AVAILABLE_SPOT(35),

    /**
     * Represents the operation code for checking available spot.
     */
    OP_CHECK_AVAILABLE_SPOT(36),

    /**
     * Represents the operation code for confirmation.
     */
    OP_CONFIRMATION(37),

    /**
     * Represents the operation code for creating spontaneous order.
     */
    OP_CREATE_SPOTANEOUS_ORDER(38),

    /**
     * Represents the operation code for signing in visitor group guide.
     */
    OP_SIGN_IN_VISITOR_GROUP_GUIDE(39),

    /**
     * Represents the operation code for marking group guide order as paid.
     */
    OP_MARK_GROUP_GUIDE_ORDER_AS_PAID(40),

    /**
     * Represents the operation code for entering visitors to park.
     */
    OP_ENTER_VISITORS_TO_PARK(41),

    /**
     * Represents the operation code for database error.
     */
    OP_DB_ERR(999);

    /**
     * The unique integer value associated with the operation code.
     */
    private final int opCodeValue;

    /**
     * Constructs an OpCodes enum with the specified operation code value.
     *
     * @param opCodeValue The value representing the operation code.
     */
    OpCodes(int opCodeValue) {
        this.opCodeValue = opCodeValue;
    }

    /**
     * Returns the integer value associated with the enum constant.
     *
     * @return The integer value associated with the enum constant.
     */
    public int getOpCodeValue() {
        return this.opCodeValue;
    }
}