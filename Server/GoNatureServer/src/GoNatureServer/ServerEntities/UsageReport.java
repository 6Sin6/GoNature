package GoNatureServer.ServerEntities;

import Entities.ParkReport;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


/**
 * The UsageReport class represents a report detailing the usage of a park.
 * It extends the ParkReport class and contains a bank of UsageReportDetail objects.
 * The class contains several nested classes: MyTime, OrderDetails, and ParkCapacityDetails, which are used to store and manipulate data related to park usage.
 * The class also contains methods for creating a PDF report, creating a line chart, creating a pie chart, and creating a table.
 * The reportData and wasParkFull arrays are used to store data about the number of visitors and whether the park was full at different times.
 * The maxValue variable is used to store the maximum number of visitors at any given time.
 */
public class UsageReport extends ParkReport implements Serializable {
    /**
     * The MyTime class represents a specific time, with fields for the day of the month and the hour of the day.
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    private class MyTime {
        private final int day;
        private final int hour;

        /**
         * The constructor for the MyTime class. It takes a Timestamp object and extracts the day of the month and the hour of the day.
         *
         * @param timestamp A Timestamp object representing the time.
         */
        private MyTime(Timestamp timestamp) {
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            this.day = dateTime.getDayOfMonth();
            this.hour = dateTime.getHour();
        }

        /**
         * Getter for the day field.
         *
         * @return The day of the month.
         */
        private int getDay() {
            return this.day;
        }

        /**
         * Getter for the hour field.
         *
         * @return The hour of the day.
         */
        private int getHour() {
            return this.hour;
        }

    }

    /**
     * The OrderDetails class represents the details of an order, with fields for the entered time, exited time, and number of visitors.
     */
    private class OrderDetails {
        private final MyTime enteredTime;
        private final MyTime exitedTime;
        private final int numOfVisitors;

        /**
         * The constructor for the OrderDetails class. It takes MyTime objects for the entered and exited times, and an integer for the number of visitors.
         *
         * @param enteredTime   A MyTime object representing the time the order was entered.
         * @param exitedTime    A MyTime object representing the time the order was exited.
         * @param numOfVisitors The number of visitors for the order.
         */
        private OrderDetails(MyTime enteredTime, MyTime exitedTime, int numOfVisitors) {
            this.enteredTime = enteredTime;
            this.exitedTime = exitedTime;
            this.numOfVisitors = numOfVisitors;
        }

        /**
         * Getter for the numOfVisitors field.
         *
         * @return The number of visitors for the order.
         */
        private int getNumOfVisitors() {
            return this.numOfVisitors;
        }

        /**
         * Getter for the enteredTime field.
         *
         * @return A MyTime object representing the time the order was entered.
         */
        private MyTime getEnteredTime() {
            return this.enteredTime;
        }

        /**
         * Getter for the exitedTime field.
         *
         * @return A MyTime object representing the time the order was exited.
         */
        private MyTime getExitedTime() {
            return this.exitedTime;
        }
    }

    /**
     * The ParkCapacityDetails class represents the details of the park's capacity, with fields for the park's capacity and the date from which the capacity is valid.
     */
    private class ParkCapacityDetails {
        private final int parkCapacity;

        private MyTime fromWhatDate;

        /**
         * The constructor for the ParkCapacityDetails class. It takes an integer for the park's capacity and a MyTime object for the date from which the capacity is valid.
         *
         * @param parkCapacity The park's capacity.
         * @param fromWhatDate A MyTime object representing the date from which the capacity is valid.
         */
        public ParkCapacityDetails(int parkCapacity, MyTime fromWhatDate) {
            this.parkCapacity = parkCapacity;
            this.fromWhatDate = fromWhatDate;
        }

        private int getParkCapacity() {
            return this.parkCapacity;
        }

        /**
         * Getter for the parkCapacity field.
         *
         * @return The park's capacity.
         */
        private MyTime getFromWhatDate() {
            return this.fromWhatDate;
        }

        /**
         * Getter for the fromWhatDate field.
         *
         * @return A MyTime object representing the date from which the capacity is valid.
         */
        private void setFromWhatDate(MyTime fromWhatDate) {
            this.fromWhatDate = fromWhatDate;
        }
    }


    /*
    Explanation of reportData 2Dimensional array:
    First Dimension : For day in month (1-31)
    Second Dimension: For hour in day (8 o'clock to 20 o'clock).
    Value = amount of visitors in that time.
    Example: reportData[0][0] = 5, meaning in day 1 (of the month) in time 08:00-09:00 there were 5 visitors.


    Explanation of wasParkFull 2Dimensional array:
    First Dimension : For day in month (1-31)
    Second Dimension: For hour in day (8 o'clock to 20 o'clock).
    Value = true if park was full in that time, false otherwise.
    Example: reportData[0][0] = true, meaning in day 1 (of the month) in time 08:00-09:00 the park was full.

     */
    private final int[][] reportData;

    private final boolean[][] wasParkFull;
    private int maxValue;

    /**
     * The constructor for the UsageReport class. It takes a park ID, park name, a ResultSet of orders, and a park capacity object.
     * It initializes the reportData and wasParkFull arrays, and sets the maxValue to 0.
     * It then organizes the park capacity data and order data, and calculates the reportData and wasParkFull arrays.
     *
     * @param parkID The ID of the park.
     * @param parkName The name of the park.
     * @param resultSet_Orders A ResultSet containing the orders.
     * @param parkCapacity An object representing the park's capacity. It can be an Integer or a ResultSet.
     * @throws DocumentException If an error occurs while creating the PDF document.
     * @throws IOException If an error occurs while writing to the ByteArrayOutputStream.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     */
    public UsageReport(Integer parkID, String parkName, ResultSet resultSet_Orders, Object parkCapacity) throws DocumentException, IOException, SQLException {
        super(parkID, parkName);
        this.reportData = new int[31][12];
        this.wasParkFull = new boolean[31][12];
        this.maxValue = 0;
        ArrayList<ParkCapacityDetails> capacity = new ArrayList<>();
        //ParkCapacityDetails[] capacity;
        ArrayList<OrderDetails> orders = new ArrayList<>();


        // Organization of data - Get park capacity data:
        if (parkCapacity instanceof Integer)
            capacity.add(new ParkCapacityDetails((int) parkCapacity, null)); // null used for indicating that it's the last capacity change occurred (this case it's the only capacity).
        else {
            ResultSet resultSet_Capacity = (ResultSet) parkCapacity;
            // Collect data from result set
            while (resultSet_Capacity.next())
                capacity.add(new ParkCapacityDetails(resultSet_Capacity.getInt("requestedValue"),
                        new MyTime(resultSet_Capacity.getTimestamp("handleDate"))));
        }

        // Organization of data - Get orders data (orders are ordered by their EnteredTime
        while (resultSet_Orders.next())
            orders.add(new OrderDetails(new MyTime(resultSet_Orders.getTimestamp("EnteredTime")),
                    new MyTime(resultSet_Orders.getTimestamp("ExitedTime")), resultSet_Orders.getInt("NumOfVisitors")));


        // Now calculating reportData and wasParkFull:
        // Definitions and starters.
        Iterator<OrderDetails> iterator_orders = orders.iterator();
        OrderDetails currOrder;
        if (iterator_orders.hasNext())
            currOrder = iterator_orders.next();
        else return; // No orders.

        Iterator<ParkCapacityDetails> iterator_ParkCapacityDetails = capacity.iterator();
        ParkCapacityDetails currCapacity = iterator_ParkCapacityDetails.next(); // It has next for sure, so no need to check
        if (capacity.size() == 1)
            currCapacity.setFromWhatDate(null);

        int[] amountToExit = new int[12]; // representing amount of people need to exit, and when (indexing like reportData hours)
        int numOfVisitors, totalInPark = 0;

        // Starting collecting data
        for (int day = 1; day <= super.getCurrentDay(); day++)  // each day of month.
        {
            if (currOrder.enteredTime.getDay() == day) // to check if there were no orders in this day
            {
                for (int hour = 8; hour < 20; hour++) // meaning time range: [hour, hour+1)
                {
                    // collect data for reportData
                    if (currOrder.enteredTime.getDay() == day && currOrder.enteredTime.getHour() == hour) // to check if the current order is relevant
                    {
                        // Count the number of people
                        numOfVisitors = currOrder.getNumOfVisitors();
                        totalInPark += numOfVisitors;

                        // if they exit at 20:00 we don't mind since everyone exit there.
                        if (currOrder.getExitedTime().getHour() != 20)
                            amountToExit[this.getIndexForHour(currOrder.getExitedTime())] += numOfVisitors;

                        // go to next order
                        if (iterator_orders.hasNext())
                            currOrder = iterator_orders.next();
                    }
                    this.maxValue = Math.max(this.maxValue, totalInPark);
                    totalInPark -= amountToExit[getIndexForHour(hour)];
                    amountToExit[getIndexForHour(hour)] = 0;
                    this.reportData[getIndexForDay(day)][getIndexForHour(hour)] = totalInPark;

                    // collect data for wasParkFull
                    if (currCapacity.getFromWhatDate() != null) // if it's null, no more changes afterwards.
                    {
                        if (currCapacity.getFromWhatDate().getDay() <= day && currCapacity.getFromWhatDate().getHour() < hour) {
                            if (iterator_ParkCapacityDetails.hasNext())
                                currCapacity = iterator_ParkCapacityDetails.next();
                            else currCapacity.setFromWhatDate(null);
                        }
                    }

                    if (totalInPark == currCapacity.getParkCapacity())
                        this.wasParkFull[getIndexForDay(day)][getIndexForHour(hour)] = true;
                }
                // reached end of day, hour is 20, all leaving:
                totalInPark = 0;
            }
        }
    }

    /**
     * This method calculates the index for a given hour in the reportData and wasParkFull arrays.
     * The arrays have 12 columns, each representing an hour from 8 to 19.
     * Therefore, the method subtracts 8 from the given hour to get the corresponding index.
     *
     * @param hour The hour to get the index for. It should be an integer between 8 and 19.
     * @return The index in the reportData and wasParkFull arrays that corresponds to the given hour.
     */
    private int getIndexForHour(int hour) {
        return hour - 8;
    }

    /**
     * This method calculates the index for the hour of a given MyTime object in the reportData and wasParkFull arrays.
     * The arrays have 12 columns, each representing an hour from 8 to 19.
     * Therefore, the method subtracts 8 from the hour of the MyTime object to get the corresponding index.
     *
     * @param time The MyTime object to get the hour index for.
     * @return The index in the reportData and wasParkFull arrays that corresponds to the hour of the given MyTime object.
     */
    private int getIndexForHour(MyTime time) {
        return time.getHour() - 8;
    }

    /**
     * This method calculates the index for a given day in the reportData and wasParkFull arrays.
     * The arrays have 31 rows, each representing a day of the month from 1 to 31.
     * Therefore, the method subtracts 1 from the given day to get the corresponding index.
     *
     * @param day The day to get the index for. It should be an integer between 1 and 31.
     * @return The index in the reportData and wasParkFull arrays that corresponds to the given day.
     */
    private int getIndexForDay(int day) {
        return day - 1;
    }


    /**
     * This method creates a PDF Blob of the usage report for a park.
     * It first creates a ByteArrayOutputStream and a PDF document with a title.
     * Then, it adds a line chart to the document.
     * It creates a new page in the document and adds a pie chart and a table if the park was full at any time during the month.
     * If the park was not full at all during the month, it adds a message to the document stating this.
     * Finally, it closes the document and returns a Blob containing the PDF data.
     *
     * @return A Blob containing the PDF data of the usage report.
     * @throws DocumentException If an error occurs while creating the PDF document.
     * @throws SQLException If an error occurs while retrieving data from the ResultSet.
     * @throws IOException If an error occurs while writing to the ByteArrayOutputStream.
     */
    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException {
        String title_Document = "Usage Report - Park: " + super.getParkName();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create PDF document
        Document document = super.createPDFDocument(title_Document, outputStream);

        // Add Line Chart
        super.addJFreeChartToDocument(document, this.createLineChart(), 1300, 600);


        document.newPage();

        // Add Pie Chart and Table

        PdfPTable table = this.createTable();
        if (table.getTotalHeight() > 0) // Checking if park was full at all this month
        {
            document.add(super.createPDFTitle("Amount of Days park was full\n"));
            super.addJFreeChartToDocument(document, this.createPieChart(), 500, 500);

            document.newPage();
            document.add(super.createPDFTitle("Statistics"));
            document.add(table);
        } else document.add(super.createPDFTitle("\n\n\nThe park wasn't full at this month at all!"));


        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }

    /**
     * This method creates a table for the usage report of a park.
     * It first initializes the necessary variables and creates an ArrayList for the columns of the table.
     * Then, it creates the table with the specified columns and a column width of 60.
     * It iterates through each day of the current month and each hour from 8 to 19.
     * If the park was full at the specified day and hour, it increments the count of times the park was full,
     * adds the hour range to the information string, and adds a cell to the table for the day, information, and capacity.
     * If the park was not full at the specified day and hour, it adds a cell to the table for the day, count of times the park was full,
     * information, and capacity, and resets the count of times the park was full, information, and capacity.
     * Finally, it returns the created table.
     *
     * @return A PdfPTable representing the usage report of the park.
     */
    private PdfPTable createTable() {
        // Initializing
        String capacity = "", information = "The park was full in these time ranges: ";
        int amountOfTimes = 0;
        // Columns of table
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Day of Month");
        columns.add("Amount of Times was Full");
        columns.add("When Was Full");
        columns.add("Actual Capacity");

        // Add table
        PdfPTable table = super.createTable(columns, 60);


        // Collecting Data for table
        for (int day = 1; day <= this.getCurrentDay(); day++) {
            for (int hour = 8; hour <= 19; hour++) {
                if (this.wasParkFull[this.getIndexForDay(day)][this.getIndexForHour(hour)]) {
                    amountOfTimes++;
                    information += this.getHourRange(hour) + ", ";

                    table.addCell(super.createCenterCell(String.valueOf(day)));
                    table.addCell(super.createCenterCell(information));
                    if (capacity == "")
                        capacity = String.valueOf(this.reportData[this.getIndexForDay(day)][this.getIndexForHour(hour)]);
                    else capacity += ", " + this.reportData[this.getIndexForDay(day)][this.getIndexForHour(hour)];
                }
            }
            if (amountOfTimes != 0) {
                information = information.substring(0, information.length() - 2);

                table.addCell(super.createCenterCell(String.valueOf(day)));
                table.addCell(super.createCenterCell(String.valueOf(amountOfTimes)));
                table.addCell(super.createCenterCell(information));
                table.addCell(super.createCenterCell(capacity));

                information = "The park was full in these time ranges: ";
                amountOfTimes = 0;
                capacity = "";
            }
        }

        return table;
    }

    /**
     * This method generates a string representing a range of one hour starting from the given hour.
     * It uses the SimpleDateFormat class to format the start and end times of the range in the "HH:mm" format.
     * The start time is set to the given hour, and the end time is set to one hour later.
     * The method then returns a string representing the hour range in the format "HH:mm - HH:mm".
     *
     * @param hour The start hour of the range. It should be an integer between 0 and 23.
     * @return A string representing the hour range in the format "HH:mm - HH:mm".
     */
    private String getHourRange(int hour) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date endDate = calendar.getTime();
        return sdf.format(startDate) + " - " + sdf.format(endDate);
    }

    /**
     * This method creates a pie chart representing the usage of the park.
     * It first initializes a DefaultPieDataset and sets the title of the chart to "Park Usage".
     * It then calculates how many days the park was full this month and adds this data to the dataset.
     * The method also adds the number of days the park had available spots to the dataset.
     * Finally, it calls the createPieChart method of the superclass with the dataset and title as arguments, and returns the created JFreeChart.
     *
     * @return A JFreeChart representing a pie chart of the park usage.
     */
    private JFreeChart createPieChart() {
        // Initialize dataset for the pie chart
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        // Initialize title
        String title = "Park Usage";
        // Get how many days was full this month
        int full = this.countDaysFull();

        dataset.setValue("Full Park", full);
        dataset.setValue("Available Spots In Park", 31 - full);

        return super.createPieChart(dataset, title);
    }

    /**
     * This method calculates and returns the number of days the park was full this month.
     * It iterates through each day of the current month and each hour from 8 to 19.
     * If the park was full at the specified day and hour, it increments a count.
     * The method then returns the count.
     *
     * @return The number of days the park was full this month.
     */
    private int countDaysFull() {
        int count = 0;
        for (int day = 1; day <= super.getCurrentDay(); day++) {
            for (int hour = 8; hour <= 19; hour++) {
                if (this.wasParkFull[this.getIndexForDay(day)][this.getIndexForHour(hour)]) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }


    // ******************************** Line Chart Methods ********************************
    // 31 categories in the chart
    /**
     * This method creates a line chart representing the number of visitors for each day of the current month.
     * It first retrieves the dataset for the chart by calling the getDataset method.
     * Then, it creates the line chart with the title, x-axis label, y-axis label, dataset, and other parameters.
     * It customizes the Y-axis, line thickness and colors, background color, and legend item font of the chart.
     * Finally, it returns the created JFreeChart.
     *
     * @return A JFreeChart representing a line chart of the number of visitors for each day of the current month.
     */
    private JFreeChart createLineChart() {
        DefaultCategoryDataset dataset = getDataset();

        // Create the line chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Number of Visitors for Month " + this.getCurrentMonth(),
                "Time",
                "Number of Visitors",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize the Y-axis
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setRange(0, this.getMaxValueAfterFactor());
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Customize line thickness and colors
        for (int i = 0; i < dataset.getRowCount(); i++) {
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(2.0f)); // Increase line thickness
            plot.getRenderer().setSeriesPaint(i, getRandomColor()); // Set a random color for each series
        }

        // Set background color
        plot.setBackgroundPaint(Color.WHITE);

        // Set the legend item font to be bold and larger
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(legend.getItemFont().deriveFont(Font.BOLD, 14f));

        return chart;
    }

    /**
     * This method creates a dataset for the line chart.
     * It initializes a DefaultCategoryDataset and populates it with the number of visitors for each day of the current month and each hour from 8 to 20.
     * The method then returns the created dataset.
     *
     * @return A DefaultCategoryDataset representing the number of visitors for each day of the current month and each hour from 8 to 20.
     */
    private DefaultCategoryDataset getDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Populate dataset with relevant data for days of month and 12 hours
        for (int day = 1; day <= super.getCurrentDay(); day++) {
            String category = String.format("Day %d", day);
            for (int hour = 8; hour <= 20; hour++) {
                int data;
                if (hour != 20)
                    data = reportData[getIndexForDay(day)][getIndexForHour(hour)];
                else data = 0;
                dataset.addValue(data, category, String.valueOf(hour));
            }
        }
        return dataset;
    }

    /**
     * This method generates a random RGB color.
     * It generates random integers for the red, green, and blue components of the color, each between 0 and 255.
     * It then creates and returns a new Color object with the generated RGB components.
     *
     * @return A Color object representing a random RGB color.
     */
    private Color getRandomColor() {
        // Generate a random RGB color
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }

    /**
     * This method calculates and returns the maximum value for the Y-axis of the line chart after adding a factor.
     * It first calculates a factor as 10% of the maximum value of the number of visitors.
     * Then, it adds a certain value to the maximum value based on the calculated factor.
     * The method then returns the calculated maximum value.
     *
     * @return The maximum value for the Y-axis of the line chart after adding a factor.
     */
    private int getMaxValueAfterFactor() {
        double factor = this.maxValue * 0.1;
        if (factor <= 5)
            return this.maxValue + 5;
        if (factor <= 10)
            return this.maxValue + 10;
        if (factor <= 15)
            return this.maxValue + 15;
        return this.maxValue + 20;
    }
}
