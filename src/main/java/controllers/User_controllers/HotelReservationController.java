package controllers.User_controllers;

import Entite.Hotel;
import Entite.Personne;
import Entite.Reservation;
import Services.Service_Hotels;
import Services.Service_Reservation;
import Utilis.AlertUtils;
import Utilis.LogManager;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
//import javax.mail.*;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.Node;  // Make sure you have this import
import javafx.util.Duration;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class HotelReservationController {
    LogManager logger = LogManager.getInstance();

    @FXML
    private ComboBox<String> HotelSelectionComboBox;

    @FXML
    private ImageView hotelImageView;  // ImageView to display the hotel image

    // Add a mapping to associate displayed strings with actual Hotel objects
    private Map<String, Hotel> hotelMap = new HashMap<>();

    @FXML
    private ComboBox<String> PaymentTypeComboBox;

    @FXML
    private DatePicker DateStartHotelReservation;  // Start Date

    @FXML
    private DatePicker DateFinHotelReservation;   // End Date

    @FXML
    private Label NumberOfDate;

    @FXML
    private Label DateFinHotelReservationLabel;

    @FXML
    private Label DateStartHotelReservationLabel;

    @FXML
    private Label TotalePrice;

    @FXML
    private Label paymenttype;

    @FXML
    private Label totalrooms;

    @FXML
    private TextField txtnumberofrooms;

    @FXML
    private Button logout;

    @FXML
    private Button showmyReservations;

    @FXML
    private HBox starsContainer; // Container for the stars
    
    private Scene rootScene;

    private Personne user;
    
    private final Service_Hotels hotelService = new Service_Hotels();
    private final Service_Reservation Servicereservation = new Service_Reservation();


    private String filledStarImageUrl = "https://png.pngtree.com/element_pic/00/16/07/1457877a2a7e3b6.jpg";
    private String emptyStarImageUrl = "https://png.pngtree.com/png-clipart/20190705/original/pngtree-vector-star-icon-png-image_4187383.jpg";

    public void setUser(Personne user) {
        if (user == null) {
            logger.warn("Warning: User is null when set in HotelReservation Scene");
        }
        this.user = user;
    }

    // Setter to inject the root home scene
    public void setRootScene(Scene rootScene) {
        this.rootScene = rootScene;
    }

    @FXML
    public void initialize() {
        logger.info("Starting loading Hotel Reservation Controller");
        logger.info("");
        logger.info("The User used for this controller is : " + user);
        logger.warn("");
        
        try {
            // Fetch all hotels from the database
            List<Hotel> hotels = hotelService.getAll();
            logger.info("List of hotels: " + hotels);
    
            // Clear the ComboBox items
            HotelSelectionComboBox.getItems().clear();
            hotelMap.clear();
    
            // Filter hotels with available rooms and add their names and locations to the ComboBox
            hotels.stream()
                .filter(hotel -> hotel.getRoomsAvailable() >= 1) // Hotels with available rooms
                .forEach(hotel -> {
                    String hotelInfo = hotel.getName() + " - " + hotel.getLocation() + " - " + hotel.getPricePerNight() + " DT";
                    hotelMap.put(hotelInfo, hotel); // Map displayed string to Hotel object
                    HotelSelectionComboBox.getItems().add(hotelInfo); // Add to ComboBox
                });
    
            // Populate PaymentTypeComboBox
            ObservableList<String> reservationOptions = FXCollections.observableArrayList("with card", "Cash");
            PaymentTypeComboBox.setItems(reservationOptions);
    
            // Add listeners for DatePickers
            DateStartHotelReservation.valueProperty().addListener((observable, oldValue, newValue) -> updateDateAndPrice());
            DateFinHotelReservation.valueProperty().addListener((observable, oldValue, newValue) -> updateDateAndPrice());
    
            // Add listener for number of rooms text field to update total price
            txtnumberofrooms.textProperty().addListener((observable, oldValue, newValue) -> updateDateAndPrice()); // Ensure price updates when rooms change
            // Set Label to handle default value of room 
            // totalrooms.setText("Total Rooms : 1");

            // init image view
            displayHotelImage("https://png.pngtree.com/png-clipart/20190705/original/pngtree-hotel-icon-for-personal-and-commercial-use-png-image_4341927.jpg");

        } catch (SQLException e) {
            logger.error("Failed to load hotels: " + e.getMessage());
            AlertUtils.showAlert("Initialization Error", "Failed to load Hotels: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    
    private void updateDateAndPrice() {
        LocalDate startDate = DateStartHotelReservation.getValue();
        LocalDate endDate = DateFinHotelReservation.getValue();
    
        // Only proceed if both start and end dates are selected
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                AlertUtils.showAlertFor3Seconds("Invalid Dates", "End date must be after the start date.", Alert.AlertType.WARNING);
                return;
            }
    
            // Calculate the number of days between start and end dates
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            NumberOfDate.setText("Total Days to Rent: " + daysBetween);
    
            // Get the selected hotel information
            String selectedHotelInfo = HotelSelectionComboBox.getValue();
            if (selectedHotelInfo == null) {
                AlertUtils.showAlertFor3Seconds("Error", "Please select a hotel.", Alert.AlertType.WARNING);
                return;
            }
    
            Hotel selectedHotel = hotelMap.get(selectedHotelInfo);
            if (selectedHotel == null) {
                AlertUtils.showAlertFor3Seconds("Error", "Selected hotel is not valid.", Alert.AlertType.ERROR);
                return;
            }
    
            try {
                // Get the price per night of the selected hotel
                double dailyPrice = selectedHotel.getPricePerNight();
    
                // Get the number of rooms from the input text field
                String number_of_rooms = txtnumberofrooms.getText();
                int rooms = 1;  // Default to 1 room
    
                if (!number_of_rooms.isEmpty()) {
                    try {
                        rooms = Integer.parseInt(number_of_rooms); // Parse the number of rooms entered
                        totalrooms.setText("Total Rooms: " + rooms);  // Update the label with the rooms value
                    } catch (NumberFormatException e) {
                        logger.error("Invalid input for number of rooms: " + number_of_rooms);
                        rooms = 1;  // Default to 1 if the input is invalid
                        AlertUtils.showAlert("Invalid Input", "Please enter a valid number of rooms.", Alert.AlertType.ERROR);
                    }
                }
    
                // Calculate the total cost based on the number of rooms and the number of days
                double totalCost = daysBetween * dailyPrice * rooms;
                TotalePrice.setText("Total Price: " + totalCost + " DNT"); // Update the total price label
    
            } catch (Exception e) {
                AlertUtils.showAlertFor3Seconds("Error", "Failed to calculate total cost.", Alert.AlertType.ERROR);
                logger.error("Error calculating total cost: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
        
    @FXML
    void DateStartHotelReservation(ActionEvent event) {
        // Retrieve the selected start date
        // Retrieve the selected start date
        LocalDate startDate = DateStartHotelReservation.getValue();
        if (startDate != null) {
            if (startDate.isBefore(LocalDate.now())) {
                // Reset the DatePicker value
                logger.info("local date is : " + LocalDate.now());
                logger.info("Start Date selected is :" + startDate);
                DateStartHotelReservation.setValue(null);

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

    private void updateStarsDisplay(int stars) {
        // Run the update in a separate thread for non-UI tasks
        new Thread(() -> {
            // All UI updates should be handled on the JavaFX Application thread
            Platform.runLater(() -> {
                // Clear the previous stars (this should be done on the FX thread)
                starsContainer.getChildren().clear(); 
    
                // Create and add the new stars
                for (int i = 0; i < 6; i++) {
                    ImageView starImageView = new ImageView();
    
                    if (i < stars) {
                        starImageView.setImage(new Image(filledStarImageUrl)); // Filled star
                    } else {
                        starImageView.setImage(new Image(emptyStarImageUrl)); // Empty star
                    }
    
                    // Resize the star image to fit inside the HBox
                    starImageView.setFitWidth(40); // Set fixed width (e.g., 40px)
                    starImageView.setFitHeight(40); // Set fixed height (e.g., 40px)
                    starImageView.setPreserveRatio(true); // Preserve aspect ratio
    
                    // Add the ImageView to the stars container (UI update)
                    starsContainer.getChildren().add(starImageView);
                }
    
                // Optionally, add animation if you want, on the JavaFX thread
                animateStars();
            });
        }).start(); // Start the background thread
    }
    
    private void animateStars() {
        // Animation should happen on the JavaFX Application Thread
        Platform.runLater(() -> {
            for (Node star : starsContainer.getChildren()) {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), star);
                scaleTransition.setFromX(0.5);
                scaleTransition.setFromY(0.5);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.setCycleCount(1);
                scaleTransition.setAutoReverse(false);
                scaleTransition.play();
            }
        });
    }
    
    @FXML
    void onPaymentTypeComboBox(ActionEvent event) {
        String getpaymentType = PaymentTypeComboBox.getValue();
        paymenttype.setText("Payment Type : " + getpaymentType );
    }

    @FXML
    void onHotelSelectionChanged(ActionEvent event) {
        String selectedHotelInfo = HotelSelectionComboBox.getValue(); // Get the selected hotel info (name + location)
    
        if (selectedHotelInfo != null) {
            // Look up the actual Hotel object using the selected hotel info as the key
            Hotel selectedHotel = hotelMap.get(selectedHotelInfo);
    
            if (selectedHotel != null) {
                String hotelImageUrl = selectedHotel.getImageUrl(); // Get the image URL from the selected hotel
                logger.info("Selected hotel image URL: " + hotelImageUrl);
    
                // Run displayHotelImage in a separate thread
                new Thread(() -> {
                    displayHotelImage(hotelImageUrl); // This will run in a separate thread
                }).start();
    
                // Run updateStarsDisplay in a separate thread
                new Thread(() -> {
                    updateStarsDisplay(selectedHotel.getStars()); // This will also run in a separate thread
                }).start();
            }
        }
    }
    

    // This method displays the image in the ImageView
    public void displayHotelImage(String hotelImageUrl) {
        // Run the image loading operation in a background thread
        new Thread(() -> {
            try {
                if (hotelImageUrl == null || hotelImageUrl.isEmpty()) {
                    logger.warn("Hotel image URL is null or empty");
                    Platform.runLater(() -> {
                        hotelImageView.setImage(new Image("https://png.pngtree.com/png-clipart/20190705/original/pngtree-hotel-icon-for-personal-and-commercial-use-png-image_4341927.jpg")); // Default image
                    });
                    return;
                }
                logger.info("Loading image from URL: " + hotelImageUrl);
                Image image = new Image(hotelImageUrl); // Load image from URL
                
                // Set the image to ImageView on the JavaFX Application Thread
                Platform.runLater(() -> {
                    hotelImageView.setImage(image); // Set the image to ImageView
                });
            } catch (Exception e) {
                logger.error("Failed to load image from URL: " + hotelImageUrl + e);
                Platform.runLater(() -> {
                    hotelImageView.setImage(new Image("https://png.pngtree.com/png-clipart/20190705/original/pngtree-hotel-icon-for-personal-and-commercial-use-png-image_4341927.jpg")); // Default image
                });
            }
        }).start();
    }

    @FXML
    void DateFinHotelReservation(ActionEvent event) {
        LocalDate startDate = DateStartHotelReservation.getValue();
        LocalDate endDate = DateFinHotelReservation.getValue();
    
        if (startDate == null || endDate == null) {
            AlertUtils.showAlertFor3Seconds("Invalid Dates", "Both start and end dates must be selected.", Alert.AlertType.WARNING);
            return;
        }
    
        if (endDate.isBefore(startDate)) {
            AlertUtils.showAlertFor3Seconds("Invalid Dates", "End date must be after the start date.", Alert.AlertType.WARNING);
            return;
        }
    
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        NumberOfDate.setText("Total Days to Rent: " + daysBetween);
    
        String selectedHotelInfo = HotelSelectionComboBox.getValue();
        if (selectedHotelInfo == null) {
            AlertUtils.showAlertFor3Seconds("Error", "Please select a hotel.", Alert.AlertType.WARNING);
            return;
        }
    
        Hotel selectedHotel = hotelMap.get(selectedHotelInfo);
        if (selectedHotel == null) {
            AlertUtils.showAlertFor3Seconds("Error", "Selected hotel is not valid.", Alert.AlertType.ERROR);
            return;
        }
    
        try {
            double dailyPrice = selectedHotel.getPricePerNight();
            double totalCost = daysBetween * dailyPrice;
            TotalePrice.setText("Total Price: " + totalCost + " DNT");
        } catch (Exception e) {
            AlertUtils.showAlertFor3Seconds("Error", "Failed to calculate total cost.", Alert.AlertType.ERROR);
            logger.error("Error calculating total cost: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void Reservehotel(ActionEvent event) {
        String selectedHotelInfo = HotelSelectionComboBox.getValue();
        LocalDate startDate = DateStartHotelReservation.getValue();
        LocalDate endDate = DateFinHotelReservation.getValue();
        String number_of_rooms = txtnumberofrooms.getText();  // Get the number of rooms as text
        int rooms = 1; // Default value for rooms
    
        try {
            rooms = Integer.parseInt(number_of_rooms);  // Try to parse the input as an integer
            if (rooms <= 0) {
                throw new NumberFormatException(); // Ensure positive values for rooms
            }
        } catch (NumberFormatException e) {
            // Handle invalid input gracefully
            AlertUtils.showAlert("Input Error", "Please enter a valid number for rooms.", Alert.AlertType.ERROR);
            return; // Exit the method if the input is invalid
        }
    
        if (selectedHotelInfo != null && startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                AlertUtils.showAlertFor3Seconds("Invalid Dates", "End date must be after the start date.", Alert.AlertType.WARNING);
                return;
            }
    
            Hotel selectedHotel = hotelMap.get(selectedHotelInfo);
            if (selectedHotel == null) {
                AlertUtils.showAlertFor3Seconds("Error", "Selected hotel is no longer available.", Alert.AlertType.ERROR);
                return;
            }
    
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            double totalCost = daysBetween * selectedHotel.getPricePerNight();
            TotalePrice.setText("Total Price: " + totalCost);
    
            // Ask for payment type
            String paymentType = PaymentTypeComboBox.getValue();
    
            if (paymentType == null) {
                AlertUtils.showAlertFor3Seconds("Payment Type", "Please select a payment type.", Alert.AlertType.WARNING);
                return;
            }
    
            // Proceed with rental logic (saving details, updating availability, etc.)
            try {
    
                // Check room availability
                if (selectedHotel.getRoomsAvailable() >= rooms) {
                    // Only update if there are enough available rooms
                    selectedHotel.setRoomsAvailable(selectedHotel.getRoomsAvailable() - rooms);
                    selectedHotel.setreserved_by_our_agency(selectedHotel.getReserved_by_our_agency() + rooms);
                    Reservation reserveHotel = new Reservation(
                        user.getNom() + " " + user.getPrenom(),
                        user.getEmail(),
                        startDate,
                        endDate,
                        "Hotel :" + selectedHotel.getName() + selectedHotel.getLocation() ,
                        String.valueOf(selectedHotel.getId()),
                        totalCost,
                        rooms
                    );
                        
                    hotelService.update(selectedHotel);

                    try {
                        logger.info("Adding Hotel to Reservation Table :  " + reserveHotel);
                        Servicereservation.add(reserveHotel);
                    } catch (SQLException e) {
                        AlertUtils.showAlertFor3Seconds("Error", "Failed to reserve the car.", Alert.AlertType.ERROR);
                        e.printStackTrace();
                    }
                    
                    String pdfContent = generatePDFContent(selectedHotel, startDate, endDate, daysBetween, totalCost, paymentType , rooms);
                    saveReservationAsPDF(event, pdfContent);
                    
                    resetForm();
                    
                    AlertUtils.showAlertFor3Seconds("Reservation Confirmed", "Reservation details saved successfully.", Alert.AlertType.INFORMATION);
                } else {
                    // Not enough rooms available
                    AlertUtils.showAlertFor3Seconds("Not Enough Rooms", "There are not enough rooms available for this reservation.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                AlertUtils.showAlertFor3Seconds("Error", "Failed to update hotel rooms availability.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            AlertUtils.showAlertFor3Seconds("Missing Information", "Please make sure all fields are filled correctly.", Alert.AlertType.WARNING);
        }
    }
    

    private String generatePDFContent(Hotel hotel, LocalDate startDate, LocalDate endDate, long daysBetween, double totalCost, String paymentType , int rooms ) {
        return "Hotel Reservation Confirmation\n" +
                "-------------------------\n" +
                "Customer Name: Mr(s). " + user.getNom() + " " + user.getPrenom() + "\n" +
                "Hotel Details:\n" +
                "  - Name: " + hotel.getName() + "\n" +
                "  - Location: " + hotel.getLocation() + "\n" +
                "  - Price per night: " + hotel.getPricePerNight() + "\n" +
                "Rental Period: " + startDate + " to " + endDate + " (" + daysBetween + " days)\n" +
                "The Rooms Reserved are : " + rooms + "rooms \n"+
                "Total Cost: " + totalCost + " DNT\n" +
                "Payment Type: " + paymentType;
    }

    private void saveReservationAsPDF(ActionEvent event, String pdfContent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Reservation Confirmation");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File saveFile = fileChooser.showSaveDialog(((Button) event.getSource()).getScene().getWindow());

        if (saveFile != null) {
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fos);
                document.open();
                document.add(new Paragraph(pdfContent));
                document.close();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                AlertUtils.showAlert("Error", "Failed to save the PDF file.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/Login.fxml"));
        Parent root = fxmlLoader.load();

        // Switch back to the home scene
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login");
        stage.getScene().setRoot(root);
    }

    @FXML
    void showmyReservations(ActionEvent event) throws IOException {
        // --> Move to My Reservation History Scene <--
        logger.info("Trigger show My Reservation History ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/hotelreservationhistory.fxml"));
        Parent root = fxmlLoader.load();
    
        // After loading FXML, get the controller
        HotelReservationHistory hotelreservationhistory = fxmlLoader.getController();
    
        // Set user in the controller (this is where the user is passed)
        if (user != null) {
            hotelreservationhistory.setUser(user);
            logger.info("Setting User Info for Home Scene for user : " + user);
        } else {
            logger.error("Error: User is null in HotelReservationController !");
        }
    
        // Now, you can safely log the user after the switch
        logger.info("User object after switching scene: " + user);
    
        // Switch to the new scene
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Hotel Reservation History");
        stage.setWidth(900);
        stage.setHeight(700);
        stage.getScene().setRoot(root);
    }

    @FXML
    void BACKHOME(ActionEvent event) throws IOException {
        logger.info("Move Back to Home Scene");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/home.fxml"));
        Parent root = fxmlLoader.load();
        HomeController homeController = fxmlLoader.getController();
        if (user != null) {
            homeController.setUserDetails(user);
            logger.info("Setting User Info for Home Scene for user : " + user);
        } else {
            logger.error("Error: User is null in HotelReservationController !");
        }
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("User Dashboard");
        stage.getScene().setRoot(root);
    }


    private void resetForm() {
        // Clear ComboBoxes
        HotelSelectionComboBox.getItems().clear();
        // HotelSelectionComboBox.setPromptText("Select a Hotel for Reservation");
        // HotelSelectionComboBox.setValue("Select a Hotel for Reservation");
        // HotelSelectionComboBox.getItems().set();
        PaymentTypeComboBox.getSelectionModel().clearSelection();
        // PaymentTypeComboBox.setPromptText("Payment Type :");
        // PaymentTypeComboBox.setValue("Payment Type :");
        PaymentTypeComboBox.getSelectionModel().selectFirst();

        // Reset DatePickers
        DateStartHotelReservation.setValue(null);
        DateFinHotelReservation.setValue(null);

        // Reset Labels
        NumberOfDate.setText("Total Days to Rent: ");
        TotalePrice.setText("Total Price: ");
        totalrooms.setText("Total Rooms : 1");

        txtnumberofrooms.clear();

        // Reinitialize car list
        initialize();
    }

}
