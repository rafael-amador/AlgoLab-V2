package com.algolab.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.algolab.util.SceneSwitcher;
import com.algolab.util.EmailSender;
import com.algolab.util.PasswordResetService;
import com.algolab.model.UserAccount;
import com.algolab.controller.VerifyResetCodeController;

public class ForgotPasswordController {

    @FXML
    private TextField emailTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        emailTextField.setOnAction(this::sendCode);
    }

    @FXML
    private void sendCode(ActionEvent event) {
        String email = emailTextField.getText();

        if (email == null || email.isEmpty()) {
            errorLabel.setText("Please enter your email.");
            return;
        }

        if (!UserAccount.emailExists(email)) {
            errorLabel.setText("Email not found.");
            return;
        }

        // Initiate reset flow
        PasswordResetService.initiateReset(email);
        String code = PasswordResetService.getCode();

        // Send Email (In a real app, do this asynchronously)
        try {
            EmailSender.send(email, "Password Reset Code", "Your password reset code is: " + code);

            // Switch to verification screen
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                SceneSwitcher.switchToWithController(stage, "common_verification.fxml",
                        new VerifyResetCodeController());
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error loading verification screen.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to send email. Try again.");
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
