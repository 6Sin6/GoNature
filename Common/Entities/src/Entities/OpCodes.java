package Entities;

public enum OpCodes {
    OP_SYNC_HANDSHAKE(0),
    OP_GET_VISITOR_ORDERS(1), /* Expect: String - VisitorID, Return: ArrayList<Order> */
    OP_GET_VISITOR_GROUP_GROUP_GUIDE_ORDERS(2), /* Same method as above */
    OP_UPDATE_ORDER_DETAILS_BY_ORDERID(3), /* Expect: String - OrderID, String - emailAddress. Returns: boolean indicator*/
    OP_HANDLE_VISITATION_CANCEL_ORDER(4), /* Expect: String - OrderID. Returns: boolean indicator */
    OP_UPDATE_GROUP_ORDER_DETAILS_BY_ORDERID(5), /* Same as HANDLE_VISITATION_EMAIL_CHANGE */
    OP_HANDLE_GROUP_VISITATION_CANCEL_ORDER(6), /* Same as HANDLE_VISITATION_CANCEL_ORDER */
    OP_CREATE_NEW_VISITATION(7), /* Expect: Order, Returns: boolean indicator */
    OP_CREATE_NEW_GROUP_VISITATION(8), /* Same method as above - Perform an upcast to Order!!! Rework group size + group guide ID... */
    OP_INSERT_VISITATION_TO_WAITLIST(9),
    OP_GET_AVAILABLE_SPOTS(10),
    OP_HANDLE_VISITATION_STATUS(11), /* Expect: String - OrderID, OrderStatus status. Returns: boolean indicator*/
    OP_HANDLE_GROUP_VISITATION_STATUS(12), /* Same method as above */
    OP_GET_ORDER_BY_ID(13), /* Expect: String - OrderID, Returns: Map<String, Order> - "isPaid" - payment status, "order" - Order. Order with null data if order is not found. Null if exception was thrown */
    OP_GET_GROUP_VISITATION_BY_ID(14), /* Same method as above */
    OP_GET_PARK_DETAILS_BY_PARK_ID(15), /* Expect: String - ParkID, Returns: Park instance with data if park is found. Park with null data if park is not found. Null if exception was thrown */
    OP_SIGN_IN(16), /* Expect: String - username, String - password. Returns: User with data if success. User with null data if failure. Null if exception was thrown */
    OP_SIGN_IN_ALREADY_LOGGED_IN(17), /* Expect: String - username, null. No return (One way from Server) */
    OP_LOGOUT(18), /* Expect: String - username, null. Returns: */
    OP_QUIT(19), /* Expect: */
    OP_GET_DEPARTMENT_MANAGER_PARKS(20),
    OP_ORDER_ALREADY_EXIST(21),
    OP_GET_USER_ORDERS_BY_USERID_ORDERID(22), /* Expect: String[] - userID 0, orderID 1. Returns: Order */
    OP_ACTIVATE_GROUP_GUIDE(23), /* Expect: String - GroupGuideID. Returns: null if success. String with reason of fail message otherwise. */
    OP_GET_PARK_NAME_BY_PARK_ID(24), /* Expect: String - ParkID. Returns: String - ParkName, null if failed. */
    OP_GET_PARKS_BY_DEPARTMENT(25), /* Expect: String - DepartmentID. Returns: HashMap<String, String> of ID and ParkName, key value respectively . */
    OP_GET_REQUESTS_FROM_PARK_MANAGER(27), /* Expect: String - DepartmentID. Returns: ArrayList<RequestChangingParkParameters> */
    OP_AUTHORIZE_PARK_REQUEST(28), /* Expect: RequestChangingParkParameters. Returns: boolean indicator */
    OP_DECLINE_PARK_REQUEST(29), /* Same method as above */
    OP_SUBMIT_REQUESTS_TO_DEPARTMENT(30), /* Expect: Map<ParkParameters, RequestChangingParkParameters>. Returns: boolean indicator */
    OP_UPDATE_EXIT_TIME_OF_ORDER(31), /* Expect: String OrderID. Returns: In case of success - null. In case of failure - String of error relevant to failure. */
    OP_MARK_ORDER_AS_PAID(32), /* Expect: Order - order. Returns: boolean indicator */
    OP_VIEW_REPORT_BLOB(33), /* Expect: String[] - 0: isDepartmentReport boolean, 1: reportType ["visitations", "cancellations"], 2: month, 3: year, 3: ParkID or DepartmentID. Returns: byte[] - PDF Blob */
    OP_GENERATE_REPORT_BLOB(34), /* Expect: String - reportType ["visitations", "cancellations"]. Returns: boolean indicator */
    OP_NO_AVAILABLE_SPOT(35),
    OP_CHECK_AVAILABLE_SPOT(36),
    OP_CONFIRMATION(37),
    OP_CREATE_SPOTANEOUS_ORDER(38),
    OP_SIGN_IN_VISITOR_GROUP_GUIDE(39),
    OP_MARK_GROUP_GUIDE_ORDER_AS_PAID(40),
    OP_ENTER_VISITORS_TO_PARK(41),
    OP_DB_ERR(999);

    private final int opCodeValue;

    OpCodes(int opCodeValue) {
        this.opCodeValue = opCodeValue;
    }

    public int getOpCodeValue() {
        return this.opCodeValue;
    }
}