package Entities;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
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

import static CommonUtils.CommonUtils.parseVisitTime;

/**
 * The NumOfVisitorsReport class represents a report detailing the number of visitors to a park.
 * It extends the ParkReport class and contains information about group and single orders.
 */
public class NumOfVisitorsReport extends ParkReport implements Serializable
{
    private final ResultSet reportData;
    private final int[][] amountPerDay; // total per day of each order type
    private int[] totalPerType; // total of visitors per order type.
    private boolean isDataCalculated, isTotalCalculated;



    /**
     * Constructs a new NumOfVisitorsReport with the specified park ID and report data.
     *
     * @param parkID The ID of the park.
     * @param resultSet The report data.
     */
    public NumOfVisitorsReport(Integer parkID, ResultSet resultSet)
    {
        super(parkID);
        this.amountPerDay = new int[this.getAmountOfOrderTypes()][31];
        this.totalPerType = new int[this.getAmountOfOrderTypes()];
        this.isTotalCalculated = false;
        this.isDataCalculated = false;
        this.reportData = resultSet;
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

        if (!this.isDataCalculated) // in case data is not calculated - calculating.
        {
            int day, numOfVisitors, type;
            while (this.reportData.next()) // building reportData.
            {
                day = reportData.getTimestamp("VisitationDate").toLocalDateTime().getDayOfMonth();
                type = reportData.getInt("OrderType");
                numOfVisitors = reportData.getInt("NumOfVisitors");
                amountPerDay[type - 1][day - 1] += numOfVisitors;
            }
            this.isDataCalculated = true;
        }
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

        if (!this.isTotalCalculated) // in case it wasn't calculated - calculating.
        {
            for (OrderType ot : OrderType.values())
                for (int day = 1; day <= 31; day++)
                    this.totalPerType[ot.getOrderType()] += this.getData(ot, day);
            this.isTotalCalculated = true;
        }

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
        String customFontPath = "/fonts/Roboto-Regular.ttf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create PDF document
        Document document = new Document(PageSize.A2.rotate());
        PdfWriter.getInstance(document, outputStream);

        // Open the document
        document.open();

        // Add title to document
        BaseFont baseFont = BaseFont.createFont(customFontPath, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(baseFont, 24, Font.BOLD, BaseColor.BLACK);
        document.add(this.createParagraph("Number of Visitors Report - Park: " + super.getParkID(), titleFont, true, 50, true));

        // Add Bar Chart (Grouped Column Chart)
        JFreeChart chart = this.createBarChart();
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOutputStream, chart, 1300, 600);
        Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);

        chartImage.setSpacingAfter(75);

        // Add Pie Chart
        JFreeChart pieChart = this.createPieChart();
        ByteArrayOutputStream pieChartOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(pieChartOutputStream, pieChart, 700, 700);
        Image pieChartImage = Image.getInstance(pieChartOutputStream.toByteArray());
        pieChartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(pieChartImage);

        document.newPage();

        // Create left and right columns for tables
        float[] columnWidths = {0.45f, 0.15f, 0.45f};
        PdfPTable tablesContainer = new PdfPTable(columnWidths);
        tablesContainer.setWidthPercentage(100);


        // Single Orders Table
        PdfPTable tableSingle = this.createTable(OrderType.ORD_TYPE_SINGLE.getOrderType());
        tableSingle.setSpacingBefore(30);
        PdfPCell singleCell = new PdfPCell();
        singleCell.addElement(this.createParagraph("Single Orders", titleFont, true, 25, false));
        singleCell.addElement(tableSingle);
        singleCell.setBorder(Rectangle.NO_BORDER);
        tablesContainer.addCell(singleCell);

        // Title Table
        PdfPCell titleCell = new PdfPCell();
        titleCell.addElement(this.createParagraph("Orders Statistics", titleFont, true, 0, true));
        titleCell.setBorder(Rectangle.NO_BORDER);
        tablesContainer.addCell(titleCell);

        // Group Orders Table
        PdfPTable tableGroup = this.createTable(OrderType.ORD_TYPE_GROUP.getOrderType());
        tableGroup.setSpacingBefore(30);
        PdfPCell groupCell = new PdfPCell();
        groupCell.addElement(this.createParagraph("Group Orders", titleFont, true, 25, false));
        groupCell.addElement(tableGroup);
        groupCell.setBorder(Rectangle.NO_BORDER);
        tablesContainer.addCell(groupCell);

        document.add(tablesContainer);

        // Close the document
        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }




    /**
     * Creates a  grouped column chart with JFreeChart based on the data in the specified reportData 2D int array.
     *
     * @return The JFreeChart object representing the chart.
     */
    protected JFreeChart createBarChart() throws SQLException
    {
        // Definitions
        int data, maxAmount = 0;
        // Initialize dataset for the pie chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Initialize colors for the bars
        Color groupOrdersColor = new Color(12, 36, 58);
        Color singleFamilyOrdersColor = new Color(188, 33, 33);

        // Iterate through the days of the current month
        for (int day = 1; day <= 31; day++)
        {
            for (OrderType orderType : OrderType.values())
            {
                data = this.getData(orderType, day); // Retrieve data
                dataset.addValue(data, orderType.toString(), String.valueOf(day)); // Add to dataset
                maxAmount = Math.max(maxAmount, data); // Get max
                if (!this.isTotalCalculated) // summing total of visitors per type, in case it wasn't calculated.
                    this.totalPerType[orderType.getOrderType() - 1] += data;
            }
        }
        this.isTotalCalculated = true; // Calculating total of visitors per type has been done.

        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Number of Visitors by Order Type", // Chart title
                "Date", // X-axis label
                "Amount of Visitors", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL,
                true, // Include legend
                true, // Include tooltips
                false // Include URLs
        );

        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.BLACK);
        chart.getCategoryPlot().setDomainGridlinePaint(Color.BLACK);
        chart.getCategoryPlot().setRangeGridlinePaint(Color.BLACK);

        // Set colors for the bars
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer br = ((BarRenderer) plot.getRenderer());
        br.setBarPainter(new StandardBarPainter());
        br.setMaximumBarWidth(300);
        br.setItemMargin(0.05);
        plot.getRenderer().setSeriesPaint(0, groupOrdersColor);
        plot.getRenderer().setSeriesPaint(1, singleFamilyOrdersColor);

        // Set range for the Y-axis
        plot.getRangeAxis().setRange(0, maxAmount * 1.1);

        return chart;
    }





    /**
     * Creates a pie chart with JFreeChart based on the data in the specified reportData 2D int array.
     *
     * @return The JFreeChart object representing the pie chart.
     */
    protected JFreeChart createPieChart() throws SQLException
    {
        // Initialize dataset for the pie chart
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Add data to the dataset
        for (OrderType orderType : OrderType.values())
            dataset.setValue(orderType.toString(), this.getTotalPerType(orderType));

        // Create the pie chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Total Visitors Per Order Type In Month", // Chart title
                dataset, // Dataset
                true, // Include legend
                true, // Include tooltips
                false // Include URLs
        );

        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.BLACK);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
        return chart;
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
        PdfPTable table = new PdfPTable(columns.size());
        table.setWidthPercentage(100);

        // Add table headers
        columns.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });

        // Collecting Data for tables.
        this.reportData.beforeFirst();
        while (this.reportData.next())
        {
            int orderTypeRes = reportData.getInt("OrderType");
            if (orderTypeRes != orderType) // Checking if it's the right order type.
            {
                this.reportData.next();
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedDate = sdf.format(this.reportData.getTimestamp("VisitationDate"));

            table.addCell(this.createCenterCell(formattedDate));
            table.addCell(this.createCenterCell(OrderType.values()[this.reportData.getInt("OrderType") - 1].toString()));
            table.addCell(this.createCenterCell(String.valueOf(this.reportData.getInt("NumOfVisitors"))));
        }

        return table;
    }



    private PdfPTable createTable(OrderType orderType) throws SQLException
    {
        return this.createTable(orderType.getOrderType());
    }
}
