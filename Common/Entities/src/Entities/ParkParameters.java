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
}
