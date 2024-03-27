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

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A class that constructs PDF reports based on data retrieved from a database.
 */
public abstract class ReportConstructor
{
    private final Font titleFont;


    /**
     * Creates a new ReportConstructor instance.
     *
     * @throws DocumentException If an error occurs while loading the custom font.
     * @throws IOException If an error occurs while reading the font file.
     */
    protected ReportConstructor() throws DocumentException, IOException
    {
        String customFontPath = "/fonts/Roboto-Regular.ttf";
        BaseFont baseFont = BaseFont.createFont(customFontPath, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        this.titleFont = new Font(baseFont, 24, Font.BOLD, BaseColor.BLACK);
    }

    /**
     * Creates a PDF file based on the data in the specified ResultSet.
     * @return A Blob object representing the PDF file.
     * @throws DocumentException If an error occurs during the creation of the PDF document.
     * @throws SQLException If an error occurs while accessing the database.
     * @throws IOException If an error occurs while reading the data.
     */
    public abstract Blob createPDFBlob() throws DocumentException, SQLException, IOException;



    /**
     * Creates a paragraph with the specified text and font.
     *
     * @param text        The text to display in the paragraph.
     * @param font        The font to use for the paragraph.
     * @param spacing     The spacing after the paragraph.
     * @param includeDate Whether to include the current date in the paragraph.
     * @return The Paragraph object representing the paragraph.
     */
    protected Paragraph createPDFTitle(String text, Font font, int spacing, boolean includeDate)
    {
        text = includeDate ? text + " - " + LocalDate.now() : text;
        Paragraph title = new Paragraph(text, font);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(spacing);
        return title;
    }



    /**
     * Creates a PDF title with the specified text.
     *
     * @param title The text of the title.
     * @return A Paragraph object representing the PDF title.
     */
    protected Paragraph createPDFTitle(String title)
    {
        return (this.createPDFTitle(title, titleFont, 25, false));
    }





    /**
     * Creates a cell with the specified text and centers it.
     *
     * @param text The text to display in the cell.
     * @return The PdfPCell object representing the cell.
     */
    protected PdfPCell createCenterCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }




    /**
     * Creates a PDF document based on the data in the specified ResultSet.
     *
     * @param documentTitle The title of the PDF document.
     * @param outputStream The output stream to write the PDF document to.
     * @return The PDF document.
     * @throws DocumentException If an error occurs while creating the PDF document.
     */
    protected Document createPDFDocument(String documentTitle, ByteArrayOutputStream outputStream) throws DocumentException
    {
        // Create PDF document
        Document document = new Document(PageSize.A2.rotate());
        PdfWriter.getInstance(document, outputStream);

        // Open the document
        document.open();

        // Add title to document
        document.add(this.createPDFTitle(documentTitle, titleFont, 50, true));
        return document;
    }




    /**
     * Adds a JFreeChart to the specified PDF document.
     *
     * @param document The PDF document.
     * @param chart The JFreeChart to add.
     * @param width The width of the chart.
     * @param height The height of the chart.
     * @throws IOException If an error occurs while writing the chart to a byte array.
     * @throws DocumentException If an error occurs while adding the chart to the PDF document.
     */
    protected void addJFreeChartToDocument(Document document, JFreeChart chart, int width, int height) throws IOException, DocumentException
    {
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOutputStream, chart, width, height);
        Image chartImage = Image.getInstance(chartOutputStream.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);
    }




    /**
     * Creates a cell with a title and a table.
     *
     * @param table The table to display.
     * @param title The title of the table.
     * @return The PdfPCell object representing the cell.
     */
    protected PdfPCell createSingleCellWithTitleAndTable(PdfPTable table, String title)
    {
        PdfPCell singleCell = new PdfPCell();
        singleCell.addElement(this.createPDFTitle(title, titleFont, 25, false));
        singleCell.addElement(table);
        singleCell.setBorder(Rectangle.NO_BORDER);
        return singleCell;
    }




    /**
     * Creates a cell with a title.
     *
     * @param text The text to display in the title.
     * @return The PdfPCell object representing the cell.
     */
    protected PdfPCell createSingleCellWithTitle(String text)
    {
        PdfPCell titleCell = new PdfPCell();
        titleCell.addElement(this.createPDFTitle(text, this.titleFont, 0, true));
        titleCell.setBorder(Rectangle.NO_BORDER);
        return titleCell;
    }


    /**
     * Creates a bar chart with the specified dataset, title, axis labels, colors, and maximum value.
     *
     * @param dataset the dataset for the chart
     * @param maxValue the maximum value for the Y-axis
     * @param title the title of the chart
     * @param xAxisTitle the title of the X-axis
     * @param yAxisTitle the title of the Y-axis
     * @param firstBarColor the color of the first bar
     * @param secondBarColor the color of the second bar
     * @return the bar chart
     */
    protected JFreeChart createGroupedColumnChart(DefaultCategoryDataset dataset, double maxValue, String title,
                                                  String xAxisTitle, String yAxisTitle, Color firstBarColor, Color secondBarColor)
    {
        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                title, // Chart title
                xAxisTitle, // X-axis label
                yAxisTitle, // Y-axis label
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
        plot.getRenderer().setSeriesPaint(0, firstBarColor);
        plot.getRenderer().setSeriesPaint(1, secondBarColor);

        // Set range for the Y-axis
        plot.getRangeAxis().setRange(0, maxValue * 1.1);

        return chart;
    }




    /**
     * Creates a pie chart with the specified dataset and title.
     *
     * @param dataset the dataset for the chart
     * @param title the title of the chart
     * @return the pie chart
     */
    protected JFreeChart createPieChart(DefaultPieDataset<?> dataset, String title)
    {
        JFreeChart chart = ChartFactory.createPieChart(
                title, // Chart title
                dataset, // Dataset
                true, // Include legend
                true, // Include tooltips
                false // Include URLs
        );

        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.BLACK);
        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
        return chart;
    }




    /**
     * Creates a table with the specified columns and width percentage.
     *
     * @param columns a list of column titles
     * @param widthPercentage the width of the table as a percentage of the page width
     * @return a PdfPTable object representing the table
     */
    protected PdfPTable createTable(ArrayList<String> columns, int widthPercentage)
    {
        PdfPTable table = new PdfPTable(columns.size());
        table.setWidthPercentage(widthPercentage);

        // Add table headers
        columns.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });

        return table;
    }



    /**
     * Returns the current month as a two-digit string, padded with leading zeros if necessary.
     *
     * @return the current month as a two-digit string
     */
    protected String getCurrentMonth()
    {
        return String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
    }



    /**
     * Returns the current day of the month as an integer.
     *
     * @return the current day of the month as an integer
     */
    protected int getCurrentDay()
    {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

}
