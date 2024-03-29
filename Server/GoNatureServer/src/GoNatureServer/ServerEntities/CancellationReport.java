package GoNatureServer.ServerEntities;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
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
     * @param departmentID   The ID of the department associated with the report.
     * @param departmentData The data associated with the report.
     *                       The data is stored as a HashMap String, ResultSet.
     */
    public CancellationReport(Integer departmentID, HashMap<String, ResultSet> departmentData) throws DocumentException, IOException {
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
     *
     * @param statName   The name of the statistic.
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
     * This method calculates and returns the maximum value for the Y-axis of the line chart after adding a factor.
     * It first calculates a factor as 10% of the maximum value of the number of visitors.
     * Then, it adds a certain value to the maximum value based on the calculated factor.
     * The method then returns the calculated maximum value.
     *
     * @return The maximum value for the Y-axis of the line chart after adding a factor.
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
                handleChartCreation("", totalCount, median, document, baseFont, true, false);
            } else if (key.contains("average")) {
                handleChartCreation("", totalCount, null, document, baseFont, true, true);
            }
            document.newPage();
        }

        for (Map.Entry<String, ResultSet> entry : sortedMapParks.entrySet()) {
            String key = entry.getKey();
            median.set(0);
            totalCount.set(0);

            if (key.contains("distribution")) {
                handleChartCreation(key.split("distribution")[1], totalCount, median, document, baseFont, false, false);
            } else if (key.contains("average")) {
                handleChartCreation(key.split("average")[1], totalCount, null, document, baseFont, false, true);
            }
            document.newPage();
        }

        // Close the document
        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }

    /**
     * This method handles the creation of charts for the cancellation report.
     * It first determines whether to create an average line chart or a distribution histogram based on the averageDistribution parameter.
     * Then, it creates a BufferedImage of the chart and writes it to a ByteArrayOutputStream.
     * The method then creates an Image from the ByteArrayOutputStream and adds it to the PDF document.
     * If the chart is not an average line chart, it also adds the total count and median of cancelled orders to the document.
     *
     * @param suffix The suffix to append to the chart title. It should be the ID of the park if the chart is for a park, or an empty string if the chart is for the department.
     * @param totalCount A reference to an AtomicInteger that will be updated with the total count of cancelled orders.
     * @param median A reference to an AtomicInteger that will be updated with the median of cancelled orders. It can be null if the chart is an average line chart.
     * @param document The PDF document to add the chart to.
     * @param baseFont The font to use for the total count and median titles.
     * @param departmentDistribution A boolean indicating whether the chart is for the department distribution or not.
     * @param averageDistribution A boolean indicating whether the chart is an average line chart or not.
     * @throws IOException If an error occurs while writing to the ByteArrayOutputStream.
     * @throws DocumentException If an error occurs while adding the chart to the PDF document.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private void handleChartCreation(String suffix, AtomicInteger totalCount, AtomicInteger median, Document document, BaseFont baseFont, boolean departmentDistribution, boolean averageDistribution) throws IOException, DocumentException, SQLException {
        JFreeChart chart = averageDistribution ? createAverageLineChart(suffix, departmentDistribution) : createDistributionHistogram(suffix, totalCount, median, departmentDistribution);

        BufferedImage bufferedImage = chart.createBufferedImage(1300, 800);
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartOutputStream);
        chartOutputStream.flush();

        // Embed histogram image into the PDF
        Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
        chartImage.scaleToFit(PageSize.A3.rotate());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);

        if (!averageDistribution) {
            Paragraph totalCountTitle = new Paragraph("Total Cancelled Orders: " + totalCount, new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK));
            totalCountTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(totalCountTitle);

            Paragraph medianTitle = new Paragraph("Median: " + median, new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK));
            medianTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(medianTitle);
        }
    }

    /**
     * This method creates an average line chart for the cancellation report.
     * It first creates a dataset for the chart by calling the createAverageLineChartSeries method.
     * Then, it creates the line chart with the title, x-axis label, y-axis label, dataset, and other parameters.
     * It customizes the background color, line color, and line thickness of the chart.
     * Finally, it returns the created JFreeChart.
     *
     * @param suffix The suffix to append to the chart title. It should be the ID of the park if the chart is for a park, or an empty string if the chart is for the department.
     * @param departmentDistribution A boolean indicating whether the chart is for the department distribution or not.
     * @return A JFreeChart representing an average line chart of the cancellation report.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private JFreeChart createAverageLineChart(String suffix, boolean departmentDistribution) throws SQLException {
        XYSeriesCollection dataset = createAverageLineChartSeries(suffix, departmentDistribution);

        JFreeChart chart = ChartFactory.createXYLineChart(
                departmentDistribution ? "Department Average Cancellations" : "Park" + suffix + " Average Cancellations", // Chart title
                "Day", // X-axis label
                "Percentage", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        chart.getXYPlot().setRenderer(renderer);

        return chart;
    }


    /**
     * This method creates a dataset for the average line chart.
     * It initializes an XYSeries and populates it with the percentage of cancelled orders for each day of the month.
     * The method then adds the series to an XYSeriesCollection and returns it.
     *
     * @param keySuffix The suffix to append to the key when retrieving data from the departmentData or parksData map. It should be the ID of the park if the data is for a park, or an empty string if the data is for the department.
     * @param departmentReports A boolean indicating whether to retrieve data from the departmentData map or the parksData map.
     * @return An XYSeriesCollection representing the dataset for the average line chart.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private XYSeriesCollection createAverageLineChartSeries(String keySuffix, boolean departmentReports) throws SQLException {
        XYSeries series = new XYSeries("Cancelled Orders");

        ResultSet resultSet = departmentReports ? departmentData.get("average" + keySuffix) : parksData.get("average" + keySuffix);

        float[] cancelledOrders = new float[31];
        for (int i = 0; i < 31; i++) {
            cancelledOrders[i] = 0;
        }

        resultSet.beforeFirst();
        while (resultSet.next()) {
            int day = resultSet.getInt("date");
            float totalCancelledOrders = resultSet.getFloat("AverageCancelledOrders");

            // Store total cancelled orders for the corresponding day
            cancelledOrders[day - 1] = totalCancelledOrders;
        }

        // Calculate the maximum cancelled orders to normalize percentages
        float maxCancelledOrders = getMax(cancelledOrders);

        // Add data to the series whilst normalizing percentages
        for (int i = 0; i < 31; i++) {
            float percentageCancelled = (cancelledOrders[i] / maxCancelledOrders) * 100;
            series.add(i + 1, percentageCancelled);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    /**
     * This method calculates and returns the maximum value in a given array of floats.
     *
     * @param array The array of floats to find the maximum value in.
     * @return The maximum value in the given array of floats.
     */
    private float getMax(float[] array) {
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * This method creates a distribution histogram for the cancellation report.
     * It first creates a series for the histogram by calling the createDepartmentDistributionSeries method.
     * Then, it creates the histogram with the title, x-axis label, y-axis label, series, and other parameters.
     * It customizes the appearance of the histogram, including the background color, gridline color, bar color, and y-axis range.
     * Finally, it returns the created JFreeChart.
     *
     * @param suffix The suffix to append to the chart title. It should be the ID of the park if the chart is for a park, or an empty string if the chart is for the department.
     * @param totalCount A reference to an AtomicInteger that will be updated with the total count of cancelled orders.
     * @param median A reference to an AtomicInteger that will be updated with the median of cancelled orders.
     * @param departmentDistribution A boolean indicating whether the chart is for the department distribution or not.
     * @return A JFreeChart representing a distribution histogram of the cancellation report.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private JFreeChart createDistributionHistogram(String suffix, AtomicInteger totalCount, AtomicInteger median, boolean departmentDistribution) throws SQLException {
        XYSeries series = createDepartmentDistributionSeries(totalCount, median, suffix, departmentDistribution);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        String chartTitle = departmentDistribution ? "Department Distribution Histogram" : "Park" + suffix + " Distribution Histogram";

        JFreeChart chart = ChartFactory.createHistogram(
                chartTitle,
                "Day",
                "Total Cancelled Orders",
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


        // Set a minimum value for the y-axis range
        // Adjust bar renderer to display a tiny portion for 0 values
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(12, 36, 58)); // Dark blue color...


        return chart;
    }

    /**
     * This method creates a series for the distribution histogram.
     * It initializes an XYSeries and populates it with the total cancelled orders for each day of the month.
     * The method then returns the created series.
     *
     * @param totalCount A reference to an AtomicInteger that will be updated with the total count of cancelled orders.
     * @param median A reference to an AtomicInteger that will be updated with the median of cancelled orders.
     * @param keySuffix The suffix to append to the key when retrieving data from the departmentData or parksData map. It should be the ID of the park if the data is for a park, or an empty string if the data is for the department.
     * @param departmentReports A boolean indicating whether to retrieve data from the departmentData map or the parksData map.
     * @return An XYSeries representing the series for the distribution histogram.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    private XYSeries createDepartmentDistributionSeries(AtomicInteger totalCount, AtomicInteger median, String keySuffix, boolean departmentReports) throws SQLException {
        XYSeries series = new XYSeries("Cancelled Orders");
        ResultSet resultSet = departmentReports ? departmentData.get("distribution" + keySuffix) : parksData.get("distribution" + keySuffix);

        // Initialize arrays to store total cancelled orders for each day
        double[] cancelledOrders = new double[31];
        ArrayList<Double> cancelledOrdersDataCp = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            cancelledOrders[i] = 0.02;
        }

        // Iterate through ResultSet to populate cancelledOrders array
        resultSet.beforeFirst();
        while (resultSet.next()) {
            int day = resultSet.getInt("date");
            double totalCancelledOrders = (double) resultSet.getInt("TotalCancelledOrders");

            // Store total cancelled orders for the corresponding day
            cancelledOrders[day - 1] = totalCancelledOrders;
            cancelledOrdersDataCp.add(totalCancelledOrders);
            totalCount.addAndGet((int) totalCancelledOrders);
        }

        // If the median is an atomic integer, then calculate the median by sorting the array and getting the middle element. Proof was given during Data Structures course.
        if (median != null && !cancelledOrdersDataCp.isEmpty()) {
            Collections.sort(cancelledOrdersDataCp);

            if (cancelledOrdersDataCp.size() % 2 == 0) {
                int middleIndex = cancelledOrdersDataCp.size() / 2;
                median.set((int) ((cancelledOrdersDataCp.get(middleIndex) + cancelledOrdersDataCp.get(middleIndex - 1)) / 2));
            } else {
                int middleIndex = cancelledOrdersDataCp.size() / 2;
                median.set(cancelledOrdersDataCp.get(middleIndex).intValue());
            }
        }


        // Add data to the series
        for (int i = 1; i <= 31; i++) {
            series.add(i, cancelledOrders[i - 1]);
        }

        return series;
    }
}
