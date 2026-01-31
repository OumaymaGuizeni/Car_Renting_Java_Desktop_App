package controllers.User_controllers;

import Entite.Flight;
import Entite.Hotel;
import Entite.Personne;
import Entite.Reservation;
import Services.Service_Flight;
import Services.Service_Reservation;
import Utilis.AlertUtils;
import Utilis.LogManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
public class FlightBookingController {
    LogManager logger = LogManager.getInstance();

    @FXML
    private TableColumn<?, ?> DateOfRent;

    @FXML
    private DatePicker DateRentFlight;

    @FXML
    private ComboBox<Flight> FlightSelectionComboBox;

    @FXML
    private ComboBox<String> PaymentType;

    @FXML
    private TableView<Reservation> Tablereservation;

    @FXML
    private TableColumn<Flight, Integer> TotaleCost;

    @FXML
    private Label TotalePrice;

    @FXML
    private TextField nombreOfSeats;

    @FXML
    private TableColumn<Flight, String> Type_Reservation;

    @FXML
    private Button btnLogout;
    @FXML
    private TableColumn<Flight, Integer> NombreOfSeatsReserved;


    private Map<String, Flight> flightMap = new HashMap<>();
    private Scene rootScene;
    private Personne user;

    private final Service_Flight serviceFlight = new Service_Flight();
    private final Service_Reservation serviceReservation = new Service_Reservation();

    public void setUser(Personne user) {
        if (user == null) {
            logger.warn("Warning: User is null when set in FlightBookingController");
        }
        this.user = user;
    }

    @FXML
    public void initialize() {
        PaymentType.setPromptText("Payment Type");
        try {
            List<Flight> flights = serviceFlight.getAll();
            FlightSelectionComboBox.getItems().clear();
            flightMap.clear();

            flights.stream()
                    .filter(flight -> flight.getSeatsAvailable() > 0)
                    .forEach(flight -> {
                        String flightInfo = "Departure: " + flight.getDeparture() + " to " + flight.getArrival() +
                                ", Price: " + flight.getPrice() + " DNT";
                        flightMap.put(flightInfo, flight);
                        FlightSelectionComboBox.getItems().add(flight);
                    });

            logger.info("Flights loaded successfully. Available flights: " + flights.size());

        } catch (SQLException e) {
            logger.error("Failed to load flights: " + e.getMessage());
            AlertUtils.showAlert(
                    "Initialization Error",
                    "Failed to load available flights. Please try again later.",
                    Alert.AlertType.ERROR
            );
        }

        PaymentType.setItems(FXCollections.observableArrayList("Credit Card", "Cash"));

        // Add listener to dynamically calculate total cost and validate seats
        nombreOfSeats.textProperty().addListener((observable, oldValue, newValue) -> {
            Flight selectedFlight = FlightSelectionComboBox.getSelectionModel().getSelectedItem();

            if (selectedFlight != null) {
                try {
                    int requestedSeats = Integer.parseInt(newValue);

                    if (requestedSeats <= 0) {
                        TotalePrice.setText("Enter a positive number of seats.");
                    } else if (requestedSeats > selectedFlight.getSeatsAvailable()) {
                        TotalePrice.setText("Not enough seats available.");
                    } else {
                        double totalCost = requestedSeats * selectedFlight.getPrice();
                        TotalePrice.setText(String.format("Total Price: %.2f DNT", totalCost));
                    }
                } catch (NumberFormatException e) {
                    TotalePrice.setText("Invalid input. Please enter a number.");
                }
            } else {
                TotalePrice.setText("Please select a flight first.");
            }
        });


        logger.info("Flight Booking Controller initialization completed");
    }


    @FXML
    void DateRentFlight(ActionEvent event) {
        LocalDate startDate = DateRentFlight.getValue();
        if (startDate != null) {
            if (startDate.isBefore(LocalDate.now())) {
                DateRentFlight.setValue(null);
                logger.warn("Invalid Start Date Selected: " + startDate);
                AlertUtils.showAlert(
                        "Invalid Date",
                        "Start date cannot be earlier than today. Please select a valid date.",
                        Alert.AlertType.ERROR
                );
            } else {
                logger.info("Valid Start Date Selected: " + startDate);
            }
        }
    }

    @FXML
    void bookFlight(ActionEvent event) throws SQLException {
        Flight selectedFlight = FlightSelectionComboBox.getSelectionModel().getSelectedItem();

        // Validate if a flight is selected
        if (selectedFlight == null) {
            AlertUtils.showAlert("Flight Selection Error", "Please select a flight.", Alert.AlertType.ERROR);
            return;
        }
        // Validate payment type selection
        String selectedPaymentType = PaymentType.getSelectionModel().getSelectedItem();
        if (selectedPaymentType == null || selectedPaymentType.isEmpty()) {
            AlertUtils.showAlert("Payment Type Error", "Please select a payment type.", Alert.AlertType.ERROR);
            return;
        }

        // Validate the number of requested seats
        int requestedSeats;
        try {
            requestedSeats = Integer.parseInt(nombreOfSeats.getText());
            if (requestedSeats <= 0) {
                AlertUtils.showAlert("Invalid Input", "Please enter a positive number of seats.", Alert.AlertType.ERROR);
                return;
            } else if (requestedSeats > selectedFlight.getSeatsAvailable()) {
                AlertUtils.showAlert("Enough Seats Available Error", "Please enter a valid number of seats.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Invalid Input", "Please enter a valid number of seats.", Alert.AlertType.ERROR);
            return;
        }

        // Validate the date
        if (DateRentFlight.getValue() == null || DateRentFlight.getValue().isBefore(LocalDate.now())) {
            AlertUtils.showAlert("Invalid Date", "The selected date must be today or in the future.", Alert.AlertType.ERROR);
            return;
        }

        // Calculate total cost and update available seats
        LocalDate startDate = DateRentFlight.getValue();
        double totalCost = requestedSeats * selectedFlight.getPrice();
        TotalePrice.setText(String.format("Total Price: %.2f", totalCost));

        int newSeatsAvailable = selectedFlight.getSeatsAvailable() - requestedSeats;
        selectedFlight.setSeats_available(newSeatsAvailable);

        try{
            serviceFlight.update(selectedFlight);
        }catch (SQLException e) {
            AlertUtils.showAlert("Error", "Failed to update the flight.", Alert.AlertType.ERROR);
        }

        // Create the reservation object
        Reservation reservation = new Reservation(
                user.getNom() + " " + user.getPrenom(),
                user.getEmail(),
                startDate,
                startDate,
                "Flight: " + selectedFlight.getDeparture() + " -> " + selectedFlight.getArrival(),
                selectedFlight.getFlightId(),
                totalCost,
                requestedSeats
        );

        // Save the reservation to the database
        try {
            serviceReservation.add(reservation);
            AlertUtils.showAlert("Booking Successful", "Your flight has been booked successfully!", Alert.AlertType.INFORMATION);
            clear();
        } catch (SQLException e) {
            AlertUtils.showAlert("Error", "Failed to reserve the flight.", Alert.AlertType.ERROR);
        }
        initialize();
    }


    private void saveRentalDetailsAsPDF(String content, File file) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph(content));
            document.close();
            logger.info("PDF saved successfully at : " + file.getAbsolutePath());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to save the PDF file.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void PrintMyReservation(ActionEvent event) {
        try {
            Reservation selectedReservation = Tablereservation.getSelectionModel().getSelectedItem();
            if (selectedReservation == null) {
                AlertUtils.showAlert("No Selection", "Please select a reservation from the table.", Alert.AlertType.WARNING);
                return;
            }

            String pdfContent = "Reservation Confirmation N° Flight : " + selectedReservation.getId() + "\n" +
                    "--------------------------------------------" + "\n" +
                    "Customer Name: Mr. " + selectedReservation.getUserFullName() + "\n" +
                    "Flight ID: " + selectedReservation.getTypeId() + "\n" +
                    "Date of flight: " + selectedReservation.getDateDebut() + "\n" +
                    "nombre of seats :" + selectedReservation.getNbrOfSeatsOrRooms()+"\n"+
                    "Total Cost: " + selectedReservation.getReservationPrice() + " DNT\n";

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File saveFile = fileChooser.showSaveDialog(((Button) event.getSource()).getScene().getWindow());

            if (saveFile != null) {
                saveRentalDetailsAsPDF(pdfContent, saveFile);
                AlertUtils.showAlert(
                        "Rental Confirmed",
                        "The rental details have been saved as a PDF.",
                        Alert.AlertType.INFORMATION
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to save the PDF file.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void ShowMyreservation(ActionEvent event) {
        try {
            logger.info("Fetching reservations for user: " + user.getEmail());
            List<Reservation> list = serviceReservation.getReservationsByUser(user.getEmail());
            List<Reservation> flightReservations = list.stream()
                    .filter(reservation -> reservation.getType().startsWith("Flight"))
                    .collect(java.util.stream.Collectors.toList());

            ObservableList<Reservation> ober = FXCollections.observableArrayList(flightReservations);
            Tablereservation.setItems(ober);

            Type_Reservation.setCellValueFactory(new PropertyValueFactory<>("type"));
            DateOfRent.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
            TotaleCost.setCellValueFactory(new PropertyValueFactory<>("reservationPrice"));
            NombreOfSeatsReserved.setCellValueFactory(new PropertyValueFactory<>("nbrOfSeatsOrRooms"));


            logger.info("Flight reservations loaded successfully.");
        } catch (SQLException e) {
            logger.error("Failed to load reservations: " + e.getMessage());
            AlertUtils.showAlert("Initialization Error", "Failed to load reservations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clear() {
        // Reset the DatePicker
        DateRentFlight.setValue(null);

        // Reset the ComboBox selections
        FlightSelectionComboBox.getSelectionModel().clearSelection();

        // Clear the TextField for the number of seats
        nombreOfSeats.clear();

        // Reset the PaymentType ComboBox
        PaymentType.getSelectionModel().clearSelection();

        // Clear the total price label
        TotalePrice.setText("");

        // Reset the reservations table
        Tablereservation.getItems().clear();

        // Reinitialize the dashboard
    }


    @FXML
    void BACK(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/home.fxml"));
        Parent root = fxmlLoader.load();

        HomeController homeController = fxmlLoader.getController();
        if (user != null) {
            homeController.setUserDetails(user);
        } else {
            logger.error("Error: User is null in FlightBookingController!");
        }

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("User Dashboard");
        stage.getScene().setRoot(root);
    }

}
