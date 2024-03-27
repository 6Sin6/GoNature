package Entities;

import java.io.Serializable;

/**
 * Represents a request to change park parameters.
 */
public class RequestChangingParkParameters implements Serializable {

    private Park park; // The park associated with the request
    private ParkParameters parameter; // The parameter being requested to change
    private Double requestedValue; // The new value requested for the parameter
    private RequestStatus status; // The status of the request

    /**
     * Constructs a RequestChangingParkParameters object with the specified parameters.
     *
     * @param park           The park associated with the request.
     * @param parameter      The parameter being requested to change.
     * @param requestedValue The new value requested for the parameter.
     * @param status         The status of the request.
     */
    public RequestChangingParkParameters(Park park, ParkParameters parameter, Double requestedValue, RequestStatus status) {
        this.park = park;
        this.parameter = parameter;
        this.requestedValue = requestedValue;
        this.status = status;
    }

    /**
     * Retrieves the park associated with the request.
     *
     * @return The park associated with the request.
     */
    public Park getPark() {
        return park;
    }

    /**
     * Sets the park associated with the request.
     *
     * @param park The park associated with the request.
     */
    public void setPark(Park park) {
        this.park = park;
    }

    /**
     * Retrieves the parameter being requested to change.
     *
     * @return The parameter being requested to change.
     */
    public ParkParameters getParameter() {
        return parameter;
    }

    /**
     * Sets the parameter being requested to change.
     *
     * @param parameter The parameter being requested to change.
     */
    public void setParameter(ParkParameters parameter) {
        this.parameter = parameter;
    }

    /**
     * Retrieves the new value requested for the parameter.
     *
     * @return The new value requested for the parameter.
     */
    public Double getRequestedValue() {
        return requestedValue;
    }

    /**
     * Sets the new value requested for the parameter.
     *
     * @param requestedValue The new value requested for the parameter.
     */
    public void setRequestedValue(Double requestedValue) {
        this.requestedValue = requestedValue;
    }

    /**
     * Retrieves the status of the request.
     *
     * @return The status of the request.
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the request.
     *
     * @param status The status of the request.
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * Constructs a RequestChangingParkParameters object with the specified parameters,
     * setting the status of the request to REQUEST_PENDING by default.
     *
     * @param park           The park associated with the request.
     * @param parameter      The parameter being requested to change.
     * @param requestedValue The new value requested for the parameter.
     */
    public RequestChangingParkParameters(Park park, ParkParameters parameter, Double requestedValue) {
        this.park = park;
        this.parameter = parameter;
        this.requestedValue = requestedValue;
        this.status = RequestStatus.REQUEST_PENDING;
    }

    /**
     * Constructs a String in a format that represents the request to change park parameter,
     * Used in table view, for the requests table column.
     *
     * @return A string that represents the request to change park parameter.
     */
    @Override
    public String toString() {
        return "Change " + parameter + " to " + requestedValue + " in " + park.getParkName() + " - Status: " + status;
    }

    /**
     * Retrieves the park manager's username associated with the request.
     *
     * @return The park manager's username associated with the request.
     */
    public String getRequesterName() {
        return park.getParkManager().getUsername();
    }
}
