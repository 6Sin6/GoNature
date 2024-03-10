package Entities;

public enum Role {
    ROLE_SINGLE_VISITOR(1),
    ROLE_VISITOR_GROUP_GUIDE(2),
    ROLE_PARK_EMPLOYEE(3),
    ROLE_PARK_DEPARTMENT_MGR(4),
    ROLE_PARK_MGR(5),
    ROLE_PARK_SUPPORT_REPRESENTATIVE(6),
    ROLE_ADMINISTRATOR(7);

    private int role;

    Role(int role) {
        this.role = role;
    }
}
