package controllers.User_controllers;

import Entite.Car;
import Entite.Personne;
import Entite.Reservation;
import Services.Service_Cars;
import Services.Service_Reservation;
import Utilis.AlertUtils;
import Utilis.LogManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class CarRentingController {

    LogManager logger = LogManager.getInstance();

    @FXML
    private Label NumberOfDate;

    @FXML
    private Label TotalePrice;

    @FXML
    private ComboBox<Car> CarSelectionComboBox;  // Change to Car type

    @FXML
    private ComboBox<String> PaymentTypeComboBox;

    @FXML
    private DatePicker DateDebit;  // Start Date
    @FXML
    private DatePicker DateFin;   // End Date

    @FXML
    private TableColumn<Reservation, String> Type_Reservation;
    @FXML
    private TableView<Reservation> Tablereservation;
    @FXML
    private TableColumn<Reservation, LocalDate> Start_Date;
    @FXML
    private TableColumn<Reservation, LocalDate> End_Date;

    @FXML
    private TableColumn<Reservation, ?> TotaleCost;


    private final Service_Cars service = new Service_Cars();

    private final Service_Reservation Servicereservation = new Service_Reservation();

    private Personne user;

    public void setUser(Personne user) {
        if (user == null) {
            logger.warn("Warning: User is null when set in CarRentingController");
        }
        this.user = user;
    }

@FXML
public void initialize() {
    logger.info("Starting loading car Renting Controller");
    logger.info("User Used is : " + user);


    // Load available cars
    try {
        List<Car> list = service.getAll();
        list.stream()
                .filter(car -> car.getAvailable() == 1)  // Filter available cars
                .forEach(car -> CarSelectionComboBox.getItems().add(car));
        CarSelectionComboBox.getSelectionModel().selectFirst();
        PaymentTypeComboBox.setItems(FXCollections.observableArrayList("With Card", "Cash"));
        PaymentTypeComboBox.getSelectionModel().selectFirst();


    } catch (SQLException e) {
        AlertUtils.showAlert("Initialization Error", "Failed to load cars." + e.getMessage(), Alert.AlertType.ERROR);
    }
  }



    @FXML
    void DateDebit(ActionEvent event) {
        // Retrieve the selected start date
        LocalDate startDate = DateDebit.getValue();
        if (startDate != null) {
            if (startDate.isBefore(LocalDate.now())) {
                // Reset the DatePicker value
                DateDebit.setValue(null);

                // Log and alert the user about the invalid date
                logger.warn("Invalid Start Date Selected: " + startDate);
                AlertUtils.showAlert(
                        "Invalid Date",
                        "Start date cannot be earlier than today. Please select a valid date.",
                        Alert.AlertType.ERROR
                );
            } else {
                logger.info("Valid Start Date Selected: " + startDate);
            }
        } else {
            logger.info("Start Date not selected");
        }
    }

    @FXML
    void DateFin(ActionEvent event) {
        // Retrieve the selected end date
        LocalDate endDate = DateFin.getValue();
        if (endDate != null) {
            logger.info("End Date Selected" + endDate);
            // System.out.println("End Date Selected: " + endDate);
        } else {
            logger.info("End Date not selected");
            // System.out.println("End Date not selected");
        }

        // Validate end date > start date
        LocalDate startDate = DateDebit.getValue();
        if (startDate != null && endDate != null) {
            if (!endDate.isAfter(startDate.plusDays(0))) {
                // Reset the invalid end date
                DateFin.setValue(null);

                // Show alert to user
                AlertUtils.showAlertFor3Seconds(
                        "Invalid Dates",
                        "End date must be at least 1 day after the start date.",
                        Alert.AlertType.WARNING
                );
                logger.warn("End date is not at least 1 day after the start date.");
                return;
            }

            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            NumberOfDate.setText("Total Days to Rent: " + daysBetween);

            // Retrieve the selected car
            Car selectedCar = CarSelectionComboBox.getValue();
            if (selectedCar != null) {
                try {
                    double dailyPrice = selectedCar.getDailyPrice(); // Get the price as double
                    double totalCost = daysBetween * dailyPrice; // Calculate the total cost
                    TotalePrice.setText("Total Price: " + totalCost);
                } catch (Exception e) {
                    AlertUtils.showAlertFor3Seconds("Error", "Failed to calculate total cost.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void RentCar(ActionEvent event) {
        Car selectedCar = CarSelectionComboBox.getValue();
        LocalDate startDate = DateDebit.getValue();
        LocalDate endDate = DateFin.getValue();

        if (selectedCar != null && startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                AlertUtils.showAlertFor3Seconds("Invalid Dates", "End date must be after the start date.", Alert.AlertType.WARNING);
                return;
            }

            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            double totalCost = daysBetween * selectedCar.getDailyPrice();
            TotalePrice.setText("Total Price: " + totalCost);

            // Ask for payment type
            String paymentType = PaymentTypeComboBox.getValue();

            if (paymentType == null) {
                AlertUtils.showAlertFor3Seconds("Payment Type", "Please select a payment type.", Alert.AlertType.WARNING);
                return;
            }

            // Proceed with rental logic
            try {
                Reservation reservCar = new Reservation(
                        user.getNom() + " " + user.getPrenom(),
                        user.getEmail(),
                        startDate,
                        endDate,
                        "Car : "+selectedCar.getBrand() +" "+ selectedCar.getModel(),
                        String.valueOf(selectedCar.getId()),
                        totalCost,
                        0
                );

                selectedCar.setAvailable(0); // Mark car as rented
                service.update(selectedCar);

                try {
                    logger.info("Adding Car " + reservCar);
                    Servicereservation.add(reservCar);
                } catch (SQLException e) {
                    AlertUtils.showAlertFor3Seconds("Error", "Failed to reserve the car.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }




                // Refresh the page and clear all fields
                resetForm();

            } catch (SQLException e) {
                AlertUtils.showAlertFor3Seconds("Error", "Failed to update the car availability.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            AlertUtils.showAlertFor3Seconds("Missing Information", "Please make sure all fields are filled correctly.", Alert.AlertType.WARNING);
        }
    }

    private void saveRentalDetailsAsPDF(String content, File file) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph(content));
            document.close();
            logger.info("PDF saved successfully at : " + file.getAbsolutePath());
            //System.out.println("PDF saved successfully at: " + file.getAbsolutePath());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to save the PDF file.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void PrintMyReservation (ActionEvent event) {
        try {
            List<Reservation> listR = Servicereservation.getAll();
            Reservation selectedReservation = Tablereservation.getSelectionModel().getSelectedItem();

            if (selectedReservation == null) {
                AlertUtils.showAlert("No Selection", "Please select a reservation from the table.", Alert.AlertType.WARNING);
                return;
            }

            // Calculate the number of days between the start and end dates
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                    selectedReservation.getDateDebut(),
                    selectedReservation.getDateFin()
            );

            // Validate rental period
            if (daysBetween < 0) {
                AlertUtils.showAlert("Invalid Dates", "The start date cannot be after the end date.", Alert.AlertType.ERROR);
                return;
            }

            // Create the PDF content
            String pdfContent = "Rental Confirmation N° : Car " + selectedReservation.getId() + "\n" +
                    "-------------------------\n" +
                    "Customer Name: Mr. " + selectedReservation.getUserFullName() + "\n" +
                    "Car Details:\n" +
                    "  - " + selectedReservation.getType() + "\n" +
                    "Rental Period: " + selectedReservation.getDateDebut() + " to " + selectedReservation.getDateFin() +
                    " (" + daysBetween + " days)\n" +
                    "Total Cost: " + selectedReservation.getReservationPrice() + " DNT\n";

            // Prompt the user to choose a file location to save the PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Rental Confirmation");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File saveFile = fileChooser.showSaveDialog(((Button) event.getSource()).getScene().getWindow());

            if (saveFile != null) {
                saveRentalDetailsAsPDF(pdfContent, saveFile);
                AlertUtils.showAlertFor3Seconds(
                        "Rental Confirmed",
                        "The rental details have been saved as a PDF.",
                        Alert.AlertType.INFORMATION
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            AlertUtils.showAlert("Error", "Failed to save the PDF file.", Alert.AlertType.ERROR);
        }
    }



    @FXML
    void ShowMyreservation(ActionEvent event) {
        try {
            logger.info(" ********** Fetching reservations for user: " + user.getEmail() + " *****************************");
            // Fetch reservations only for the logged-in user
            List<Reservation> list = Servicereservation.getReservationsByUser(user.getEmail());
            List<Reservation> listCar = list.stream()
                    .filter(reservation -> reservation.getType().startsWith("Car"))
                    .collect(java.util.stream.Collectors.toList());
            ObservableList<Reservation> ober = FXCollections.observableArrayList(listCar);
            Tablereservation.setItems(ober);

            // Set up table columns

            Type_Reservation.setCellValueFactory(new PropertyValueFactory<>("type"));
            Start_Date.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
            End_Date.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
            TotaleCost.setCellValueFactory(new PropertyValueFactory<>("reservationPrice"));
        } catch (SQLException e) {
            AlertUtils.showAlert("Initialization Error", "Failed to load reservations. " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private void resetForm() {
        // Clear ComboBoxes
        CarSelectionComboBox.getItems().clear();
        PaymentTypeComboBox.getSelectionModel().clearSelection();

        // Reset DatePickers
        DateDebit.setValue(null);
        DateFin.setValue(null);

        // Reset Labels
        NumberOfDate.setText("Totale date to Rent");
        TotalePrice.setText("Total Price :");

        // Reinitialize car list
        initialize();
    }


    @FXML
    void BACK(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/home.fxml"));
        Parent root = fxmlLoader.load();

        // Pass user details back to HomeController
        HomeController homeController = fxmlLoader.getController();
        if (user != null) {
            homeController.setUserDetails(user);
        } else {
            logger.info("Error : User is null in CarRentingController ! User will be not definied in home scene ");
            //System.out.println("Error: User is null in CarRentingController!");
        }

        // Switch back to the home scene
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("User Dashboard");
        stage.getScene().setRoot(root);
    }
}
