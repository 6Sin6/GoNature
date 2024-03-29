package Entities;

import java.io.Serializable;

/**
 * Represents a single visitor.
 * A single visitor can only create and manage single orders.
 */
public class SingleVisitor extends User implements Serializable {
    /**
     * Error message to be displayed when a single visitor tries to create a group order.
     */
    private final String ErrorMsg = "A single Visitor can not create a group order";

    /**
     * The ID of the single visitor.
     */
    private String ID;

    /**
     * Returns the ID of the single visitor.
     *
     * @return The ID of the single visitor.
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the ID of the single visitor.
     *
     * @param ID The ID to set.
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Constructs a SingleVisitor object with the specified ID.
     * The ID is also used as the username and the role is set to ROLE_SINGLE_VISITOR.
     *
     * @param ID The ID of the single visitor.
     */
    public SingleVisitor(String ID) {
        super(ID, ID, Role.ROLE_SINGLE_VISITOR);
        setID(ID);
    }
}
