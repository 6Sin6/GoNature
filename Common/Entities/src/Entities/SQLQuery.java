package Entities;

/**
 * Enumeration representing various SQL queries.
 */
public enum SQLQuery {

    /**
     * SQL query for inserting data into a database.
     */
    SQL_INSERT(1),

    /**
     * SQL query for updating data in a database.
     */
    SQL_UPDATE(2),

    /**
     * SQL query for deleting data from a database.
     */
    SQL_DELETE(3),

    /**
     * SQL query for selecting data from a database.
     */
    SQL_SELECT(4);

    private int SQLQuery;

    /**
     * Constructs a SQLQuery enum with the given integer value.
     *
     * @param SQLQuery The integer value associated with the SQL query.
     */
    SQLQuery(int SQLQuery) {
        this.SQLQuery = SQLQuery;
    }
}
