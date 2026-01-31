package Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.collections.ObservableList;
import Entite.House;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExportService {

    // Méthode pour exporter la liste des maisons en Excel
    public void generateExcelHouseTable(String fileName, ObservableList<House> houses) throws IOException {
        Workbook workbook = new XSSFWorkbook();  // Créer un nouveau classeur Excel
        Sheet sheet = workbook.createSheet("Houses");  // Créer une feuille Excel

        // Créer une ligne d'en-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nom");
        headerRow.createCell(2).setCellValue("Localisation");
        headerRow.createCell(3).setCellValue("Chambres Disponibles");
        headerRow.createCell(4).setCellValue("Prix par Jour");
        headerRow.createCell(5).setCellValue("Capacité Totale");
        headerRow.createCell(6).setCellValue("Réservé par notre Agence");

        // Créer des styles pour les cellules
        CellStyle greenStyle = workbook.createCellStyle();
        greenStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        CellStyle orangeStyle = workbook.createCellStyle();
        orangeStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        orangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Remplir le tableau avec les données des maisons
        int rowNum = 1;  // Commencer à la ligne 1 (après l'en-tête)
        for (House house : houses) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(house.getId());
            row.createCell(1).setCellValue(house.getName());
            row.createCell(2).setCellValue(house.getLocation());
            row.createCell(3).setCellValue(house.getRoomsAvailable());
            row.createCell(4).setCellValue(house.getPricePerNight());
            row.createCell(5).setCellValue(house.getFullCapacity());

            // Créer et définir la valeur dans la cellule "Réservé par notre Agence"
            Cell reservedCell = row.createCell(6);
            String reservedByAgency = house.getReservedByOurAgency();  // Récupérer la valeur

            reservedCell.setCellValue(reservedByAgency);

            // Appliquer la couleur en fonction de getFullCapacity
            if (house.getFullCapacity() != null && house.getFullCapacity().equals("Reserved")) {
                // Appliquer orange si FullCapacity == "Reserved"
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    row.getCell(i).setCellStyle(orangeStyle);  // Appliquer orange à toutes les cellules de la ligne
                }
            } else {
                // Appliquer vert si FullCapacity != "Reserved"
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    row.getCell(i).setCellStyle(greenStyle);  // Appliquer vert à toutes les cellules de la ligne
                }
            }
        }
 
        // Écrire dans le fichier Excel
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }

        workbook.close();  // Fermer le classeur
    }
}
