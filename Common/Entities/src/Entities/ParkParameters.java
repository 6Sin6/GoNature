package Entities;

/**
 * Enumeration representing various parameters related to a park.
 */
public enum ParkParameters {

    /**
     * Parameter representing the maximum capacity of visitors allowed in the park.
     */
    PARK_GAP_VISITORS_CAPACITY(1),

    /**
     * Parameter representing the overall capacity of the park.
     */
    PARK_CAPACITY(2),

    /**
     * Parameter representing the default maximum allowed duration for visitation in the park.
     */
    PARK_DEFAULT_MAX_VISITATION_LONGEVITY(3);

    private int ParkParameters;

    /**
     * Constructs a ParkParameters enum with the given integer value.
     *
     * @param ParkParameters The integer value associated with the parameter.
     */
    ParkParameters(int ParkParameters) {
        this.ParkParameters = ParkParameters;
    }

    /**
     * Retrieves the integer value associated with the parameter.
     *
     * @return The integer value associated with the parameter.
     */
    public int getParameterVal() {
        return ParkParameters;
    }

    /**
     * Retrieves the String representation value associated with the parameter in MYSQL table parks.
     *
     * @return The String representation value associated with the parameter in MYSQL table parks.
     */
    public String getColumnName() {
        switch (this) {
            case PARK_CAPACITY:
                return "Capacity";
            case PARK_DEFAULT_MAX_VISITATION_LONGEVITY:
                return "DefaultVisitationTime";
            case PARK_GAP_VISITORS_CAPACITY:
                return "GapVisitorsCapacity";
            default:
                return null;
        }
    }

    /**
     * Retrieves the String representation value associated with the parameter.
     * @param parameter The parameter to convert to a String.
     * @return The String representation value associated with the parameter.
     */
    public static String parameterToString(ParkParameters parameter) {
        switch (parameter) {
            case PARK_CAPACITY:
                return "Park Capacity";
            case PARK_DEFAULT_MAX_VISITATION_LONGEVITY:
                return "Park Max Visitation Longevity";
            case PARK_GAP_VISITORS_CAPACITY:
                return "Park Difference Between Current Visitors and Capacity";
            default:
                return null;
        }
    }
}
