package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        StackPane root = loader.load();

        // Configurer la scène
        Scene scene = new Scene(root, 600, 400);

        // Configurer la fenêtre principale
        primaryStage.setTitle("UAM e-Vote - Système de Vote Électronique");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Lancement de JavaFX
        launch(args);
    }
}