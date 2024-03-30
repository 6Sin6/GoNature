package VisitorsControllers;

import CommonClient.controllers.BaseController;

/**
 * This abstract class serves as the base controller for general visitor dashboards.
 * It extends the {@link CommonClient.controllers.BaseController BaseController} class
 * to inherit common functionality related to UI controllers.
 * Subclasses of this class are expected to implement the {@code getUserID} method,
 * which retrieves the unique identifier for the current user.
 * The format of the user ID is dependent on the implementation but should be
 * a non-null, non-empty string that uniquely identifies a user within the system.
 * Implementing subclasses should provide a way of obtaining this identifier.
 */

public abstract class GeneralVisitorDashboard extends BaseController {

    /**
     * Retrieves the unique identifier for the current user.
     * <p>
     * This method is abstract and must be implemented by subclasses to provide
     * a way of obtaining the user's unique ID. The format of the ID is dependent
     * on the implementation but is generally expected to be a non-null, non-empty
     * string that uniquely identifies a user within the system.
     * </p>
     *
     * @return A {@code String} representing the unique identifier of the user.
     */
    public abstract String getUserID();
}
