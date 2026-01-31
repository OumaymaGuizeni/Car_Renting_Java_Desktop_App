package controllers.Admin_controllers;

import java.io.IOException;

import Utilis.AlertUtils;
import Utilis.LogManager;
import controllers.User_controllers.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class AdminDashboardController {

    LogManager logger = LogManager.getInstance();

    @FXML
    private TextField txtEmail;
    @FXML
    void MangeUsers(ActionEvent event) throws IOException {
        logger.info("************ Going to manage Users  ManageUsers  ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/manage_users.fxml"));
        Parent root = fxmlLoader.load();
        // Get the current scene dynamically without using txtID
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        ManageUsersController manageUsersController = fxmlLoader.getController();
    }

    @FXML
    void MangingCars(ActionEvent event) throws IOException {
        logger.info("************ Going to managecars now  ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/ManageCars.fxml"));
        Parent root = fxmlLoader.load();
        // Get the current scene dynamically without using txtID
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        ManageCarsController rentCarController = fxmlLoader.getController();
    }

    @FXML
    void MangeHotels(ActionEvent event) throws IOException {
        logger.info("************ Going to MangeHotels now  ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/manage_hotels.fxml"));
        Parent root = fxmlLoader.load();
        // Get the current scene dynamically without using txtID
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        ManageHotelsController manageHotelsController = fxmlLoader.getController();
    }
    @FXML
    void ManageFlights(ActionEvent event)throws IOException {
        logger.info("************ Going to ManageFlights now  ********************** ");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/Manage_flights.fxml"));
            Parent root = fxmlLoader.load();
            // Get the current scene dynamically without using txtID
            ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
            ManageFlightController ManageFlightController = fxmlLoader.getController();
        }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        logger.info("************ Admin is logging Out  ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/Login.fxml"));
        Parent root = fxmlLoader.load();

        // Get the current scene dynamically without using txtID
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene newScene = new Scene(root, 900, 700); // Specify the new width and height
        currentStage.setScene(newScene);
        // Reset the title to "Login"
        currentStage.setTitle("Login");
        currentStage.centerOnScreen();
        LoginController loginController = fxmlLoader.getController();
    }


    @FXML
    void Mangespecialoffers(ActionEvent event) throws IOException {
        logger.info("************ Going to Manage Special Offers now ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/ManageSpecialOffers.fxml"));
        Parent root = fxmlLoader.load();
        // Navigate to the new scene
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        ManageSpecialOffersController manageSpecialOffersController = fxmlLoader.getController();
    }
        public void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void MangeHouses(ActionEvent event) throws IOException {
        logger.info("************ Going to ManageHouses now  ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/manage_houses.fxml"));
        Parent root = fxmlLoader.load();
        // Get the current scene dynamically without using txtID
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        ManageHousesController manageHousesController = fxmlLoader.getController();
    }
    
        @FXML
    void MangeReservations(ActionEvent event) throws IOException {
        logger.info("************ Going to manage Users  Manage Reservations  ********************** ");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/ManagerReservation.fxml"));
        Parent root = fxmlLoader.load();
        // Get the current scene dynamically without using txtID
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        ManagerReservation managerReservation = fxmlLoader.getController();
    }


    public void handleHelp(ActionEvent event) {
        String Title = "Help Menu ";
        String Content = "Here is some helpfull information about logging in \n\n"
        +   "1. Enter your email address.\\n"
        +   "2. Enter your Pass.\n"
        +   "3. Click 'Login' to access your account.\n"
        +   "4. If you are new, click 'Create New User' to register.\n\n"
        +   "For more help, contact support.";
        AlertUtils.showAlert(Title, Content, Alert.AlertType.INFORMATION);
        txtEmail.requestFocus();
    }

}