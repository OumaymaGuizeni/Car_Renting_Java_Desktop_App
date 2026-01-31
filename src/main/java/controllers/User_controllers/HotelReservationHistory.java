package controllers.User_controllers;

import Entite.Reservation;  
import Services.Service_Reservation;
import Utilis.AlertUtils;
import Utilis.LogManager;
import Entite.Personne;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HotelReservationHistory {
    LogManager logger = LogManager.getInstance();

    // TableView to display reservations
    @FXML
    private TableColumn<Reservation, String> Type_Reservation;
    @FXML
    private TableView<Reservation> Tablereservation;
    @FXML
    private TableColumn<Reservation, LocalDate> Start_Date;
    @FXML
    private TableColumn<Reservation, LocalDate> End_Date;
    @FXML
    private TableColumn<Reservation, Double> TotaleCost;

    @FXML
    private Button backClicked;
    @FXML
    private Button loadReservationsButton;  // The new button to load reservations

    private final Service_Reservation Servicereservation = new Service_Reservation();
    private Personne user;
    private Scene rootScene;

    /**
     * Set the user object for this controller.
     * This method should be called before interacting with the controller.
     */
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

    /**
     * Initialize the TableView columns and set up necessary bindings.
     * This method is called automatically when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        logger.info("Initializ the HotelReservationHistory");
    }

    /**
     * Fetch and display reservations for the current user.
     * This method filters hotel reservations and updates the TableView.
     * @throws SQLException if there is an issue with the database query.
     */
    public void loadReservations() throws SQLException {
        // Fetch the reservations for the user
        List<Reservation> list = Servicereservation.getReservationsByUser(user.getEmail());

        // Filter hotel reservations
        List<Reservation> listHotel = list.stream()
                .filter(reservation -> reservation.getType().startsWith("Hotel"))
                .collect(java.util.stream.Collectors.toList());

        logger.info("list is : " + list);
        logger.info("listHotel is : " + listHotel);

        // Convert the list to an observable list and update the TableView
        ObservableList<Reservation> ober = FXCollections.observableArrayList(listHotel);
        Tablereservation.setItems(ober);

        // Set up the TableView columns
        Type_Reservation.setCellValueFactory(new PropertyValueFactory<>("type"));
        logger.info("Type_Reservation column configured.");
        Start_Date.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        logger.info("Start_Date column configured.");
        End_Date.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        logger.info("End_Date column configured.");
        TotaleCost.setCellValueFactory(new PropertyValueFactory<>("reservationPrice"));
        logger.info("TotaleCost column configured.");
        logger.info("Fetched Reservations: " + list);
        logger.info("ObservableList size: " + ober.size());
        logger.info("TableView items size: " + Tablereservation.getItems());
    }

    /**
     * Event handler for when the "Load Reservations" button is clicked.
     */
    @FXML
    public void loadReservationsButtonClicked(ActionEvent event) {
        try {
            loadReservations();  // This now gets called when the button is clicked
        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Failed to load reservations due to database issues.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void backClicked(ActionEvent event) throws IOException {
        logger.info("Move Back to Hotel Reservation Scene");

        // Load the FXML for the Hotel Reservation Scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/HotelReservation.fxml"));
        Parent root = fxmlLoader.load();  // Load the root node from the FXML file

        // Get the controller for the loaded FXML
        HotelReservationController hotelreservationcontroller = fxmlLoader.getController();

        // Check if the user is not null before setting it in the controller
        if (user != null) {
            hotelreservationcontroller.setUser(user);
            logger.info("Setting User Info for Home Scene for user : " + user);
        } else {
            logger.error("Error: User is null in HotelReservationController !");
        }

        // Switch back to the previous scene (Hotel Reservation Scene)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Hotel Reservation");
        Scene newScene = new Scene(root, 1000, 700); // Set width to 1200 and height to 800
        stage.sizeToScene();
        // stage.setResizable(true);
        logger.info("Scene Width: " + newScene.getWidth());
        logger.info("Scene Height: " + newScene.getHeight());
        logger.info("Root Width: " + root.prefWidth(-1));
        logger.info("Root Height: " + root.prefHeight(-1));

        stage.setScene(newScene); // Set the new scene on the stage
        stage.show(); // Show the stage to apply changes
    }
}
