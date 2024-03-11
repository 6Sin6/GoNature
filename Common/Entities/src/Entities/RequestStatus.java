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
}
