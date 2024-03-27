package CommonClient.controllers;

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