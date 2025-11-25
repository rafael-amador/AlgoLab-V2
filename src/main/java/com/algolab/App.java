package com.algolab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.algolab.util.SessionManager;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Determine starting FXML
        String startView = "login.fxml";

        String savedToken = SessionManager.loadTokenLocally();
        if (savedToken != null && SessionManager.validateSession(savedToken)) {
            startView = "mainMenu.fxml";
        }

        Parent root = FXMLLoader.load(getClass().getResource("/com/algolab/view/" + startView));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/algolab/styles/darkTheme.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Algo Lab");
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
