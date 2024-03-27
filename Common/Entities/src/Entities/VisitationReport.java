package Entities;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.io.*;
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
     * @param statName    The name of the statistic.
     * @param reportData   The data associated with the report.
     * The data is stored as a HashMap String, ResultSet .
     */
    public VisitationReport(Integer departmentID, String statName, ResultSet reportData) throws DocumentException, IOException
    {
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
     * @param statName The name of the statistic.
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
     * This method is intended to create a PDF Blob for the VisitationReport.
     * Creates a PDF file based on the data in the specified ResultSet.
     *
     * @return A Blob object representing the PDF file.
     * @throws DocumentException If there is an error while creating the PDF document.
     * @throws SQLException If there is an error while converting the PDF to a Blob.
     * @throws IOException If there is an error while handling the PDF file.
     */
    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException
    {
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
     * Creates a chart with JFreeChart based on the data in the specified ResultSet.
     *
     * @return The JFreeChart object representing the chart.
     */
    protected JFreeChart createBarChart() throws SQLException
    {
        // Definitions
        double maxTimeSpent = 0;
        // Initialize dataset for the pie chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Get current date
        LocalDate currentDate = LocalDate.now();
        // Initialize colors for the bars
        Color groupOrdersColor = new Color(12, 36, 58);
        Color singleFamilyOrdersColor = new Color(188, 33, 33);
        // Initialize titles for Axis and chart.
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

        return super.createBarChart(dataset, maxTimeSpent, title,
                xAxisTitle, yAxisTitle, groupOrdersColor, singleFamilyOrdersColor);
    }




    /**
     * Creates a pie chart with JFreeChart based on the data in the specified ResultSet.
     *
     * @return The JFreeChart object representing the pie chart.
     */
    protected JFreeChart createPieChart() throws SQLException
    {
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
     * Creates a table with the data in the specified ResultSet.
     * @param orderType The order type to display in the table.
     * @return The PdfPTable object representing the table.
     */
    private PdfPTable createTable(int orderType) throws SQLException
    {
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
     * @param orderType The order type to display in the table.
     * @return The PdfPTable object representing the table.
     */
    private PdfPTable createTable(OrderType orderType) throws SQLException
    {
        return this.createTable(orderType.getOrderType());
    }





        /**
         * Retrieves the average time spent for the specified date and order type.
         *
         * @param date The date to retrieve the time spent for.
         * @param orderTypeSuffix The suffix for the order type.
         * @return The average time spent for the specified date and order type.
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
     * Retrieves the total time spent for the specified order type.
     *
     * @param orderTypeSuffix The suffix for the order type.
     * @return The total time spent for the specified order type.
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
