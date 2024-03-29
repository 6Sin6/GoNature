package GoNatureServer.ServerEntities;

import Entities.OrderType;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static CommonUtils.CommonUtils.parseVisitTime;

/**
 * Represents a visitation report for a specific department.
 */
public class VisitationReport extends DepartmentReport implements Serializable {
    private HashMap<String, ResultSet> reportData;


    /**
     * Constructs a new VisitationReport object with the specified date and department.
     *
     * @param departmentID The ID of the department associated with the report.
     * @param statName     The name of the statistic.
     * @param reportData   The data associated with the report.
     *                     The data is stored as a HashMap String, ResultSet .
     */
    public VisitationReport(Integer departmentID, String statName, ResultSet reportData) throws DocumentException, IOException {
        super(departmentID);
        this.reportData = new HashMap<>();
        this.reportData.put(statName, reportData);
    }


    /**
     * Retrieves the data associated with the report.
     *
     * @return The data associated with the report.
     */
    public HashMap<String, ResultSet> getReportData() {
        return reportData;
    }


    /**
     * Sets the data associated with the report.
     *
     * @param reportData The data to set as a HashMap String, ResultSet.
     */
    public void setReportData(HashMap<String, ResultSet> reportData) {
        this.reportData = reportData;
    }


    /**
     * Adds a ResultSet to the report data.
     *
     * @param statName   The name of the statistic.
     * @param reportData The ResultSet to add.
     */
    public void addReportData(String statName, ResultSet reportData) {
        this.reportData.put(statName, reportData);
    }


    /**
     * Removes a ResultSet from the report data.
     *
     * @param statName The statistic to remove, along with its data.
     */
    public void removeReportData(String statName) {
        this.reportData.remove(statName);
    }


    /**
     * This method is responsible for creating a PDF Blob for the VisitationReport.
     * It first creates a PDF document with a title that includes the department ID.
     * Then, it adds a bar chart (grouped column chart) and a pie chart to the document, each on a new page.
     * After that, it creates a table container with left and right columns for tables.
     * It then creates and adds a table for single orders and a table for group orders to the table container.
     * The table container is then added to the document.
     * Finally, the document is closed and converted to a Blob, which is returned.
     *
     * @return A Blob object representing the PDF file.
     * @throws DocumentException If there is an error while creating the PDF document.
     * @throws SQLException      If there is an error while converting the PDF to a Blob.
     * @throws IOException       If there is an error while handling the PDF file.
     */
    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        String title_Document = "Visitations Report - Department: " + super.getDepartmentID();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create PDF document
        Document document = super.createPDFDocument(title_Document, outputStream);

        // Add Bar Chart (Grouped Column Chart)
        super.addJFreeChartToDocument(document, this.createBarChart(), 1300, 600);

        document.newPage();


        // Add Pie Chart
        super.addJFreeChartToDocument(document, this.createPieChart(), 700, 700);

        document.newPage();


        // Create left and right columns for tables
        float[] columnWidths = {0.45f, 0.15f, 0.45f};
        PdfPTable tablesContainer = new PdfPTable(columnWidths);
        tablesContainer.setWidthPercentage(100);


        // Single Orders Table
        PdfPTable tableSingle = this.createTable(OrderType.ORD_TYPE_SINGLE);
        tableSingle.setSpacingBefore(30);
        PdfPCell singleCell = super.createSingleCellWithTitleAndTable(tableSingle, "Single Orders");
        tablesContainer.addCell(singleCell);


        // Title Table
        PdfPCell titleCell = createSingleCellWithTitle("Entrance Statistics");
        tablesContainer.addCell(titleCell);


        // Group Orders Table
        PdfPTable tableGroup = this.createTable(OrderType.ORD_TYPE_GROUP);
        tableSingle.setSpacingBefore(30);
        PdfPCell groupCell = super.createSingleCellWithTitleAndTable(tableGroup, "Group Orders");
        tablesContainer.addCell(groupCell);

        document.add(tablesContainer);


        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }


    /**
     * Creates a bar chart using JFreeChart based on the average time spent by order type for each day of the current month.
     * The method first initializes a dataset and sets the colors for the bars in the chart.
     * It then iterates through each day of the current month, retrieves the average time spent for group orders and single/family-sized orders for that day, and adds the data to the dataset.
     * The maximum time spent is also tracked to set the range of the y-axis in the chart.
     * Finally, it calls the createGroupedColumnChart method to create the chart with the specified dataset, maximum time spent, titles, and colors.
     *
     * @return A JFreeChart object representing the bar chart.
     * @throws SQLException If an error occurs while retrieving the time spent data.
     */
    protected JFreeChart createBarChart() throws SQLException {
        double maxTimeSpent = 0;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LocalDate currentDate = LocalDate.now();

        Color groupOrdersColor = new Color(12, 36, 58);
        Color singleFamilyOrdersColor = new Color(188, 33, 33);

        String title = "Average Time Spent by Order Type";
        String xAxisTitle = "Date";
        String yAxisTitle = "Average Time Spent (Hours)";

        // Iterate through the days of the current month
        for (int day = 1; day <= 31; day++) {
            String date = currentDate.withDayOfMonth(day).toString();

            // Retrieve data for the current date
            double averageTimeSpentGroup = getTimeSpentForDate(date, "_2");
            double averageTimeSpentSingleFamily = getTimeSpentForDate(date, "_1");

            // Add data to dataset
            dataset.addValue(averageTimeSpentGroup, "Group Orders", String.valueOf(day));
            dataset.addValue(averageTimeSpentSingleFamily, "Single/Family-Sized Orders", String.valueOf(day));

            // Update max time spent
            maxTimeSpent = Math.max(maxTimeSpent, Math.max(averageTimeSpentGroup, averageTimeSpentSingleFamily));
        }

        return super.createGroupedColumnChart(dataset, maxTimeSpent, title,
                xAxisTitle, yAxisTitle, groupOrdersColor, singleFamilyOrdersColor);
    }


    /**
     * This method creates a pie chart using JFreeChart based on the total time spent by order type for the entire month.
     * It first initializes a dataset for the pie chart and sets the title of the chart.
     * Then, it retrieves the total time spent for group orders and single/family-sized orders for the entire month.
     * The retrieved data is then added to the dataset.
     * Finally, it calls the createPieChart method from the superclass to create the pie chart with the specified dataset and title.
     *
     * @return A JFreeChart object representing the pie chart.
     * @throws SQLException If an error occurs while retrieving the total time spent data.
     */
    protected JFreeChart createPieChart() throws SQLException {
        // Initialize dataset for the pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        // Initialize title
        String title = "Total Time Spent by Order Type";

        // Get total time spent for each order type for the entire month
        double totalGroupTimeSpent = getTotalTimeSpent("_2");
        double totalSingleFamilyTimeSpent = getTotalTimeSpent("_1");

        // Add data to the dataset
        dataset.setValue("Group Orders", totalGroupTimeSpent);
        dataset.setValue("Single/Family-Sized Orders", totalSingleFamilyTimeSpent);

        return super.createPieChart(dataset, title);
    }


    /**
     * This method creates a table with data from a specified ResultSet.
     * The table is created with columns for "Park ID", "Park Name", "Entrance Time", "Reservation Time", and "Time Spent".
     * The method then iterates through the ResultSet, adding a row to the table for each record that matches the specified order type.
     * Each row contains the park ID, park name, entrance time, reservation time, and time spent for the corresponding record.
     * The method uses the createTable and createCenterCell methods from the superclass to create the table and cells, respectively.
     * The method also uses the parseVisitTime method to format the time spent.
     *
     * @param orderType The order type to display in the table. It should be an integer representing the order type.
     * @return A PdfPTable object representing the table. Each row in the table corresponds to a record in the ResultSet that matches the specified order type.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private PdfPTable createTable(int orderType) throws SQLException {
        // Columns of table
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Park ID");
        columns.add("Park Name");
        columns.add("Entrance Time");
        columns.add("Reservation Time");
        columns.add("Time Spent");

        // Add table
        PdfPTable table = super.createTable(columns, 100);

        // Collecting data for table
        ResultSet entranceData = this.reportData.get("entrance");
        entranceData.beforeFirst();
        while (entranceData.next()) {
            int orderTypeRes = entranceData.getInt("OrderType");
            if (orderTypeRes != orderType) {
                continue;
            }
            table.addCell(super.createCenterCell(String.valueOf(entranceData.getInt("ParkID"))));
            table.addCell(super.createCenterCell(entranceData.getString("ParkName")));
            table.addCell(super.createCenterCell(String.valueOf(entranceData.getTimestamp("EntranceTime"))));
            table.addCell(super.createCenterCell(String.valueOf(entranceData.getTimestamp("ReservationTime"))));
            table.addCell(super.createCenterCell(parseVisitTime(entranceData.getTimestamp("TimeSpent"))));
        }

        return table;
    }


    /**
     * Creates a table with the data in the specified ResultSet.
     *
     * @param orderType The order type to display in the table.
     * @return The PdfPTable object representing the table.
     */
    private PdfPTable createTable(OrderType orderType) throws SQLException {
        return this.createTable(orderType.getOrderType());
    }


    /**
     * This method retrieves the average time spent for a specified date and order type from the report data.
     * It first retrieves the ResultSet for "timespent" from the report data.
     * Then, it iterates through the ResultSet, checking each record's visitation date against the specified date.
     * If a match is found, it retrieves the average time spent for the specified order type from the record.
     * If the retrieved value is null, it is set to 0.0.
     * The method then converts the average time spent from minutes to hours and returns it.
     * If no match is found after iterating through the entire ResultSet, the method returns 0.0.
     *
     * @param date            The date to retrieve the time spent for. It should be a string representing the date.
     * @param orderTypeSuffix The suffix for the order type. It should be a string representing the suffix.
     * @return The average time spent for the specified date and order type, converted from minutes to hours.
     *         If no data is found for the specified date, the method returns 0.0.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private double getTimeSpentForDate(String date, String orderTypeSuffix) throws SQLException {
        ResultSet timeSpentData = this.reportData.get("timespent");
        timeSpentData.beforeFirst();
        while (timeSpentData.next()) {
            String visitationDate = timeSpentData.getDate("VisitationDate").toString();
            if (visitationDate.equals(date)) {
                double averageTimeSpent = timeSpentData.getDouble("AverageTimeSpent" + orderTypeSuffix);
                if (timeSpentData.wasNull()) {
                    averageTimeSpent = 0.0; // Set to 0 if the value was null
                }
                // Convert from minutes to hours
                return averageTimeSpent / 60.0;
            }
        }
        return 0.0; // Return 0 if data for the date is not found
    }


    /**
     * This method retrieves the total time spent for a specified order type from the report data.
     * It first retrieves the ResultSet for "timespent" from the report data.
     * Then, it iterates through the ResultSet, retrieving the average time spent for the specified order type from each record.
     * If the retrieved value is not null, it is added to the total time spent.
     * The method then converts the total time spent from minutes to hours and returns it.
     *
     * @param orderTypeSuffix The suffix for the order type. It should be a string representing the suffix.
     * @return The total time spent for the specified order type, converted from minutes to hours.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private double getTotalTimeSpent(String orderTypeSuffix) throws SQLException {
        double total = 0.0;
        ResultSet timeSpentData = this.reportData.get("timespent");
        timeSpentData.beforeFirst();
        while (timeSpentData.next()) {
            double averageTimeSpent = timeSpentData.getDouble("AverageTimeSpent" + orderTypeSuffix);
            if (!timeSpentData.wasNull()) {
                total += averageTimeSpent;
            }
        }
        return total / 60.0; // Convert total from minutes to hours
    }
}
