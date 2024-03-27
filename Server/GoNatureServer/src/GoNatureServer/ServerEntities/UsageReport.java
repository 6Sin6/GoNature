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

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


/**
 * The UsageReport class represents a report detailing the usage of a park.
 * It extends the ParkReport class and contains a bank of UsageReportDetail objects.
 */
public class UsageReport extends ParkReport implements Serializable
{
    @SuppressWarnings("InnerClassMayBeStatic")
    private class MyTime
    {
        private final int day;
        private final int hour;


        private MyTime(Timestamp timestamp)
        {
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            this.day = dateTime.getDayOfMonth();
            this.hour = dateTime.getHour();
        }

        private int getDay()
        {
            return this.day;
        }

        private int getHour()
        {
            return this.hour;
        }

    }
    private class OrderDetails
    {
        private final MyTime enteredTime;
        private final MyTime exitedTime;
        private final int numOfVisitors;

        private OrderDetails(MyTime enteredTime, MyTime exitedTime, int numOfVisitors)
        {
            this.enteredTime = enteredTime;
            this.exitedTime = exitedTime;
            this.numOfVisitors = numOfVisitors;
        }

        private int getNumOfVisitors()
        {
            return this.numOfVisitors;
        }

        private MyTime getEnteredTime()
        {
            return this.enteredTime;
        }

        private MyTime getExitedTime()
        {
            return this.exitedTime;
        }
    }


    private class ParkCapacityDetails
    {
        private final int parkCapacity;

        private MyTime fromWhatDate;

        public ParkCapacityDetails(int parkCapacity, MyTime fromWhatDate)
        {
            this.parkCapacity = parkCapacity;
            this.fromWhatDate = fromWhatDate;
        }

        private int getParkCapacity()
        {
            return this.parkCapacity;
        }

        private MyTime getFromWhatDate()
        {
            return this.fromWhatDate;
        }

        private void setFromWhatDate(MyTime fromWhatDate)
        {
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


    public UsageReport(Integer parkID, String parkName, ResultSet resultSet_Orders, Object parkCapacity) throws DocumentException, IOException, SQLException
    {
        super(parkID, parkName);
        this.reportData = new int[31][12];
        this.wasParkFull = new boolean[31][12];
        this.maxValue = 0;
        ArrayList<ParkCapacityDetails> capacity = new ArrayList<>();
        //ParkCapacityDetails[] capacity;
        ArrayList<OrderDetails> orders = new ArrayList<>();


        // Organization of data - Get park capacity data:
        if (parkCapacity instanceof Integer)
            capacity.add(new ParkCapacityDetails((int)parkCapacity, null)); // null used for indicating that it's the last capacity change occurred (this case it's the only capacity).
        else
        {
            ResultSet resultSet_Capacity = (ResultSet) parkCapacity;
            // Collect data from result set
            while(resultSet_Capacity.next())
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
                        if (currCapacity.getFromWhatDate().getDay() <= day && currCapacity.getFromWhatDate().getHour() < hour)
                        {
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

    private int getIndexForHour(int hour)
    {
        return hour - 8;
    }

    private int getIndexForHour(MyTime time)
    {
        return time.getHour() - 8;
    }

    private int getIndexForDay(int day)
    {
        return day - 1;
    }





    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException
    {
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
        }
        else document.add(super.createPDFTitle("\n\n\nThe park wasn't full at this month at all!"));


        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }



    private PdfPTable createTable()
    {
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
        for (int day = 1; day <= this.getCurrentDay(); day++)
        {
            for (int hour = 8; hour <= 19; hour++)
            {
                if (this.wasParkFull[this.getIndexForDay(day)][this.getIndexForHour(hour)])
                {
                    amountOfTimes++;
                    information += this.getHourRange(hour) + ", ";

                    table.addCell(super.createCenterCell(String.valueOf(day)));
                    table.addCell(super.createCenterCell(information));
                    if (capacity == "")
                        capacity = String.valueOf(this.reportData[this.getIndexForDay(day)][this.getIndexForHour(hour)]);
                    else capacity += ", " + this.reportData[this.getIndexForDay(day)][this.getIndexForHour(hour)];
                }
            }
            if (amountOfTimes != 0)
            {
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




    private String getHourRange(int hour)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date endDate = calendar.getTime();
        return sdf.format(startDate) + " - " + sdf.format(endDate);
    }




    private JFreeChart createPieChart()
    {
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


    private int countDaysFull()
    {
        int count = 0;
        for(int day = 1; day <= super.getCurrentDay(); day++)
        {
            for (int hour = 8; hour <= 19; hour++)
            {
                if (this.wasParkFull[this.getIndexForDay(day)][this.getIndexForHour(hour)])
                {
                    count++;
                    break;
                }
            }
        }
        return count;
    }












    // ******************************** Line Chart Methods ********************************
    // 31 categories in the chart
    private JFreeChart createLineChart()
    {
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
        for (int i = 0; i < dataset.getRowCount(); i++)
        {
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




    private DefaultCategoryDataset getDataset()
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Populate dataset with relevant data for days of month and 12 hours
        for (int day = 1; day <= super.getCurrentDay() ; day++)
        {
            String category = String.format("Day %d", day);
            for (int hour = 8; hour <= 20; hour++)
            {
                int data;
                if (hour != 20)
                    data = reportData[getIndexForDay(day)][getIndexForHour(hour)];
                else data = 0;
                dataset.addValue(data, category, String.valueOf(hour));
            }
        }
        return dataset;
    }






    private Color getRandomColor()
    {
        // Generate a random RGB color
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }




    private int getMaxValueAfterFactor()
    {
        double factor = this.maxValue*0.1;
        if (factor <= 5)
            return this.maxValue + 5;
        if (factor <= 10)
            return this.maxValue + 10;
        if (factor <= 15)
            return this.maxValue + 15;
        return this.maxValue + 20;
    }

    
// Line chart graph, per day:
/*
    public JFreeChart createLineChart(int day)
    {
        // Initialize dataset for the line chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Initialize titles for Axis and chart
        String title = "Number of People in the Park";
        String xAxisTitle = "Time (Hour)";
        String yAxisTitle = "Number of People";

        // Add data to the dataset for the specified day
        for (int hour = 8; hour <= 19; hour++)
        {
            int visitors = reportData[this.getIndexForDay(day)][this.getIndexForHour(hour)];
            dataset.addValue(visitors, "Number of People", String.valueOf(hour));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                xAxisTitle,
                yAxisTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize the Y-axis range
        chart.getCategoryPlot().getRangeAxis().setRange(0, maxValue + 5);

        // Customize the x-axis to show hours
        chart.getCategoryPlot().getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));


        // Create a ChartPanel with the chart and set its size
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        // Return the JFreeChart object
        return chart;
    }
    */
}
