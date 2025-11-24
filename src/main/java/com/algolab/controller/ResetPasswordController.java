
package com.algolab.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.algolab.util.SceneSwitcher;
import com.algolab.util.PasswordResetService;
import com.algolab.model.UserAccount;

public class ResetPasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    public static final int MIN_PASSWORD_LENGTH = 6;

    @FXML
    private void initialize() {
        newPasswordField.setOnAction(this::resetPassword);
        confirmPasswordField.setOnAction(this::resetPassword);
    }

    @FXML
    private void resetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            errorLabel.setText("Please enter both fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        // Password Complexity Checks
        if (!isBigger(newPassword)) {
            errorLabel.setText("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
            return;
        }
        if (!hasCapital(newPassword)) {
            errorLabel.setText("Password must contain at least one uppercase letter.");
            return;
        }
        if (!hasNumber(newPassword)) {
            errorLabel.setText("Password must contain at least one number.");
            return;
        }

        String email = PasswordResetService.getEmail();
        if (email == null) {
            errorLabel.setText("Session expired. Please start over.");
            return;
        }

        if (UserAccount.updatePassword(email, newPassword)) {
            // Success
            PasswordResetService.clear();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                SceneSwitcher.switchTo(stage, "login.fxml");
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error returning to login.");
            }
        } else {
            errorLabel.setText("Failed to update password. Try again.");
        }
    }

    private boolean isBigger(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    private boolean hasCapital(String password) {
        return password.matches(".*[A-Z].*");
    }

    private boolean hasNumber(String password) {
        return password.matches(".*[0-9].*");
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        PasswordResetService.clear();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
