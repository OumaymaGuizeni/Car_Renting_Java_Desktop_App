package controllers.User_controllers;
import java.util.List;
import java.io.IOException;

import Services.Service_Personnes;
import Utilis.AlertUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import Entite.Personne;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import org.json.JSONObject;

import com.sun.media.jfxmedia.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import Utilis.LogManager;
import Utilis.AlertUtils;
import Utilis.TextToSpeech;
import javafx.scene.control.CheckBox;

public class LoginController {

    @FXML
    public CheckBox speechToggle; // Toggle to enable/disable speech

    @FXML
    public Button loginButton;

    @FXML
    public TextField txtEmail;

    @FXML
    public TextField txtCin;

    @FXML
    public Button CreatUser;

    @FXML
    public Button Exit;

    @FXML
    public MediaView mediaView;
    @FXML
    public Label helpTextLabel;  // Declare the label variable
    @FXML
    public Label timeLabel; // Label to display the current time

    @FXML
    public Label weatherLabel; // Label to display weather data
    @FXML
    private CheckBox togglePasswordCheckbox;

    @FXML
    private TextField txtCinVisible;

    private ActionEvent event = null;
    Service_Personnes service = new Service_Personnes();

    LogManager logger = LogManager.getInstance();

    private boolean isSpeechEnabled = false; // Tracks the speech functionality status

    @FXML
    public void initialize() {
        // Real-time clock update
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> updateTime()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Video playback (ensure video is loaded without performance impact)
        playVideo();

        // Fetch and display weather data asynchronously
        fetchWeatherData();

        // Add hover listeners for buttons
        addHoverSpeech_buttons(loginButton, "Now you will Go to your account , Welcome");
        addHoverSpeech_buttons(CreatUser, "You can join us any moment just hit this button ");
        addHoverSpeech_buttons(Exit, "Ops , seems you want to leave  ");
        addHoverSpeech_txtfield(txtEmail, "Here , you can enter your email account ");
        addHoverSpeech_txtfield(txtCin, "Here , you can enter your Password account ");


        // Add listener to check email validity when focus changes to CIN field
        txtCin.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) { // Lost focus
                String email = txtEmail.getText();
                // if (!isValidEmail(email)) {
                //     showAlert("Please enter a valid email address before proceeding to CIN.");
                //     txtEmail.requestFocus();
                // }
            }
        });
    }

    private void addHoverSpeech_buttons(Button button, String message) {
        button.setOnMouseEntered(event -> {
            if (speechToggle.isSelected()) {
                TextToSpeech TTSservice = new TextToSpeech();
                TTSservice.speak(message);
            }
        });
    }

    private void addHoverSpeech_txtfield(TextField textField, String message) {
        textField.setOnMouseEntered(event -> {
            if (speechToggle.isSelected()) {
                TextToSpeech TTSservice = new TextToSpeech();
                TTSservice.speak(message);
            }
        });
    }

    private void playVideo() {
        // LogManager logger = LogManager.getInstance();
        try {
            URL videoUrl = getClass().getResource("/videos/welcome_scene.mp4");
            if (videoUrl == null) {
                logger.error("Vide file is not found ! ");
                //System.err.println("Video file not found!");
                return;
            }

            Media media = new Media(videoUrl.toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Infinite loop
        } catch (Exception e) {
            logger.error(String.format("Error Loading Video : %s" , e.getMessage()));
            // System.err.println("Error loading video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Update the time
    private void updateTime() {
        LocalTime time = LocalTime.now();
        String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        timeLabel.setText(formattedTime);
        // Use it for debug pupose only otherwise it will create uncessary logging message each second 
        // logger.info(String.format("Current Displayed Time is : %s" , formattedTime ));
    }

    @FXML
    private void handleCreateUser(ActionEvent event) {
        this.event = event;
        // LogManager logger = LogManager.getInstance();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_FILES/UserRegistration.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("User Registration");
            stage.show();
            Stage currentStage = (Stage) txtEmail.getScene().getWindow();
            currentStage.close();
            AlertUtils.showAlertFor3Seconds("Welcome", "Launching UserRegistration Page", AlertType.INFORMATION);
            logger.info("Starting the CreateUser Scene now ");
        } catch (IOException e) {
            AlertUtils.showAlertFor3Seconds("Error", "Failed to launch UserRegistration Page.", AlertType.ERROR);
            logger.error("Error in launching UserRegistration Page Kindly check the Next Error Message ");
            logger.error(String.format("Error is : %s" , e.getMessage()));
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        this.event = event;
        String email = txtEmail.getText();
        String cin = txtCin.getText();
        logger.info("Handle login Now for a User ");
        logger.info(String.format("The input given by the user is: Email %s / CIN %s", email, cin));
        
        if (email.isEmpty() || cin.isEmpty()) {
            AlertUtils.showAlert("Error", "Please fill in both the email and CIN.",AlertType.ERROR);
        }
        else {
            if (email.equals("admin") && cin.equals("cin")) {

                navigateToAdminDashboard(); 
            }
            else if (!isValidEmail(email)) {
                AlertUtils.showAlert("Error", "Please enter a valid email address.", AlertType.ERROR);
            } else if (cin.length() < 8) {
                AlertUtils.showAlert("Error", "Password must be 8 characters long",AlertType.ERROR);
            }
            else {

                String cinHashed = service.hashSHA256(cin);

                    authenticateUser(email, cinHashed);
                }
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches(".+@.+\\..+");
    }

    private void authenticateUser(String email, String cin) {
        Service_Personnes servicePersonnes = new Service_Personnes();
        try {
            List<Personne> users = servicePersonnes.getAll();
            for (Personne user : users) {
                // System.out.println(user.getEmail());
                // System.out.println(user.getCin());
                logger.info(String.format("Fetting Email %s" , user.getEmail()));
                logger.info(String.format("Fetting CIN %s" , user.getCin()));

                if (email.equals(user.getEmail()) && cin.equals(user.getCin())) {
                    navigateToUserDashboard(user);
                    return;
                }
            }
            AlertUtils.showAlert("Error", "Invalid email or CIN , Please try Again , thanks ",AlertType.ERROR);
            logger.error("Error : invalid email or CIN , showing popup to the User to try again");
        } catch (SQLException e) {
            logger.error(String.format("Error fetching Users : %s " , e.getMessage()));
            // System.out.println("Error fetching users: " + e.getMessage());
        }
    }

    private void navigateToUserDashboard(Personne user) {
        logger.info("******* Navigate to User Dashboard now *********");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_FILES/home.fxml"));
            Parent root = loader.load();
            HomeController homeController = loader.getController();
            homeController.setUserDetails(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("User Dashboard");
            stage.show();

            Stage currentStage = (Stage) txtEmail.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            AlertUtils.showAlert("Error", "Failed to load The User Dashboard", AlertType.ERROR);
            // showAlert("Failed to load the User Dashboard.");
        }
    }

    private void navigateToAdminDashboard() {
        logger.info("********* Navigate to Admin Dashboard now ****************");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_FILES/admin_Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("Admin Dashboard");
            stage.show();

            Stage currentStage = (Stage) txtEmail.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            AlertUtils.showAlert("Error", "Failed to load the Admin Dashboard ", AlertType.ERROR);
            logger.error(String.format("Error : Failed to load Admin Dashboard : %s " , e.getMessage()));
            // showAlert("Failed to load the Admin Dashboard.");
        }
    }

    public void handleExit(ActionEvent event) {
        logger.info("*************** User Exit the Login Page ************** "); 
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void handleHelp(ActionEvent event) {
        String Title =  "Help ! Need Assistance ? "; 
        String PopupContent = "Here is some helpful information about logging in: \n\n\n"
            + "1. Enter your email address.\n"
            + "2. Enter your CIN.\n"
            + "3. Click 'Login' to access your account.\n"
            + "4. If you are new, click 'Create New User' to register.\n\n"
            + "For more help, contact support." ;
        AlertUtils.showAlert(Title, PopupContent,AlertType.INFORMATION);
        txtEmail.requestFocus();
    }


    public void AboutMyBooking(ActionEvent event) {
        // Display the contact information in an alert
        String Title = "Contact Us" ;
        String PopupContent = "Contact Us for furthuer Assistance \n \n \n "
            + "Email   :       MyBooking@esprit.tn\n"
            + "Phone   :       +216 25000336\n\n"
            + "Website : www.MyBooking.com \n\n"
            + "We are happy to assist you! " ; 
        AlertUtils.showAlert(Title, PopupContent,AlertType.INFORMATION);
        txtEmail.requestFocus();
    }

    private void fetchWeatherData() {
        logger.info("Fetching Weather Data");
        String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Tunisie?unitGroup=metric&key=PPKBBJ7637X5SNDUG6HZA23X7";
        new Thread(() -> {
            try {
                // Fetch data from API
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON
                JSONObject json = new JSONObject(response.toString());
                JSONObject currentConditions = json.getJSONObject("currentConditions");
                double temperature = currentConditions.getDouble("temp");
                double humidity = currentConditions.getDouble("humidity");

                // Update UI in the main thread
                javafx.application.Platform.runLater(() ->
                        weatherLabel.setText(String.format("Temperature: %.2f°C\nHumidity: %.2f%%", temperature, humidity))
                );
                logger.info("Logging Data of Weather");
                logger.info(String.format("Temperature: %.2f °C", temperature)); // Log temperature
                logger.info(String.format("Humidity: %.2f %%", humidity)); // Log humidity

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> weatherLabel.setText("Failed to fetch weather data."));
                logger.error("Error : Failed to fetch Weather Data");
                logger.error(String.format("The Error is : %s " , e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }
    @FXML
    private void togglePasswordVisibility() {
        if (togglePasswordCheckbox.isSelected()) {
            txtCinVisible.setText(txtCin.getText());
            txtCinVisible.setVisible(true);
            txtCin.setVisible(false);
        } else {
            txtCin.setText(txtCinVisible.getText());
            txtCin.setVisible(true);
            txtCinVisible.setVisible(false);
        }
    }
}
