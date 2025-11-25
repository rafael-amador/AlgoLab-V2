package com.algolab.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.algolab.util.SceneSwitcher;
import com.algolab.util.SessionManager;

public class MainMenuController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem newDatabaseMenuItem;
    @FXML
    private MenuItem newFolderMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem signOutMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private RadioMenuItem viewVisualMenuItem;
    @FXML
    private RadioMenuItem viewTableMenuItem;
    @FXML
    private RadioMenuItem viewSplitMenuItem;
    @FXML
    private MenuItem runLastAlgorithmMenuItem;
    @FXML
    private MenuItem clearAlgorithmOutputMenuItem;
    @FXML
    private MenuItem preferencesMenuItem;
    @FXML
    private MenuItem themeMenuItem;
    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private VBox leftPanel;
    @FXML
    private Button newDatabaseButton;
    @FXML
    private Button newFolderButton;
    @FXML
    private TreeView<?> databaseTreeView;

    @FXML
    private BorderPane centerPanel;
    @FXML
    private Label activeDatabaseLabel;
    @FXML
    private ToggleButton visualViewToggle;
    @FXML
    private ToggleButton tableViewToggle;
    @FXML
    private TabPane viewTabPane;
    @FXML
    private Tab visualTab;
    @FXML
    private StackPane visualViewContainer;
    @FXML
    private Tab tableTab;
    @FXML
    private TableView<?> dataTableView;

    @FXML
    private VBox rightPanel;
    @FXML
    private Accordion algorithmAccordion;
    @FXML
    private Button insertButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button linearSearchButton;
    @FXML
    private Button binarySearchButton;
    @FXML
    private Button jumpSearchButton;
    @FXML
    private Button bubbleSortButton;
    @FXML
    private Button mergeSortButton;
    @FXML
    private Button quickSortButton;
    @FXML
    private Button bfsButton;
    @FXML
    private Button dfsButton;
    @FXML
    private Button dijkstraButton;

    @FXML
    private HBox statusBar;
    @FXML
    private Label statusUserLabel;
    @FXML
    private Label statusVersionLabel;
    @FXML
    private Label statusLastSavedLabel;

    @FXML
    public void initialize() {
        // Set the status user label based on current session token
        try {
            // Prefer in-memory current email (set at login/signup). If not present,
            // fall back to a stored session token.
            String email = SessionManager.getCurrentEmail();
            if (email == null) {
                String token = SessionManager.loadTokenLocally();
                if (token != null && !token.isEmpty()) {
                    email = SessionManager.getEmailForToken(token);
                }
            }

            if (email != null) {
                statusUserLabel.setText("User: " + email);
            } else {
                statusUserLabel.setText("User: (guest)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusUserLabel.setText("User: (guest)");
        }

        // Wire Exit menu item to logout+exit to ensure immediate behavior
        try {
            if (exitMenuItem != null) {
                exitMenuItem.setOnAction(evt -> {
                    javafx.application.Platform.exit();
                });
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Logout user - clear session and return to login screen
     */
    @FXML
    public void logout(ActionEvent event) {
        // Clear session from database and local storage
        SessionManager.logout();

        // Use a known Node to obtain the Stage instead of casting the event source
        Stage stage = null;
        try {
            if (menuBar != null && menuBar.getScene() != null) {
                stage = (Stage) menuBar.getScene().getWindow();
            } else if (statusBar != null && statusBar.getScene() != null) {
                stage = (Stage) statusBar.getScene().getWindow();
            } else {
                // Fallback to event source if it's a Node
                if (event.getSource() instanceof Node) {
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                }
            }

            if (stage != null) {
                SceneSwitcher.switchTo(stage, "login.fxml");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
}
