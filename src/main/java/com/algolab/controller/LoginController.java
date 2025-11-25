package com.algolab.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;
import com.algolab.util.SceneSwitcher;
import com.algolab.util.SessionManager;
import com.algolab.model.UserAccount;

public class LoginController {
    @FXML
    private TextField gmailTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private StackPane errorContainer;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        hideError(); // starts with error hidden

        gmailTextField.setOnAction(e -> login(e));
        passwordTextField.setOnAction(e -> login(e));
    }

    public void login(ActionEvent e) {
        String gmail = gmailTextField.getText();
        String password = passwordTextField.getText();

        UserAccount user = new UserAccount(gmail, password);
        try {
            if (inputIsValid()) {
                if (user.login()) {
                    // Login successful
                    hideError();

                    // Create session if "Keep me signed in" is checked
                    if (rememberMeCheckbox.isSelected()) {
                        SessionManager.createSession(gmail, 30); // 30 days
                    }
                        // Always set current in-memory user for immediate UI update
                        SessionManager.setCurrentEmail(gmail);

                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    SceneSwitcher.switchTo(stage, "mainMenu.fxml");
                } else {
                    // Show error message to user through label
                    showError(user.getLastError());
                }
            }
        } catch (IOException error) {
            // later add a custom popup for big errors like this:
            System.out.println("error: " + error.getMessage());
        }
    }

    @FXML
    private void switchToForgotPassword(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "forgot_password.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToSignUp(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "signup.fxml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void guestSignIn(ActionEvent e) {
        // Start an ephemeral guest session (no persistence)
        System.out.println("Guest sign-in requested");

        // Ensure no persistent session remains
        try {
            SessionManager.clearLocalToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SessionManager.setCurrentEmail("Guest");
        hideError();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "mainMenu.fxml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean inputIsValid() {
        boolean isValid = true;
        if (isEmpty(passwordTextField)) {
            showErrorHighlight(passwordTextField);
            showError("password is empty");
            isValid = false;
        } else {
            clearErrorHighlight(passwordTextField);
        }
        if (isEmpty(gmailTextField)) {
            showErrorHighlight(gmailTextField);
            showError("Gmail is empty");
            isValid = false;
        } else {
            clearErrorHighlight(gmailTextField);
        }
        return isValid;
    }

    public boolean isEmpty(TextField text) {
        return text.getText().trim().isEmpty();
    }

    // Label error
    private void showError(String message) {
        errorLabel.setText(message);
        errorContainer.setVisible(true);
        errorContainer.setManaged(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorContainer.setVisible(false);
        errorContainer.setManaged(false);
    }

    // Highlight Error
    private void showErrorHighlight(TextField field) {
        if (!field.getStyleClass().contains("error-field")) {
            field.getStyleClass().add("error-field");
        }
    }

    private void clearErrorHighlight(TextField field) {
        field.getStyleClass().remove("error-field");
    }
}