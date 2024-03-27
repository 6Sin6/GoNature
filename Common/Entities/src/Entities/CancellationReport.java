package Entities;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a visitation report for a specific department.
 */
public class CancellationReport extends DepartmentReport implements Serializable {
    private HashMap<String, ResultSet> departmentData;
    private HashMap<String, ResultSet> parksData;

    /**
     * Constructs a new VisitationReport object with the specified date and department.
     *
     * @param departmentID The ID of the department associated with the report.
     * @param departmentData   The data associated with the report.
     * The data is stored as a HashMap String, ResultSet.
     */
    public CancellationReport(Integer departmentID, HashMap<String, ResultSet> departmentData) throws DocumentException, IOException
    {
        super(departmentID);
        this.departmentData = departmentData;
    }

    /**
     * Retrieves the data associated with the department report.
     *
     * @return The data associated with the department report.
     */
    public HashMap<String, ResultSet> getDepartmentData() {
        return departmentData;
    }

    /**
     * Sets the data associated with the department report.
     *
     * @param departmentData The data to set as a HashMap String, ResultSet.
     */
    public void setDepartmentData(HashMap<String, ResultSet> departmentData) {
        this.departmentData = departmentData;
    }

    /**
     * Retrieves the data associated with the park reports.
     *
     * @return The data associated with the park reports.
     */
    public HashMap<String, ResultSet> getParksData() {
        return parksData;
    }

    /**
     * Sets the data associated with the park reports.
     *
     * @param parksData The data to set as a HashMap String, ResultSet.
     */
    public void setParksData(HashMap<String, ResultSet> parksData) {
        this.parksData = parksData;
    }

    /**
     * Adds a ResultSet to the report data.
     * @param statName The name of the statistic.
     * @param reportData The ResultSet to add.
     */
    public void addDepartmentReportData(String statName, ResultSet reportData) {
        this.departmentData.put(statName, reportData);
    }

    /**
     * Removes a ResultSet from the report data.
     *
     * @param statName The statistic to remove, along with its data.
     */
    public void removeDepartmentReportData(String statName) {
        this.departmentData.remove(statName);
    }


    /**
     * Creates a PDF file based on the data in the specified ResultSet.
     *
     * @return A Blob object representing the PDF file.
     */
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
        document.add(this.createPDFTitle("Cancellations Report - Department: " + super.getDepartmentID(), titleFont, 50, true));

        // Create histogram chart
        AtomicInteger totalCount = new AtomicInteger(0); // Must be a reference to update the value in createDistributionSeries
        AtomicInteger median = new AtomicInteger(0);

        // Convert HashMap to TreeMap to sort by keys
        TreeMap<String, ResultSet> sortedMapDepartment = new TreeMap<>(departmentData);
        TreeMap<String, ResultSet> sortedMapParks = new TreeMap<>(parksData);

        for (Map.Entry<String, ResultSet> entry : sortedMapDepartment.entrySet()) {
            String key = entry.getKey();
            median.set(0);
            totalCount.set(0);

            if (key.contains("distribution")) {
                handleChartCreation("", totalCount, median, document, baseFont, true,false);
            } else if (key.contains("average")) {
                handleChartCreation("", totalCount, null, document, baseFont, true,true);
            }
            document.newPage();
        }

        for (Map.Entry<String, ResultSet> entry : sortedMapParks.entrySet()) {
            String key = entry.getKey();
            median.set(0);
            totalCount.set(0);

            if (key.contains("distribution")) {
                handleChartCreation(key.split("distribution")[1], totalCount, median, document, baseFont, false,false);
            } else if (key.contains("average")) {
                handleChartCreation(key.split("average")[1], totalCount, null, document, baseFont, false,true);
            }
            document.newPage();
        }

        // Close the document
        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }

    private void handleChartCreation(String suffix, AtomicInteger totalCount, AtomicInteger median, Document document, BaseFont baseFont, boolean departmentDistribution, boolean averageDistribution) throws IOException, DocumentException, SQLException {
        JFreeChart chart = createDistributionHistogram(suffix, totalCount, median, departmentDistribution, averageDistribution);
        BufferedImage bufferedImage = chart.createBufferedImage(1300, 800);
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartOutputStream);
        chartOutputStream.flush();

        // Embed histogram image into the PDF
        Image histogramImage = Image.getInstance(chartOutputStream.toByteArray());
        histogramImage.scaleToFit(PageSize.A3.rotate());
        histogramImage.setAlignment(Element.ALIGN_CENTER);
        document.add(histogramImage);

        if (!averageDistribution) {
            Paragraph totalCountTitle = new Paragraph("Total Cancelled Orders: " + totalCount, new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK));
            totalCountTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(totalCountTitle);

            Paragraph medianTitle = new Paragraph("Median: " + median, new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK));
            medianTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(medianTitle);
        }
    }

    private JFreeChart createDistributionHistogram(String suffix, AtomicInteger totalCount, AtomicInteger median, boolean departmentDistribution, boolean averageDistribution) throws SQLException {
        XYSeries series = null;
        if (averageDistribution) {
            series = createDepartmentAverageHistogram(suffix, departmentDistribution);
        } else {
            series = createDepartmentDistributionSeries(totalCount, median, suffix, departmentDistribution);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        String chartTitle;
        if (departmentDistribution) {
            chartTitle = averageDistribution ? "Department Average Distribution Histogram" : "Department Distribution Histogram";
        } else {
            chartTitle = averageDistribution ? "Park" + suffix + " Average Distribution Histogram" : "Park" + suffix + " Distribution Histogram";
        }

        JFreeChart chart = ChartFactory.createXYBarChart(
                chartTitle,
                "Day",
                false,
                averageDistribution ? "Average Cancelled Orders Of All Orders" : "Total Cancelled Orders",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.BLACK);
        chart.getXYPlot().setDomainGridlinePaint(Color.BLACK);
        chart.getXYPlot().setRangeGridlinePaint(Color.BLACK);

        // Set colors for the bars
        XYPlot plot = chart.getXYPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(12, 36, 58));

        return chart;
    }

    private XYSeries createDepartmentAverageHistogram(String keySuffix, boolean departmentReports) throws SQLException {
        XYSeries series = new XYSeries("Cancelled Orders");
        ResultSet resultSet = departmentReports ? departmentData.get("average" + keySuffix) : parksData.get("average" + keySuffix);

        // Initialize arrays to store total cancelled orders for each day
        float[] cancelledOrders = new float[31];
        for (int i = 0; i < 31; i++) {
            cancelledOrders[i] = 0;
        }

        // Iterate through ResultSet to populate cancelledOrders array
        while (resultSet.next()) {
            int day = resultSet.getInt("date");
            float totalCancelledOrders = resultSet.getFloat("AverageCancelledOrders");

            // Store total cancelled orders for the corresponding day
            cancelledOrders[day - 1] = totalCancelledOrders;
        }

        // Add data to the series
        for (int i = 0; i < 31; i++) {
            series.add(i + 1, cancelledOrders[i]);
        }

        return series;
    }

    private XYSeries createDepartmentDistributionSeries(AtomicInteger totalCount, AtomicInteger median, String keySuffix, boolean departmentReports) throws SQLException {
        XYSeries series = new XYSeries("Cancelled Orders");
        ResultSet resultSet = departmentReports ? departmentData.get("distribution" + keySuffix) : parksData.get("distribution" + keySuffix);

        // Initialize arrays to store total cancelled orders for each day
        int[] cancelledOrders = new int[31];
        ArrayList<Integer> cancelledOrdersDataCp = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            cancelledOrders[i] = 0;
        }

        // Iterate through ResultSet to populate cancelledOrders array
        while (resultSet.next()) {
            int day = resultSet.getInt("date");
            int totalCancelledOrders = resultSet.getInt("TotalCancelledOrders");

            // Store total cancelled orders for the corresponding day
            cancelledOrders[day - 1] = totalCancelledOrders;
            cancelledOrdersDataCp.add(totalCancelledOrders);
            totalCount.addAndGet(totalCancelledOrders);
        }

        // If the median is an atomic integer, then calculate the median by sorting the array and getting the middle element. Proof was given during Data Structures course.
        if (median != null && !cancelledOrdersDataCp.isEmpty()) {
            Collections.sort(cancelledOrdersDataCp);

            if (cancelledOrdersDataCp.size() % 2 == 0) {
                int middleIndex = cancelledOrdersDataCp.size() / 2;
                median.set((cancelledOrdersDataCp.get(middleIndex) + cancelledOrdersDataCp.get(middleIndex - 1)) / 2);
            } else {
                int middleIndex = cancelledOrdersDataCp.size() / 2;
                median.set(cancelledOrdersDataCp.get(middleIndex));
            }
        }


        // Add data to the series
        for (int i = 0; i < 31; i++) {
            series.add(i + 1, cancelledOrders[i]);
        }

        return series;
    }
}
