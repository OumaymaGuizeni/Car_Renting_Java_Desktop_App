package controllers.User_controllers;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import Entite.House;
import Entite.Personne;
import Entite.Reservation;
import Services.EmailSender;
import Services.PdfExportService;
import Services.Service_Houses;
import Services.Service_Reservation;
import Utilis.AlertUtils;
import Utilis.LogManager;
import controllers.Admin_controllers.ManageUsersController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class HouseConfirmeReservation {
    private final LogManager logger = LogManager.getInstance();
    private final Service_Reservation Servicereservation = new Service_Reservation();
    @FXML
    private CheckBox checkBoxCash;

    @FXML
    private CheckBox checkBoxCard;
    @FXML
    private TableView<House> tableHouses; // Correct naming
    private House house;  // Objet qui va recevoir les données
    @FXML
    private TextField priceField;
    private ComboBox<String> comboBoxHouses; // For house selection
    @FXML
    private Button btnLogout;

    @FXML
    private DatePicker startDatePicker; // Start Date Picker
    @FXML
    private DatePicker endDatePicker;   // End Date Picker
    @FXML
    private TextField additionalInfoField; // Additional information TextField

    // Bouton pour la confirmation
    @FXML
    private Button btnConfirm;
    
    private Scene rootScene;
    private Personne user;
    private final Service_Houses houseService = new Service_Houses();

    // Méthode setter pour l'objet House
    public void setHouse(House house) {
        this.house = house;
    }
    public void setUser(Personne user) {
        if (user == null) {
            logger.warn("Warning: User is null when set in HouseReservationController");
        }
        this.user = user;
    }

    public void setRootScene(Scene rootScene) {
        this.rootScene = rootScene;
    }

    @FXML
    private void initialize() {
        // Make sure tableHouses is properly initialized
        if (tableHouses != null) {

        } else {
            logger.error("Error: tableHouses is null during initialization");
        }

     // Event handler for cash payment
     checkBoxCash.setOnAction(event -> handlePaymentOptionSelection());
     
     // Event handler for card payment
     checkBoxCard.setOnAction(event -> handlePaymentOptionSelection());
    }
    private void handlePaymentOptionSelection() {
        if (checkBoxCash.isSelected() || checkBoxCard.isSelected()) {
            // Make sure the other payment option is deselected
            if (checkBoxCash.isSelected()) {
                checkBoxCard.setSelected(false);
            } else if (checkBoxCard.isSelected()) {
                checkBoxCash.setSelected(false);
            }
    // Vérifiez que les champs de date sont renseignés
    if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
        AlertUtils.showAlertFor3Seconds("Error", "Please select both start and end dates.", Alert.AlertType.ERROR);
        return;
    }

    // Récupérer les dates sélectionnées
    LocalDate startDate = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();

    // Vérifiez que la date de début est antérieure à la date de fin
    if (!startDate.isBefore(endDate)) {
        AlertUtils.showAlertFor3Seconds("Error", "The start date must be before the end date.", Alert.AlertType.ERROR);
        return;
    }
    // Vérifiez que la date de début et la date de fin sont toutes les deux supérieures à la date d'aujourd'hui
    LocalDate today = LocalDate.now();
    if (startDate.isBefore(today) || endDate.isBefore(today)) {
        AlertUtils.showAlertFor3Seconds("Error", "Both the start and end dates must be later than today's date.", Alert.AlertType.ERROR);
        return;
    }
    // Calculer la durée en jours entre les deux dates
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    if (daysBetween <= 0) {
        AlertUtils.showAlertFor3Seconds("Error", "The reservation period must be at least one day.", Alert.AlertType.ERROR);
        return;
    }
            // Calculate the total price
            double totalPrice = house.getPricePerNight() * daysBetween;
            priceField.setText(String.format("%.2f DT", totalPrice));
        }
    }
    @FXML
    public void confirmReservation(ActionEvent event) {
        // Vérifiez si l'utilisateur est nul
        if (user == null) {
            AlertUtils.showAlertFor3Seconds("Error", "User is not logged in or initialized.", Alert.AlertType.ERROR);
            return; // Empêche la suite de l'exécution
        }
    
        // Vérifiez que les champs de date sont renseignés
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            AlertUtils.showAlertFor3Seconds("Error", "Please select both start and end dates.", Alert.AlertType.ERROR);
            return;
        }
    
        // Récupérer les dates sélectionnées
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
    
        // Vérifiez que la date de début est antérieure à la date de fin
        if (!startDate.isBefore(endDate)) {
            AlertUtils.showAlertFor3Seconds("Error", "The start date must be before the end date.", Alert.AlertType.ERROR);
            return;
        }
        // Vérifiez que la date de début et la date de fin sont toutes les deux supérieures à la date d'aujourd'hui
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today) || endDate.isBefore(today)) {
            AlertUtils.showAlertFor3Seconds("Error", "Both the start and end dates must be later than today's date.", Alert.AlertType.ERROR);
            return;
        }
        // Calculer la durée en jours entre les deux dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween <= 0) {
            AlertUtils.showAlertFor3Seconds("Error", "The reservation period must be at least one day.", Alert.AlertType.ERROR);
            return;
        }
        double totalPrice = house.getPricePerNight() * daysBetween;
        priceField.setText(String.format("%.2f DT", totalPrice));
        // Afficher les informations récupérées pour test
        logger.info("Start Date: " + startDate);
        logger.info("End Date: " + endDate);
        logger.info("House ID: " + house.getId());
        int x =0;
        try {
            // Créer une nouvelle réservation
            totalPrice = house.getPricePerNight() * daysBetween;
            Reservation reservHouse = new Reservation(
                house.getId(),
                user.getNom() + " " + user.getPrenom(),
                user.getEmail(),
                startDate,
                endDate,
                "House" + " " + house.getLocation(),
                String.valueOf(house.getId()),
                totalPrice,
                x
            );
    
            // Ajouter la réservation
            try {
                logger.info("Adding Reservation: " + reservHouse);
                Servicereservation.add(reservHouse);
    
                // Afficher un message de succès
                AlertUtils.showAlertFor3Seconds("Success", "Reservation confirmed successfully!", Alert.AlertType.INFORMATION);
                try {
                    // Créer un reçu PDF
                    String receiptPath = "recu_reservation_" + reservHouse.getId() + ".pdf";
                    PdfExportService pdfExportService = new PdfExportService();
                    pdfExportService.generateReservationReceipt(receiptPath, reservHouse);
            
                    // Envoyer un email avec le reçu en pièce jointe
                    EmailSender.sendReservationConfirmationEmail(user.getEmail(), reservHouse, receiptPath);
            
                    AlertUtils.showAlertFor3Seconds("Success", "Réservation confirmée et e-mail envoyé avec succès !", Alert.AlertType.INFORMATION);

                } catch (Exception e) {
                    AlertUtils.showAlertFor3Seconds("Error", "Erreur lors de la génération du reçu ou de l'envoi de l'e-mail.", Alert.AlertType.ERROR);
                    logger.error(e.toString());
                }
            } catch (Exception e) {
                AlertUtils.showAlertFor3Seconds("Error", "Failed to reserve the house.", Alert.AlertType.ERROR);
                logger.error("Error while adding reservation");
            }
    
        } catch (Exception e) {
            AlertUtils.showAlertFor3Seconds("Error", "Failed to create the reservation.", Alert.AlertType.ERROR);
            logger.error("Error while creating reservation");
        }
    
        house.setFullCapacity("Reserved");
        updateHouseInDatabase(house);
        
    
    }
    @FXML
    void BACK(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/HouseReservation.fxml"));
        Parent root = fxmlLoader.load();

        HouseReservationController houseReservationController = fxmlLoader.getController();
        houseReservationController.setUser(user);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("User Dashboard");
        stage.getScene().setRoot(root);
    }

    @FXML
    public void Exit(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        logger.info("Application closed.");
    }

    // Method for Confirm Button
    


private void updateHouseInDatabase(House house) {
    try {
        houseService.update(house);
    } catch (SQLException e) {
        //showError("Database Error", "Unable to update house: ");
    }
}

   
}
