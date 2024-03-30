package CommonClient.controllers;


/**
 * An abstract base class for all controllers in the JavaFX application. This class provides common functionalities
 * and fields that are required by all controllers, such as handling the application's window controller and performing cleanup tasks.
 */
public abstract class BaseController {
    protected ApplicationWindowController applicationWindowController;

    /**
     * This method is used to set the application window controller.
     * All controllers need to have a reference to the SINGLE application window controller.
     *
     * @param applicationWindowController The application window controller.
     */
    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }

    /**
     * This abstract method is called by all controllers when the user loads another page.
     * It is used to clean up the current page fields such as text fields, combo boxes, etc.
     */
    public void cleanup() {
    }
}