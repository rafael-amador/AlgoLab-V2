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
        // Clean up expired sessions on startup
        SessionManager.cleanupExpiredSessions();

        // Check for existing valid session
        String savedToken = SessionManager.loadTokenLocally();
        String startView = "login.fxml";

        if (savedToken != null && SessionManager.validateSession(savedToken)) {
            // Valid session exists, skip login
            startView = "mainMenu.fxml";
        }

        java.net.URL viewUrl = App.class.getResource("/com/algolab/view/" + startView);
        if (viewUrl == null) {
            System.err.println("ERROR: FXML resource not found on classpath: /com/algolab/view/" + startView);
            System.err.println("Make sure 'src/main/resources' is configured as a resource path so files are copied into '" + System.getProperty("user.dir") + "/bin' or that your build tool places resources on the classpath.");
            throw new RuntimeException("FXML start view not found on classpath: " + startView);
        }

        Parent root = FXMLLoader.load(viewUrl);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Algo Lab");
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);

        // Load CSS (fail fast with clear message when resource missing)
        java.net.URL cssUrl = getClass().getResource("/com/algolab/styles/darkTheme.css");
        if (cssUrl == null) {
            System.err.println("WARNING: Stylesheet not found on classpath: /com/algolab/styles/darkTheme.css");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
