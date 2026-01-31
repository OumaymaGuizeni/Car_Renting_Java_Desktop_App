package controllers.User_controllers;

import Entite.Personne;
import Entite.SpecialOffers;
import Services.Service_SpecialOffer;
import Utilis.AlertUtils;
import Utilis.LogManager;
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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OffersController {
    LogManager logger = LogManager.getInstance();

    @FXML
    private Button btnLogout;

    private Scene rootScene;
    private Personne user;

    @FXML
    private ComboBox<SpecialOffers> ComboxSpecialOffer;

    @FXML
    private ComboBox<String> PaymentType;

    @FXML
    private Label Offer_Date;

    @FXML
    private Label Offer_Type;
    @FXML
    private Label discount;

    @FXML
    private Label Prices;

    private final Service_SpecialOffer service = new Service_SpecialOffer();

    public void setUser(Personne user) {
        if (user == null) {
            logger.error("Warning: User is null when set in OffersController");
        }
        this.user = user;
    }


    @FXML
    void initialize() {
        try {
            // Load all special offers and filter for available ones
            List<SpecialOffers> list = service.getAll();
            ObservableList<SpecialOffers> availableOffers = FXCollections.observableArrayList();
            list.stream()
                    .filter(specialOffers -> specialOffers.getAvailability() == 1)  // Filter available offers
                    .forEach(availableOffers::add);

            // Set the filtered offers in the ComboBox
            ComboxSpecialOffer.setItems(availableOffers);

            // Add a listener to update details when an offer is selected
            ComboxSpecialOffer.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateOfferDetails(newValue);
                }
            });

            // Set payment options
            ObservableList<String> paymentOptions = FXCollections.observableArrayList("with card", "Cash");
            PaymentType.setItems(paymentOptions);

        } catch (Exception e) {
            AlertUtils.showAlert("Initialization Error", "Failed to initialize the controller: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void updateOfferDetails(SpecialOffers selectedOffer) {
        Offer_Type.setText("Offer Type: " + selectedOffer.getOfferType());
        Offer_Date.setText("Offer Date: " + selectedOffer.getStartDate() + " To " + selectedOffer.getEndDate());
        Prices.setText("Price: " + selectedOffer.getPrice());
        discount.setText("Discount Percentage: " + selectedOffer.getDiscountPercentage());
    }
    @FXML
    void RentOfferSpecial(ActionEvent event) throws Exception  {
        SpecialOffers selectedOffer = ComboxSpecialOffer.getValue();
        if (selectedOffer != null) {
            selectedOffer.setAvailability(0);
            System.out.println(selectedOffer);
            service.update(selectedOffer);
            initialize();

        } else {
            AlertUtils.showAlert("No Offer Selected", "Please select an offer to rent.", Alert.AlertType.WARNING);
        }


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
            logger.info("Error : User is null in OffersController");
        }

        // Switch back to the home scene
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("User Dashboard");
        stage.getScene().setRoot(root);
    }
}
