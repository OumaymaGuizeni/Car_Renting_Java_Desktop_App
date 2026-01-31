
package controllers.User_controllers;

import Entite.LocationInfo;
import javafx.scene.control.Label; // For JavaFX Label
import Entite.Personne;
import Services.EmailSender;
import Services.LocationService;
import Services.Service_Personnes;
import Utilis.AlertUtils;
import Utilis.LogManager;
import Utilis.SecureRandomPasscodeGenerator;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

// import javax.swing.text.html.parser.Entity;

public class CreateUserController {
    @FXML
    private Label timezoneLabel;
    @FXML
    private Label latLabel;

    @FXML
    private Label lonLabel;

    @FXML
    private PasswordField ConfirmePassword;
    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPrenom;

    @FXML
    private TextField txtCIN;

    @FXML
    private TextField txtAddress;

    @FXML
    private Button openMapButton;

    @FXML
    private Button CreateAccount;

    @FXML
    private TextField txtEmail;

    @FXML
    private Button EXIT;

    private final Service_Personnes servicePersonnes = new Service_Personnes();
    private final LocationService locationService = new LocationService();
    private final LogManager logger = LogManager.getInstance();

    public void initialize() {
        setupRealTimeValidation();
        
        // Automatically update the location during initialization
        try {
            LocationInfo locationInfo = locationService.getLocationInfo();
            updateLocationLabels(locationInfo);
        } catch (Exception e) {
            timezoneLabel.setText("Error fetching data");
            latLabel.setText("");
            lonLabel.setText("");
        }
    }
    
    private void setupRealTimeValidation() {
        // Name validation (not empty)
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("[A-Za-z ]+")) {
                txtName.setStyle("-fx-border-color: red;");
            } else {
                txtName.setStyle(null);
            }
        });

        // Prenom validation (not empty)
        txtPrenom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("[A-Za-z ]+")) {
                txtPrenom.setStyle("-fx-border-color: red;");
            } else {
                txtPrenom.setStyle(null);
            }
        });

        // Email validation
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
                // Invalid email format
                txtEmail.setStyle("-fx-border-color: red;");
            } else {
                // Valid email format
                try {
                    if (servicePersonnes.isEmailExists(newValue)) {
                        // Email exists in the database
                        txtEmail.setStyle("-fx-border-color: red;");

                        // Show alert
                        AlertUtils.showAlert(
                                "Email Exists",
                                "The email address is already registered. Please use a different email.",
                                Alert.AlertType.ERROR
                        );

                        // Defer clearing the text field to avoid conflict
                        javafx.application.Platform.runLater(() -> {
                            txtEmail.clear();
                        });
                    } else {
                        // Email is valid and does not exist
                        txtEmail.setStyle(null);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertUtils.showAlert(
                            "Database Error",
                            "An error occurred while checking the email. Please try again later.",
                            Alert.AlertType.ERROR
                    );
                }
            }
        });


        // CIN validation (exactly 8 digits)
        txtCIN.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{8,}")) { // Match 8 or more digits
                txtCIN.setStyle("-fx-border-color: red;");
            } else {
                txtCIN.setStyle(null);
            }
        });

        // confirm Password
        // CIN validation (exactly 8 digits)
        ConfirmePassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{8,}")) { // Match 8 or more digits
                ConfirmePassword.setStyle("-fx-border-color: red;");
            } else {
                ConfirmePassword.setStyle(null);
            }
        });

        // Address validation (not empty)
        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                txtAddress.setStyle("-fx-border-color: red;");
            } else {
                txtAddress.setStyle(null);
            }
        });


    }

    @FXML
    private void handleCreateUser(ActionEvent event) {
        logger.info("User Clicked on Create User, Start the checks");
        try {
            String name = txtName.getText();
            String prenom = txtPrenom.getText();
            String cin = txtCIN.getText();
            String address = txtAddress.getText();
            String email = txtEmail.getText();

            // Validation
            if (name.isEmpty() || prenom.isEmpty() || cin.isEmpty() || address.isEmpty() || email.isEmpty()) {
                AlertUtils.showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
                return;
            }

            if (!txtName.getStyle().isEmpty() || !txtPrenom.getStyle().isEmpty() || !(txtEmail.getStyle().isEmpty())
                    || !txtCIN.getStyle().isEmpty() || !txtAddress.getStyle().isEmpty()) {
                AlertUtils.showAlert("Error", "Please correct the invalid fields!", Alert.AlertType.ERROR);
                return;
            }

            // Check if CIN and ConfirmePassword match
            String confirmePassword = ConfirmePassword.getText();
            if (!cin.equals(confirmePassword)) {
                AlertUtils.showAlert("Error", "Password and Confirm Password must be the same!", Alert.AlertType.ERROR);
                return;
            }

            // The ID will be automatically set by the Service_Personnes class
            Personne newPerson = new Personne(name, prenom, cin, address, email);
            servicePersonnes.add(newPerson);

            String currentDirectory = System.getProperty("user.dir");
            String attachmentPath = currentDirectory + "/src/main/resources/src/logo.png";

            // Generate a secure random passcode
            String passcode = SecureRandomPasscodeGenerator.generateRandomPasscode();

            // Send the email with the passcode
            EmailSender.sendEmailWelcomeAddUser(email, passcode, attachmentPath);

            // Show alert for successful user creation
            AlertUtils.showAlert("Success", "User created successfully! Check your email for the passcode.", Alert.AlertType.INFORMATION);

            // Popup for passcode validation
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Passcode Verification");
            dialog.setHeaderText("Enter the passcode sent to your email");
            dialog.setContentText("Passcode:");

            Optional<String> userInput = dialog.showAndWait();

            if (userInput.isPresent()) {
                if (userInput.get().equals(passcode)) {
                    // Passcode matches
                    AlertUtils.showAlert("Success", "Verification Code successfully!", Alert.AlertType.INFORMATION);
                    logger.info("Passcode verified. Proceeding with user creation.");

                    // Redirect to login page
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/Login.fxml"));
                        Parent root = fxmlLoader.load();

                        LoginController loginController = fxmlLoader.getController();

                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.getScene().setRoot(root);

                        // Set the stage dimensions
                        stage.setWidth(900); // Set desired width
                        stage.setHeight(600); // Set desired height
                    } catch (IOException e) {
                        logger.error(String.format("Error: Failed to back to login page: %s ", e.getMessage()));
                    }
                } else {
                    // Passcode does not match
                    AlertUtils.showAlert("Error", "Invalid Verification Code entered. Operation aborted.", Alert.AlertType.ERROR);
                    // refresh the fields 
                    txtName.setText("");
                    txtPrenom.setText("");
                    txtEmail.setText("");
                    txtCIN.setText("");
                    txtAddress.setText("");
                    ConfirmePassword.setText("");
                    
                    logger.error("Invalid Verification Code entered. Aborting operation.");
                }
            } else {
                // User cancels the dialog
                AlertUtils.showAlert("Cancelled", "Verification Code process cancelled. Operation aborted.", Alert.AlertType.WARNING);
                logger.warn("Verification Code verification cancelled by the user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to save user to the database. Please try again.", Alert.AlertType.ERROR);
            logger.error("Failed to save user to the database. Please try again.");
        }
    }


    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        logger.info("************************** Moving Back to Login Scene ***************************");
        
        // Load the FXML for the login scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/Login.fxml"));
        Parent root = fxmlLoader.load();
        
        // Get the current stage dynamically
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        // Set the root and adjust the scene size
        Scene scene = new Scene(root, 900, 700); 
        stage.setScene(scene);
        
        stage.centerOnScreen();
        
        logger.info("Scene switched to Login.fxml with size 900x700.");
    }

 
    private void updateLocationLabels(LocationInfo locationInfo) {
        timezoneLabel.setText(locationInfo.getTimezone());
        latLabel.setText(String.valueOf(locationInfo.getLat()));
        lonLabel.setText(String.valueOf(locationInfo.getLon()));
        
    }

    @FXML
    private void handleGetLocation() {
        try {
            LocationInfo locationInfo = locationService.getLocationInfo();
            updateLocationLabels(locationInfo);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des données de localisation : " + e.getMessage());
            timezoneLabel.setText("Erreur de récupération");
            latLabel.setText("");
            lonLabel.setText("");
        }
    }
    

    @FXML
    private void handleOpenMapButton(ActionEvent event) {
    
        String url = "https://www.openstreetmap.org/#map=8/36.814/9.943";
    
        try {
            // Create a WebView
            WebView webView = new WebView();
            webView.getEngine().load(url); // Load the URL
    
            // Create a scene and add the WebView
            Scene scene = new Scene(webView, 800, 600);
    
            // Create a new Stage (window)
            Stage mapStage = new Stage(); // Use a new Stage to avoid conflicts
            mapStage.setTitle("OpenStreetMap Viewer");
            mapStage.setScene(scene);
    
            // Show the new window
            mapStage.show();
        } catch (Exception e) {
            logger.error("Failed to load URL : " + url);
            // ystem.err.println("Failed to load URL: " + url);
            e.printStackTrace();
    
            // Show an error alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load OpenStreetMap");
            alert.setContentText("An error occurred while trying to load the map.");
            alert.showAndWait();
        }
    }
    
    
}


