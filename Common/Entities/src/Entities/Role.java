package Entities;

public enum Role {
    ROLE_GUEST(0),
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

    public static Role stringToRule(String role) {
        switch (role) {
            case "ROLE_GUEST":
                return ROLE_GUEST;
            case "Visitor":
                return ROLE_SINGLE_VISITOR;
            case "ROLE_VISITOR_GROUP_GUIDE":
                return ROLE_VISITOR_GROUP_GUIDE;
            case "Park Employee":
                return ROLE_PARK_EMPLOYEE;
            case "Department Manager":
                return ROLE_PARK_DEPARTMENT_MGR;
            case "Park Manager":
                return ROLE_PARK_MGR;
            case "Support Representative":
                return ROLE_PARK_SUPPORT_REPRESENTATIVE;
            case "ROLE_ADMINISTRATOR":
                return ROLE_ADMINISTRATOR;
            default:
                return null;
        }
    }
}
