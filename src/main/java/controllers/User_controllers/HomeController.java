package controllers.User_controllers;

import Entite.Personne;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Utilis.AlertUtils;
import Utilis.LogManager;

import java.io.IOException;

public class HomeController {
    LogManager logger = LogManager.getInstance();

    @FXML
    private Button btnLogout;
    
    @FXML
    private Label lblWelcome;
   
    @FXML
    private Label lblNom;
    
    @FXML
    private Label lblPrenom;
    
    @FXML
    private Label lblAddress;
    
    @FXML
    private Label lbEmail;

    private Personne user;

    public void setUserDetails(Personne user) {
        this.user = user;
        if (user != null) {
            lblWelcome.setText("Welcome, " + user.getPrenom() + "!");
            lblNom.setText("Name: " + user.getNom());
            lblPrenom.setText("LastName: " + user.getPrenom());
            lblAddress.setText("Address: " + user.getAddress());
            lbEmail.setText("Email: " + user.getEmail());
        } else {
            logger.error("Error: User details are null in HomeController!");
            //System.out.println("Error: User details are null in HomeController!");
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        logger.info("************* User is login out ************* ");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_FILES/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Login");
            stage.show();

            Stage currentStage = (Stage) lblWelcome.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            logger.error("Error : Failed to load the login Screen");
            AlertUtils.showAlert("Error", "Failed to load the login screen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void navigateToHotelReservation(MouseEvent event) {
        logger.info("********** Navigate to Hotel Reservartion Scene *********************");
        logger.info("Loading User also : " + user);
        navigateToScene("/FXML_FILES/HotelReservation.fxml", "Hotel Reservation", HotelReservationController.class);
    }

    @FXML
    private void navigateToCarRenting(MouseEvent event) {
        logger.info("******************* Navigate to CarRenting Scene ******************** ");
        navigateToScene("/FXML_FILES/CarRenting.fxml", "Car Renting", CarRentingController.class);
    }

    @FXML
    private void navigateToHouseReservation(MouseEvent event) {
        logger.info("********** Navigate to house Reservartion Scene *********************");
        navigateToScene("/FXML_FILES/HouseReservation.fxml", "House Reservation", HouseReservationController.class);
    }
    @FXML
    private void navigateToFlightBooking(MouseEvent event) {
        logger.info(" ****************** Navigate to Flight booking Scene ****************** ");
        navigateToScene("/FXML_FILES/FlightBooking.fxml", "Flight Booking", FlightBookingController.class);
    }

    @FXML
    private void navigateToOffers(MouseEvent event) {
        logger.info("******************* Navigate to special offers Scene **************** ");
        navigateToScene("/FXML_FILES/Offers.fxml", "Special Offers", OffersController.class);
    }

    // Helper method to load a scene and pass user details to the controller
    private <T> void navigateToScene(String fxmlFilePath, String title, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            // Get controller instance and set user details
            T controller = loader.getController();
            if (controller instanceof HotelReservationController) {
                ((HotelReservationController) controller).setUser(user);
            } else if (controller instanceof CarRentingController) {
                ((CarRentingController) controller).setUser(user);
            } else if (controller instanceof FlightBookingController) {
                ((FlightBookingController) controller).setUser(user);
            } else if (controller instanceof OffersController) {
                ((OffersController) controller).setUser(user);
            } else if (controller instanceof HouseReservationController) {
                ((HouseReservationController) controller).setUser(user);
            } else {
                logger.info("Error : Unsupported Controller type : did you fogot to add the scene into HOME CONTROLLER ?");
                // System.out.println("Error: Unsupported controller type.");
            }

            // Switch to the new scene
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle(title);
        } catch (IOException e) {
            AlertUtils.showAlert("Error", "Failed to load the screen: " + title, Alert.AlertType.ERROR);
            logger.error("Error loading the scene" + e.getMessage() + e );
        }
    }
}
