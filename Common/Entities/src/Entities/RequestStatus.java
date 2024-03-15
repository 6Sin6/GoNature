package Entities;

/**
 * Enumeration representing the status of a request.
 */
public enum RequestStatus {

    /**
     * Status indicating that the request is pending.
     */
    REQUEST_PENDING(1),

    /**
     * Status indicating that the request has been accepted.
     */
    REQUEST_ACCEPTED(2),

    /**
     * Status indicating that the request has been declined.
     */
    REQUEST_DECLINED(3);

    private int RequestStatus;

    /**
     * Constructs a RequestStatus enum with the given integer value.
     *
     * @param RequestStatus The integer value associated with the request status.
     */
    RequestStatus(int RequestStatus) {
        this.RequestStatus = RequestStatus;
    }

    /**
     * Retrieves the integer value associated with the status.
     *
     * @return The integer value associated with the status.
     */
    public int getRequestStatus() {
        return RequestStatus;
    }

    /**
     * Retrieves the String representation value associated with the status.
     *
     * @return The String representation value associated with the status.
     */
    public static String statusToString(RequestStatus status) {
        switch (status) {
            case REQUEST_PENDING:
                return "Pending Authorization";
            case REQUEST_ACCEPTED:
                return "Authorized";
            case REQUEST_DECLINED:
                return "Unauthorized";
            default:
                return null;
        }
    }
}
