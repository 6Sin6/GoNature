package Entities;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * The NumOfVisitorsReport class represents a report detailing the number of visitors to a park.
 * It extends the ParkReport class and contains information about group and single orders.
 */
public class NumOfVisitorsReport extends ParkReport implements Serializable
{
    private final ResultSet reportData;
    private final int[][] amountPerDay; // total per day of each order type
    private final int[] totalPerType; // total of visitors per order type.




    /**
     * Constructs a new NumOfVisitorsReport with the specified park ID and report data.
     *
     * @param parkID The ID of the park.
     * @param resultSet The report data.
     */
    public NumOfVisitorsReport(Integer parkID, ResultSet resultSet) throws DocumentException, IOException, SQLException
    {
        super(parkID);
        this.amountPerDay = new int[this.getAmountOfOrderTypes()][31];
        this.totalPerType = new int[this.getAmountOfOrderTypes()];

        this.reportData = resultSet;

        int day, numOfVisitors, type;
        while (this.reportData.next())  // Organizing report data.
        {
            // Building amountPerDay.
            day = reportData.getTimestamp("VisitationDate").toLocalDateTime().getDayOfMonth();
            type = reportData.getInt("OrderType");
            numOfVisitors = reportData.getInt("NumOfVisitors");
            amountPerDay[type - 1][day - 1] += numOfVisitors;

            // Building totalPerDay
            this.totalPerType[type - 1] += numOfVisitors;
        }
        this.reportData.beforeFirst();
    }




    /**
     * Returns the number of order types in the system.
     *
     * @return the number of order types
     */
    private int getAmountOfOrderTypes()
    {
        return OrderType.values().length;
    }




    /**
     * Returns the data for the specified order type and day of the month.
     *
     * @param orderType The order type.
     * @param dayOfOrder The day of the month.
     * @return The number of visitors for the specified order type and day.
     * @throws IllegalArgumentException If the order type or day is invalid.
     * @throws SQLException If there is an error while retrieving the data.
     */
    private int getData(int orderType, int dayOfOrder) throws SQLException
    {
        if (orderType < 1 || orderType > this.getAmountOfOrderTypes() || dayOfOrder < 1 || dayOfOrder > 31)
            throw new IllegalArgumentException("Invalid order type or day");

        return this.amountPerDay[orderType - 1][dayOfOrder - 1];
    }




    /**
     * Returns the data for the specified order type and day of the month.
     *
     * @param orderType The order type.
     * @param dayOfOrder The day of the month.
     * @return The number of visitors for the specified order type and day.
     * @throws IllegalArgumentException If the order type or day is invalid.
     * @throws SQLException If there is an error while retrieving the data.
     */
    private int getData(OrderType orderType, int dayOfOrder) throws SQLException
    {
        return this.getData(orderType.getOrderType(), dayOfOrder);
    }




    /**
     * Returns the total number of visitors for the specified order type.
     *
     * @param orderType The order type.
     * @return The total number of visitors for the specified order type.
     * @throws IllegalArgumentException If the order type is invalid.
     */
    private int getTotalPerType(int orderType) throws SQLException
    {
        if (orderType < 1 || orderType > this.getAmountOfOrderTypes())
            throw new IllegalArgumentException("Invalid order type or day");

        return this.totalPerType[orderType - 1];
    }




    /**
     * Returns the total number of visitors for the specified order type.
     *
     * @param orderType The order type.
     * @return The total number of visitors for the specified order type.
     * @throws IllegalArgumentException If the order type is invalid.
     */
    private int getTotalPerType(OrderType orderType) throws SQLException
    {
        return this.getTotalPerType(orderType.getOrderType());
    }





    /**
     * This method is intended to create a PDF Blob for the NumOfVisitorsReport.
     * Creates a PDF file based on the data in the specified reportData 2D int array.
     *
     * @return A Blob object representing the PDF version of the report.
     * @throws DocumentException If there is an error while creating the PDF document.
     * @throws SQLException If there is an error while converting the PDF to a Blob.
     * @throws IOException If there is an error while handling the PDF file.
     * */
    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException
    {
        String title_Document = "Number of Visitors Report - Park: " + super.getParkID();
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
        PdfPCell titleCell = createSingleCellWithTitle("Order Statistics");
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
     * Creates a  grouped column chart with JFreeChart based on the data in the specified reportData 2D int array.
     *
     * @return The JFreeChart object representing the chart.
     */
    private JFreeChart createBarChart() throws SQLException
    {
        // Definitions
        int data, maxAmount = 0;
        // Initialize dataset for the pie chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Initialize colors for the bars
        Color groupOrdersColor = new Color(12, 36, 58);
        Color singleFamilyOrdersColor = new Color(188, 33, 33);
        // Initialize titles for Axis and chart.
        String title = "Number of Visitors by Order Type";
        String xAxisTitle = "Day of Month";
        String yAxisTitle = "Amount of Visitors";

        // Iterate through the days of the current month
        for (int day = 1; day <= 31; day++)
        {
            for (OrderType orderType : OrderType.values())
            {
                data = this.getData(orderType, day); // Retrieve data
                dataset.addValue(data, orderType.toString(), String.valueOf(day)); // Add to dataset
                maxAmount = Math.max(maxAmount, data); // Get max
            }
        }

        return super.createBarChart(dataset, maxAmount, title,
                xAxisTitle, yAxisTitle, groupOrdersColor, singleFamilyOrdersColor);
    }





    /**
     * Creates a pie chart with JFreeChart based on the data in the specified reportData 2D int array.
     *
     * @return The JFreeChart object representing the pie chart.
     */
    private JFreeChart createPieChart() throws SQLException
    {
        // Initialize dataset for the pie chart
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        // Initialize title
        String title = "Total Visitors Per Order Type In Month";

        // Add data to the dataset
        for (OrderType orderType : OrderType.values())
            dataset.setValue(orderType.toString(), this.getTotalPerType(orderType));

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
        columns.add("Visitation Date");
        columns.add("Order Type");
        columns.add("Number of Visitors");

        // Add table
        PdfPTable table = super.createTable(columns);

        // Collecting Data for table
        while (this.reportData.next())
        {
            int orderTypeRes = reportData.getInt("OrderType");
            if (orderTypeRes == orderType) // Checking if it's the right order type.
            {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String formattedDate = sdf.format(this.reportData.getTimestamp("VisitationDate"));

                table.addCell(this.createCenterCell(formattedDate));
                table.addCell(this.createCenterCell(OrderType.values()[this.reportData.getInt("OrderType") - 1].toString()));
                table.addCell(this.createCenterCell(String.valueOf(this.reportData.getInt("NumOfVisitors"))));
            }
        }
        this.reportData.beforeFirst();

        return table;
    }



    /**
     * Creates a table with the data in the specified ResultSet.
     *
     * @param orderType The order type to display in the table.
     * @return The PdfPTable object representing the table.
     * @throws SQLException If there is an error while retrieving the data.
     */
    private PdfPTable createTable(OrderType orderType) throws SQLException
    {
        return this.createTable(orderType.getOrderType());
    }
}
