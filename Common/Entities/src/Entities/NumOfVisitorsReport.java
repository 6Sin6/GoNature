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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;

/**
 * The NumOfVisitorsReport class represents a report detailing the number of visitors to a park.
 * It extends the ParkReport class and contains information about group and single orders.
 */
public class NumOfVisitorsReport extends ParkReport implements Serializable
{
    private int[][] reportData;



    /**
     * Constructs a NumOfVisitorsReport object with the specified parameters.
     *
     * @param parkID The park associated with the report.
     * @param reportData The report data as a 2D array.
     * The data is stored at 2D array - first dimension is for OrderType, second dimension is for day per month (size 31).
     */
    public NumOfVisitorsReport(Integer parkID, int[][] reportData)
    {
        super(parkID);
        if (reportData[0].length != 31 || reportData.length != this.getAmountOfOrderTypes())
            throw new IllegalArgumentException("Array must be of size 31");
        this.reportData = reportData;
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
     */
    private int getData(int orderType, int dayOfOrder)
    {
        if (orderType < 1 || orderType >= this.getAmountOfOrderTypes() || dayOfOrder < 1 || dayOfOrder > 31)
            throw new IllegalArgumentException("Invalid order type or day");
        return this.reportData[orderType - 1][dayOfOrder - 1];
    }




    /**
     * Returns the data for the specified order type and day of the month.
     *
     * @param orderType The order type.
     * @param dayOfOrder The day of the month.
     * @return The number of visitors for the specified order type and day.
     * @throws IllegalArgumentException If the order type or day is invalid.
     */
    private int getData(OrderType orderType, int dayOfOrder)
    {
        return this.getData(orderType.getOrderType(), dayOfOrder);
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

        // Add title
        BaseFont baseFont = BaseFont.createFont(customFontPath, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(baseFont, 24, Font.BOLD, BaseColor.BLACK);
        document.add(this.createParagraph("Number of Visitors Report - Park: " + super.getParkID(), titleFont, true, 50, true));

        JFreeChart chart = this.createChart();
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOutputStream, chart, 1300, 600);
        Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);

        chartImage.setSpacingAfter(75);

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
        titleCell.addElement(this.createParagraph("Entrance Statistics", titleFont, true, 0, true));
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
     * Creates a chart with JFreeChart based on the data in the specified reportData 2D int array.
     *
     * @return The JFreeChart object representing the chart.
     */
    protected JFreeChart createChart() throws SQLException
    {
        // Definitions
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int data, maxAmount = 0;
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
            }
        }

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
    protected JFreeChart createPieChart() throws SQLException {
        // Initialize dataset for the pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Get total time spent for each order type for the entire month
        double totalGroupTimeSpent = getTotalTimeSpent("_2");
        double totalSingleFamilyTimeSpent = getTotalTimeSpent("_1");

        // Add data to the dataset
        dataset.setValue("Group Orders", totalGroupTimeSpent);
        dataset.setValue("Single/Family-Sized Orders", totalSingleFamilyTimeSpent);

        // Create the pie chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Total Time Spent by Order Type", // Chart title
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
}
