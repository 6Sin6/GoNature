package DataBase;


import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Provides methods for performing actions on the database, with integration
 * to update or log through the ServerPortFrameController.
 */
public class DBController {
    private final Connection conn;

    public DBController(Connection conn) {
        this.conn = conn;
    }


    /**
     * Inserts a new record into the specified table and logs the action.
     *
     * @param tableName The name of the table where the record will be inserted.
     * @param values    The values to insert into the table. Assumes a prepared statement format.
     * @return true if the insert operation was successful, false otherwise.
     */
    public boolean insertRecord(String tableName, String columns, String... values) throws SQLException {
        if (tableName.isEmpty()) {
            throw new SQLException("Table name cannot be empty");
        }
        if (values.length == 0) {
            throw new SQLException("No values provided");
        }
        String sql = "INSERT INTO " + tableName + "(" + columns + ")" + " VALUES (" + String.join(", ", values) + ")";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException("Insert into " + tableName + " failed: " + e.getMessage());
        }
        return false;
    }


    /**
     * Inserts a new record into the specified table and logs the action.
     *
     * @param tableName The name of the table where the record will be inserted.
     * @param columns   The columns to insert into the table. Assumes a prepared statement format.
     * @param blob      The blob to insert into the table.
     * @param values    The values to insert into the table. Assumes a prepared statement format.
     * @return true if the insert operation was successful, false otherwise.
     */
    public boolean insertBlobRecord(String tableName, String columns, Blob blob, String... values) throws SQLException {
        if (tableName.isEmpty()) {
            throw new SQLException("Table name cannot be empty");
        }
        // Generate parameter placeholders for prepared statement
        String[] placeholders = new String[values.length];
        Arrays.fill(placeholders, "?");
        String valuesPlaceholder = String.join(", ", placeholders);

        String sql = "INSERT INTO " + tableName + "(" + columns + ")" + " VALUES (" + valuesPlaceholder + ", ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set other parameters
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }
            // Set Blob parameter
            pstmt.setBlob(values.length + 1, blob);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException("Insert into " + tableName + " failed: " + e.getMessage());
        }
        return false;
    }



    /**
     * Updates records in the specified table and logs the action.
     *
     * @param tableName   The name of the table to update.
     * @param setClause   The SET clause specifying the columns to update and their new values.
     * @param whereClause The WHERE clause to specify which records to update.
     * @return true if the update operation was successful, false otherwise.
     */
    public boolean updateRecord(String tableName, String setClause, String whereClause) throws SQLException {
        if (tableName.isEmpty()) {
            throw new SQLException("Table name cannot be empty");
        }
        if (Objects.equals(whereClause, "")) {
            throw new SQLException("Where clause is empty. Update without where-clause is not allowed.");
        }
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Update in " + tableName + " failed: " + e.getMessage());
        }
    }

    /**
     * Updates an existing record in the specified table with the provided blob data and where clause, and logs the action.
     *
     * @param tableName  The name of the table where the record will be updated.
     * @param blobColumn The name of the column containing the blob data.
     * @param blob       The new blob data to update in the table.
     * @param whereClause The WHERE clause to specify which record(s) to update. Assumes a prepared statement format.
     * @return true if the update operation was successful, false otherwise.
     */
    public boolean updateBlobRecord(String tableName, String blobColumn, Blob blob, String whereClause) throws SQLException {
        if (tableName.isEmpty() || blobColumn.isEmpty() || whereClause.isEmpty()) {
            throw new SQLException("Table name, blob column name, and where clause cannot be empty");
        }

        String sql = "UPDATE " + tableName + " SET " + blobColumn + " = ? WHERE " + whereClause;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set Blob parameter
            pstmt.setBlob(1, blob);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException("Update " + tableName + " failed: " + e.getMessage());
        }
        return false;
    }



    /**
     * Deletes records from the specified table and logs the action.
     *
     * @param tableName   The name of the table from which to delete records.
     * @param whereClause The WHERE clause to specify which records to delete.
     * @return true if the delete operation was successful, false otherwise.
     */
    public boolean deleteRecord(String tableName, String whereClause) throws SQLException {
        if (tableName.isEmpty()) {
            throw new SQLException("Table name cannot be empty");
        }
        String sql = "DELETE FROM " + tableName + " WHERE " + whereClause;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException("Delete from " + tableName + " failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Selects records with specified fields from the specified table and logs the action.
     *
     * @param tableName   The name of the table from which to select records.
     * @param whereClause The WHERE clause to specify which records to select.
     * @param fields      The fields to select from the table.
     * @return A ResultSet containing the selected records, or null if an error occurs.
     */
    public ResultSet selectRecordsFields(String tableName, String whereClause, String... fields) throws SQLException {
        String sql = "SELECT " + String.join(",", fields) + " FROM " + tableName + (whereClause.isEmpty() ? "" : " WHERE " + whereClause);
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException("Select from " + tableName + " failed: " + e.getMessage());
        }
    }

    /**
     * Selects records from the specified table and logs the action.
     *
     * @param tableName   The name of the table from which to select records.
     * @param whereClause The WHERE clause to specify which records to select.
     * @return A ResultSet containing the selected records, or null if an error occurs.
     */
    public ResultSet selectRecords(String tableName, String whereClause) throws SQLException {
        String sql = "SELECT * FROM " + tableName + (whereClause.isEmpty() ? "" : " WHERE " + whereClause);
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException("Select from " + tableName + " failed: " + e.getMessage());
        }
    }
}
