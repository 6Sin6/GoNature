package Entities;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;

public abstract class ReportConstructor
{
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
     * @param text The text to display in the paragraph.
     * @param font The font to use for the paragraph.
     * @param center Whether to center the paragraph.
     * @param spacing The spacing after the paragraph.
     * @param includeDate Whether to include the current date in the paragraph.
     * @return The Paragraph object representing the paragraph.
     */
    public Paragraph createParagraph(String text, Font font, boolean center, int spacing, boolean includeDate) {
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
    public PdfPCell createCenterCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

}
