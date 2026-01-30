package Main;

import Utilis.InitBaseDonnee;
import Utilis.TextToSpeech;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        InitBaseDonnee initBaseDonnee = new InitBaseDonnee();
        initBaseDonnee.createTablesIfNotExist();
        // Load the Login scene
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/FXML_FILES/Login.fxml"));
        Parent loginRoot = loginLoader.load();

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(loginRoot, 900, 600));
        primaryStage.centerOnScreen();
        primaryStage.show();
        // TextToSpeech TTSservice = new TextToSpeech();
        // TTSservice.speak("Welcome  to our Application , let's get started ");
    }
}
