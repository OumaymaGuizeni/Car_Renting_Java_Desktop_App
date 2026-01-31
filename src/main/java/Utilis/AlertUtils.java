package Utilis;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.util.Duration;

public class AlertUtils {

    // Existing method to show alert with user input (blocks until dismissed)
    public static void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // New method to show alert for 3 seconds
    public static void showAlertFor3Seconds(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();

        // Create a PauseTransition to close the alert after 3 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> alert.close());
        pause.play();
    }
}
