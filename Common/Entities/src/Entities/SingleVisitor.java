package Entities;

import java.io.Serializable;

/**
 * Represents a single visitor.
 * A single visitor can only create and manage single orders.
 */
public class SingleVisitor extends User implements Serializable {

    private final String ErrorMsg = "A single Visitor can not create a group order";

    private String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public SingleVisitor(String ID) {
        super(ID, ID, Role.ROLE_SINGLE_VISITOR);
        setID(ID);
    }

}
