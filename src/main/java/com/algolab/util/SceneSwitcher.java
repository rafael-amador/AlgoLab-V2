package com.algolab.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SceneSwitcher {

    public static void switchTo(Stage stage, String fxml) throws IOException {
        // Load from /com/algolab/view/
        java.net.URL url = SceneSwitcher.class.getResource("/com/algolab/view/" + fxml);
        if (url == null) {
            throw new IOException("FXML resource not found on classpath: /com/algolab/view/" + fxml + ". Ensure resources are on the classpath (e.g., src/main/resources).");
        }
        Parent root = FXMLLoader.load(url);
        if (stage.getScene() == null) {
            stage.setScene(new javafx.scene.Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }
    }

    public static void switchToWithController(Stage stage, String fxml, Object controller) throws IOException {
        java.net.URL url = SceneSwitcher.class.getResource("/com/algolab/view/" + fxml);
        if (url == null) {
            throw new IOException("FXML resource not found on classpath: /com/algolab/view/" + fxml + ". Ensure resources are on the classpath (e.g., src/main/resources).");
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        Parent root = loader.load();
        if (stage.getScene() == null) {
            stage.setScene(new javafx.scene.Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }
    }
}
