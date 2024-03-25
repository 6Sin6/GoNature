package Entities;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The UsageReport class represents a report detailing the usage of a park.
 * It extends the ParkReport class and contains a bank of UsageReportDetail objects.
 */
public class UsageReport extends ParkReport implements Serializable
{
    private final ResultSet reportData;
    private boolean[] wasFull;


    public UsageReport(Integer parkID, ResultSet resultSet) throws DocumentException, IOException, SQLException
    {
        super(parkID);
        this.reportData = resultSet;
        this.wasFull = new boolean[31];
    }




    @Override
    public Blob createPDFBlob() throws DocumentException, SQLException, IOException
    {
        String title_Document = "Usage Report - Park: " + super.getParkID();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create PDF document
        Document document = super.createPDFDocument(title_Document, outputStream);

        // Create left and right columns for tables
        float[] columnWidths = {0.45f, 0.15f, 0.45f};
        PdfPTable tablesContainer = new PdfPTable(columnWidths);
        tablesContainer.setWidthPercentage(100);

        // Add Table of Full Park information
        PdfPTable tableFull = this.createTable_Full();
        tableFull.setSpacingBefore(30);
        PdfPCell singleCell = super.createSingleCellWithTitleAndTable(tableFull, "When Park Was Full");
        tablesContainer.addCell(singleCell);

        // Title Table
        PdfPCell titleCell = createSingleCellWithTitle("Usage Statistics");
        tablesContainer.addCell(titleCell);

        // Add Table of not full.
        PdfPTable tableNotFull = this.createTable_NotFull();
        tableNotFull.setSpacingBefore(30);
        PdfPCell groupCell = super.createSingleCellWithTitleAndTable(tableNotFull, "Group Orders");
        tablesContainer.addCell(groupCell);

        document.add(tablesContainer);

        document.newPage();


        // Add Pie Chart
        super.addJFreeChartToDocument(document, this.createPieChart(), 700, 700);



        document.close();

        return new SerialBlob(outputStream.toByteArray());
    }



    private PdfPTable createTable_Full() throws SQLException
    {
        // Initializing
        int day, capacity;
        String formattedDate, monthValue, information;
        LocalDate currentDate = LocalDate.now();
        // Columns of table
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Day");
        columns.add("Information");

        // Add table
        PdfPTable table = super.createTable(columns, 100);

        // Collecting Data for table
        while (this.reportData.next())
        {
            day = this.reportData.getInt("Day");
            capacity = this.reportData.getInt("capacity");
            this.wasFull[day - 1] = true;

            information = "The park was full on this day, with capacity " + capacity;

            monthValue = String.format("%02d", currentDate.getMonthValue());
            formattedDate = String.format("%02d", day) + "-" + monthValue;

            table.addCell(this.createCenterCell(formattedDate));
            table.addCell(this.createCenterCell(information));
        }
        this.reportData.beforeFirst();

        return table;
    }

    private PdfPTable createTable_NotFull()
    {
        // Initializing
        String formattedDate, monthValue, information = "The park wasn't full on this day.";
        LocalDate currentDate = LocalDate.now();

        // Columns of table
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Day");
        columns.add("Information");

        // Add table
        PdfPTable table = super.createTable(columns, 100);

        // Collecting Data for table
        for (int day = 1; day <= 31; day++)
        {
            if (!this.wasFull[day - 1])
            {
                monthValue = String.format("%02d", currentDate.getMonthValue());
                formattedDate = String.format("%02d", day) + "-" + monthValue;

                table.addCell(this.createCenterCell(formattedDate));
                table.addCell(this.createCenterCell(information));
            }
        }

        return table;
    }



    private JFreeChart createPieChart() throws SQLException
    {
        // Initialize dataset for the pie chart
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        // Initialize title
        String title = "Park Usage";
        // Get how many times was full
        int full = this.countFull();

        dataset.setValue("Full Park", full);
        dataset.setValue("Not Full Park", 31 - full);

        return super.createPieChart(dataset, title);
    }


    private int countFull()
    {
        int count = 0;
        for (boolean wasFull : this.wasFull)
            if (wasFull)
                count++;
        return count;
    }




}
