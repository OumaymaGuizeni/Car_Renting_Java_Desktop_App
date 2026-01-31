package Services;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Utilis.LogManager;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFExporter {

    public static <T> void exportTableToPDF(TableView<T> table, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // --- First Page: Company Information ---
            // Add company name
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph companyName = new Paragraph("MY BOOKING", titleFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);

            // Add some space
            document.add(new Paragraph("\n\n"));

            // Add date, machine name, and time
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String machineName = InetAddress.getLocalHost().getHostName();

            Font detailsFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Paragraph details = new Paragraph("Date: " + currentDate + " | Time: " + currentTime + " | Machine: " + machineName, detailsFont);
            details.setAlignment(Element.ALIGN_CENTER);
            document.add(details);

            // Add some space
            document.add(new Paragraph("\n\n"));

            // Load logo from file system using a local path
            String imagePath = "src/main/resources/src/logo.png"; // Provide the correct path
            LogManager logger = LogManager.getInstance();
            logger.info("Logo Path is : " + imagePath);
            // System.out.println("Logo path: " + imagePath);
            File logoFile = new File(imagePath);

            if (!logoFile.exists()) {
                throw new IOException("Logo file not found at path: " + imagePath);
            }

            // Create the Image instance from the local file
            Image logo = Image.getInstance(logoFile.getAbsolutePath());
            logo.scaleToFit(200, 200); // Scale logo to fit size
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            // Add some space before signature block
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("This document is electronically signed and verified by:"));
            
            // Signature block (Professional style)
            Paragraph signatureBlock = new Paragraph();
            signatureBlock.setAlignment(Element.ALIGN_CENTER);
            signatureBlock.add(new Paragraph("_____________________________")); // Signature line
            signatureBlock.add(new Paragraph("Yasser JEMLI"));
            signatureBlock.add(new Paragraph("CEO"));
            signatureBlock.add(new Paragraph("Date: " + currentDate));

            // Adding some space between the signature and the table
            document.add(signatureBlock);

            // --- Start New Page for Table ---
            document.newPage();

            // --- Second Page: Table ---
            PdfPTable pdfTable = new PdfPTable(table.getColumns().size());
            pdfTable.setWidthPercentage(100);

            // Set table font
            Font tableFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            // Add table headers
            for (TableColumn<T, ?> column : table.getColumns()) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(column.getText(), tableFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(headerCell);
            }

            // Add table rows
            for (T rowData : table.getItems()) {
                for (TableColumn<T, ?> column : table.getColumns()) {
                    Object cellData = column.getCellObservableValue(rowData).getValue();
                    pdfTable.addCell(cellData != null ? cellData.toString() : "");
                }
            }

            // Add the table to the document
            document.add(pdfTable);
        } catch (Exception e) {
            throw new DocumentException("Failed to export table to PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}
