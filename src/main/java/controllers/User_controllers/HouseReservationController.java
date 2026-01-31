package controllers.User_controllers;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import Entite.House;
import Entite.Personne;
import Entite.Reservation;
import Services.Service_Houses;
import Services.Service_Reservation;
import Utilis.AlertUtils;
import Utilis.LogManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HouseReservationController {
    private final LogManager logger = LogManager.getInstance();

    @FXML
    private TableView<House> tableHouses; // Correct naming
    @FXML
    private TableColumn<Reservation, String> Type_Reservations;
    @FXML
    private TableView<Reservation> Tablereservations;
    @FXML
    private TableColumn<Reservation, LocalDate> Start_Dates;
    @FXML
    private TableColumn<Reservation, LocalDate> End_Dates;

    @FXML
    private TableColumn<Reservation, ?> TotaleCost;
    @FXML
    private TableColumn<House, String> columnId;
    @FXML
    private TableColumn<House, String> columnName;
    @FXML
    private TableColumn<House, String> columnLocation;
    @FXML
    private TableColumn<House, Integer> columnRoomsAvailable;
    @FXML
    private TableColumn<House, Integer> columnPricePerNight;
    @FXML
    private TableColumn<House, String> columnFullCapacity;
    @FXML
    private TableColumn<House, String> columnReservedByOurAgency;
    @FXML
    private TableColumn<House, String> columnRentButton;
    @FXML
    private TableColumn<House, String> columnViewButton;
    @FXML
    private ComboBox<String> comboBoxHouses; // For house selection
    @FXML
    private Button btnLogout;
    @FXML
    private ImageView dynamicImage;
    @FXML
    private TextField priceField;
    private Scene rootScene;
    private Personne user;
    private final Service_Houses houseService = new Service_Houses();
private final Service_Reservation Servicereservation = new Service_Reservation();
    public void setUser(Personne user) {
        if (user == null) {
            logger.warn("Warning: User is null when set in HouseReservationController");
        }
        this.user = user;
    }

    public void setRootScene(Scene rootScene) {
        this.rootScene = rootScene;
    }
    private boolean isTableVisible = false;
    @FXML
    private void initialize() {
        // Make sure tableHouses is properly initialized
        if (tableHouses != null) {
            loadHouses(); // Ensure the houses are loaded during initialization
            setupTable(); // Setup the table after loading houses
        } else {
            logger.error("Error: tableHouses is null during initialization");
            Tablereservations.setVisible(isTableVisible);

        }
    }

    private void setupTable() {
        // Set the columns' value factory to bind the data to the table view
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnRoomsAvailable.setCellValueFactory(new PropertyValueFactory<>("roomsAvailable"));
        columnPricePerNight.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        columnFullCapacity.setCellValueFactory(new PropertyValueFactory<>("fullCapacity"));
        columnReservedByOurAgency.setCellValueFactory(new PropertyValueFactory<>("reservedByOurAgency"));
    
        // Ensure the columns are editable
        tableHouses.setEditable(true);
    
        // Editable columns setup (if needed)
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setOnEditCommit(event -> {
            House house = event.getRowValue();
            house.setName(event.getNewValue());
            updateHouseInDatabase(house);
        });
        columnName.setEditable(false);
        columnLocation.setEditable(false);
        columnLocation.setEditable(false);
        columnRoomsAvailable.setEditable(false);
        columnPricePerNight.setEditable(false);
        
        
        columnLocation.setCellFactory(TextFieldTableCell.forTableColumn());
        columnLocation.setOnEditCommit(event -> {
            House house = event.getRowValue();
            house.setLocation(event.getNewValue());
            updateHouseInDatabase(house);
        });
    
        columnRoomsAvailable.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnRoomsAvailable.setOnEditCommit(event -> {
            House house = event.getRowValue();
            house.setRoomsAvailable(event.getNewValue());
            updateHouseInDatabase(house);
        });
    
        columnPricePerNight.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnPricePerNight.setOnEditCommit(event -> {
            House house = event.getRowValue();
            house.setPricePerNight(event.getNewValue());
            updateHouseInDatabase(house);
        });
    
        columnFullCapacity.setCellFactory(TextFieldTableCell.forTableColumn());
        columnFullCapacity.setOnEditCommit(event -> {
            House house = event.getRowValue();
            house.setFullCapacity(event.getNewValue());
            updateHouseInDatabase(house);
        });
    
        // Apply row style based on FullCapacity value
        tableHouses.setRowFactory(new Callback<TableView<House>, TableRow<House>>() {
            @Override
            public TableRow<House> call(TableView<House> tableView) {
                return new TableRow<House>() {
                    @Override
                    protected void updateItem(House house, boolean empty) {
                        super.updateItem(house, empty);
                        if (house != null && house.getFullCapacity().equals("Not Reserved")) {
                            setStyle("-fx-background-color: rgba(235, 222, 240, 0.5); " +
                            "-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
                        } else {
                            setStyle(""); // Reset style for other rows
                        }
                    }
                };
            }
        });
    
        tableHouses.setStyle(
            "-fx-table-cell-border-color: lightgray;" +  // Set the internal gridline color
            "-fx-border-color: transparent;"            // No external border
        );
                columnReservedByOurAgency.setCellFactory(TextFieldTableCell.forTableColumn());
        columnReservedByOurAgency.setOnEditCommit(event -> {
            House house = event.getRowValue();
            house.setReservedByOurAgency(event.getNewValue());
            updateHouseInDatabase(house);
            
        });

        columnRentButton.setCellFactory(param -> new TableCell<>() {
            private final Button rentButton = new Button();
        
            {
                // Set the icon for the rent button
                Image rentIcon = new Image("/src/rent.png");  // Replace with the actual path to your image file
                ImageView rentImageView = new ImageView(rentIcon);
                rentImageView.setFitWidth(30);  // Set the width of the icon
                rentImageView.setFitHeight(30); // Set the height of the icon
        
                // Set the icon as the button's graphic
                rentButton.setGraphic(rentImageView);
        
                // Remove text from the button
                rentButton.setText(null);
        
                // Handle button click
                rentButton.setOnAction(event -> {
                    House house = getTableView().getItems().get(getIndex());
                    rentHouse(house,event);
                });
        
                // Make button transparent: no border, no background
                rentButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
        
                // Stretch the button to fill the entire cell width
                rentButton.setMaxWidth(Double.MAX_VALUE);
                rentButton.setMinWidth(0);
            }
        
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(rentButton);
                }
            }
        });
        
        
            // Setup for View Button Column
            columnViewButton.setCellFactory(param -> new TableCell<>() {
                private final Button viewButton = new Button();
            
                {
                    // Set the icon for the rent button
                    Image viewIcon = new Image("/src/view.png");  // Replace with the actual path to your image file
                    ImageView viewImageView = new ImageView(viewIcon);
                    viewImageView.setFitWidth(30);  // Set the width of the icon
                    viewImageView.setFitHeight(30); // Set the height of the icon
            
                    // Set the icon as the button's graphic
                    viewButton.setGraphic(viewImageView);
            
                    // Remove text from the button
                    viewButton.setText(null);
            
                    // Handle button click
                    viewButton.setOnAction(event -> {
                        House house = getTableView().getItems().get(getIndex());
                        viewHouseDetails(house);
                    });
            
                    // Make button transparent: no border, no background
                    viewButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
            
                    // Stretch the button to fill the entire cell width
                    viewButton.setMaxWidth(Double.MAX_VALUE);
                    viewButton.setMinWidth(0);
                }
            
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewButton);
                    }
                }
            });
    }
    private void rentHouse(House house, ActionEvent event) {
        try {
            // Étape 1 : Logique de location de la maison
            logger.info("Renting house " + house.getName());
    
            // Étape 2 : Chargement du fichier FXML du deuxième contrôleur
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/HouseConfirmeReservation.fxml"));
            Parent root = fxmlLoader.load();
    
            // Étape 3 : Récupérer le contrôleur du deuxième fichier FXML
            HouseConfirmeReservation confirmationController = fxmlLoader.getController();
    
            // Étape 4 : Passer l'objet 'house' au deuxième contrôleur
            confirmationController.setHouse(house);
            confirmationController.setUser(user);
            // Étape 5 : Mise à jour de la scène
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.getScene().setRoot(root);
    
            logger.info("Successfully navigated to confirmation screen");
    
        } catch (IOException e) {
            logger.error("Error loading FXML file: " );
            System.out.println(e);
        }
    }

    
    private void viewHouseDetails(House house) {
        logger.info("Viewing details for house: " + house.getName());
        logger.info("Details: " + house.toString());
    
        try {
            // Spécifier le chemin absolu de l'image
            String imagePath = house.getReservedByOurAgency();
            System.out.println(imagePath);
            
            // Création de l'image en utilisant le chemin local
            Image image = new Image("file:" + imagePath, true); // true pour chargement en arrière-plan
            ImageView dynamicImage = new ImageView(image);
    
            // Activer la mise à l'échelle de l'image pour qu'elle s'adapte à la taille de la fenêtre
            dynamicImage.setPreserveRatio(true); // Conserver les proportions
            
            // Utiliser StackPane pour contenir l'image
            StackPane imagePane = new StackPane(dynamicImage);
            imagePane.setAlignment(Pos.CENTER);
    
            // Créer une nouvelle scène avec le StackPane
            Scene newScene = new Scene(imagePane, 500, 500); // Taille initiale de la fenêtre
            Stage newStage = new Stage();
            newStage.setTitle("House Image - Image");
    
            // Lier la taille de l'image à celle de la fenêtre
            dynamicImage.fitWidthProperty().bind(newScene.widthProperty());
            dynamicImage.fitHeightProperty().bind(newScene.heightProperty());
    
            // Définir et afficher la scène
            newStage.setScene(newScene);
            newStage.show();
    
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }
    

    

    public void loadHouses() {
        try {
            // Retrieve houses from the database
            List<House> houses = houseService.getAll();
    
            // Filter houses where Full Capacity is "NotReserved"
            List<House> filteredHouses = houses.stream()
                                               .filter(house -> "Not Reserved".equals(house.getFullCapacity()))
                                               .collect(java.util.stream.Collectors.toList());
    
            // Affichage des données dans la console
            // System.out.println("Filtered Houses (NotReserved):");
            logger.info(("Filtered Houses (NotReserved)"));
            for (House house : filteredHouses) {
                // System.out.println("ID: " + house.getId() + ", Name: " + house.getName() + ", Location: " + house.getLocation()
                //         + ", Rooms Available: " + house.getRoomsAvailable() + ", Price Per Night: " + house.getPricePerNight()
                //         + ", Full Capacity: " + house.getFullCapacity() + ", Reserved By Our Agency: " + house.getReservedByOurAgency());
                logger.info("ID: " + house.getId() + ", Name: " + house.getName() + ", Location: " + house.getLocation()
                        + ", Rooms Available: " + house.getRoomsAvailable() + ", Price Per Night: " + house.getPricePerNight()
                        + ", Full Capacity: " + house.getFullCapacity() + ", Reserved By Our Agency: " + house.getReservedByOurAgency());
            }
    
            // Convert the List<House> to ObservableList for TableView binding
            ObservableList<House> houseList = FXCollections.observableArrayList(filteredHouses);
            tableHouses.setItems(houseList);
        } catch (Exception e) {
            logger.error("Error loading houses: " );
        }
    }
    

    private void updateHouseInDatabase(House house) {
        try {
            houseService.update(house);
        } catch (SQLException e) {
            logger.error("Database Error Unable to update house: ");
        }
    }


    @FXML
    void BACK(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/home.fxml"));
        Parent root = fxmlLoader.load();

        HomeController homeController = fxmlLoader.getController();
        if (user != null) {
            homeController.setUserDetails(user);
        } else {
            logger.error("Error: User is null in HouseReservationController!");
        }

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

        @FXML
    void ShowMyreservation(ActionEvent event) {
        try {
            logger.info(" ********** Fetching reservations for user: " + user.getEmail() + " *****************************");
            // Fetch reservations only for the logged-in user
            List<Reservation> list = Servicereservation.getReservationsByUser(user.getEmail());
            List<Reservation> listCar = list.stream()
                    .filter(reservation -> reservation.getType().startsWith("House"))
                    .collect(java.util.stream.Collectors.toList());
            ObservableList<Reservation> ober = FXCollections.observableArrayList(listCar);
            Tablereservations.setItems(ober);

            // Set up table columns

            Type_Reservations.setCellValueFactory(new PropertyValueFactory<>("type"));
            Start_Dates.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
            End_Dates.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
            TotaleCost.setCellValueFactory(new PropertyValueFactory<>("reservationPrice"));
        } catch (SQLException e) {
            AlertUtils.showAlert("Initialization Error", "Failed to load reservations. " + e.getMessage(), Alert.AlertType.ERROR);
        }

    
}

@FXML
void hideTable(ActionEvent event) {
    isTableVisible = !isTableVisible;

    // Appliquez l'état à la table
    Tablereservations.setVisible(isTableVisible);
    
}

}