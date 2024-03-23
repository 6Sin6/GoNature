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
import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
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
     * The data is stored as a HashMap<String, ResultSet>.
     */
    public VisitationReport(Integer departmentID, String statName, ResultSet reportData) {
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
     * @param reportData The data to set as a HashMap<String, ResultSet>.
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
     * Creates a PDF file based on the data in the specified ResultSet.
     *
     * @return A Blob object representing the PDF file.
     */
    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
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
        document.add(this.createParagraph("Visitations Report - Department: " + super.getDepartmentID(), titleFont, true, 50, true));

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
     * Creates a chart with JFreeChart based on the data in the specified ResultSet.
     *
     * @return The JFreeChart object representing the chart.
     */
    protected JFreeChart createChart() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double maxTimeSpent = 0;

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Initialize colors for the bars
        Color groupOrdersColor = new Color(12, 36, 58);
        Color singleFamilyOrdersColor = new Color(188, 33, 33);

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

        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Average Time Spent by Order Type", // Chart title
                "Date", // X-axis label
                "Average Time Spent (Hours)", // Y-axis label
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
        plot.getRangeAxis().setRange(0, maxTimeSpent * 1.1);

        return chart;
    }




    /**
     * Creates a pie chart with JFreeChart based on the data in the specified ResultSet.
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




    /**
     * Creates a table with the data in the specified ResultSet.
     * @params orderType The order type to display in the table.
     * @return The PdfPTable object representing the table.
     */
    private PdfPTable createTable(int orderType) throws SQLException {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Park ID");
        columns.add("Park Name");
        columns.add("Entrance Time");
        columns.add("Reservation Time");
        columns.add("Time Spent");

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

        ResultSet entranceData = this.reportData.get("entrance");
        entranceData.beforeFirst();
        while (entranceData.next()) {
            int orderTypeRes = entranceData.getInt("OrderType");
            if (orderTypeRes != orderType) {
                entranceData.next();
                continue;
            }
            table.addCell(this.createCenterCell(String.valueOf(entranceData.getInt("ParkID"))));
            table.addCell(this.createCenterCell(entranceData.getString("ParkName")));
            table.addCell(this.createCenterCell(String.valueOf(entranceData.getTimestamp("EntranceTime"))));
            table.addCell(this.createCenterCell(String.valueOf(entranceData.getTimestamp("ReservationTime"))));
            table.addCell(this.createCenterCell(parseVisitTime(entranceData.getTimestamp("TimeSpent"))));
        }

        return table;
    }




    /**
     * Creates a paragraph with the specified text and font.
     *
     * @param text The text to display in the paragraph.
     * @param font The font to use for the paragraph.
     * @param center Whether to center the paragraph.
     * @param spacing The spacing after the paragraph.
     * @param includeDate Whether to include the current date in the paragraph.
     * @return The Paragraph object representing the paragraph.
     */
    private Paragraph createParagraph(String text, Font font, boolean center, int spacing, boolean includeDate) {
        text = includeDate ? text + " - " + LocalDate.now() : text;
        Paragraph title = new Paragraph(text, font);
        if (center) {
            title.setAlignment(Element.ALIGN_CENTER);
        }
        title.setSpacingAfter(spacing);
        return title;
    }




    /**
     * Creates a cell with the specified text and centers it.
     *
     * @param text The text to display in the cell.
     * @return The PdfPCell object representing the cell.
     */
    private PdfPCell createCenterCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
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
