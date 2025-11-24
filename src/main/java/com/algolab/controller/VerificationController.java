package com.algolab.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.algolab.util.SceneSwitcher;
import com.algolab.util.VerificationService;
import com.algolab.util.SessionManager;
import com.algolab.model.UserAccount;

public class VerificationController {

    @FXML
    private TextField d1, d2, d3, d4, d5, d6;
    @FXML
    private Label errorLabel;
    @FXML
    private StackPane errorContainer;
    @FXML
    private Label emailedYouLabel;

    @FXML
    private void initialize() {
        hideError();
        setupBox(d1, d2, null);
        setupBox(d2, d3, d1);
        setupBox(d3, d4, d2);
        setupBox(d4, d5, d3);
        setupBox(d5, d6, d4);
        setupBox(d6, null, d5);
    }

    // Setup behavior for each OTP digit box
    private void setupBox(TextField field, TextField next, TextField prev) {

        // Only allow ONE digit
        field.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();

            // 1 digit max, must be number
            if (!newText.matches("\\d?")) {
                return null;
            }

            return change;
        }));

        // Auto move to next box
        field.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() == 1 && next != null) {
                next.requestFocus();
            }
        });

        // Backspace moves back
        field.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case BACK_SPACE:
                    if (field.getText().isEmpty() && prev != null) {
                        prev.requestFocus();
                    }
                    break;
                default:
                    break; // handles all other keys
            }
        });
        // Submit on enter
        field.setOnAction(e -> {
            try {
                verifyCode(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Click "Verify" button
    @FXML
    private void verifyCode(ActionEvent e) throws Exception {
        String code = d1.getText() +
                d2.getText() +
                d3.getText() +
                d4.getText() +
                d5.getText() +
                d6.getText();

        // Make sure user actually typed all digits
        if (code.length() != 6) {
            showError("Please enter all 6 digits.");
            return;
        }

        // Check the code with VerificationService
        if (VerificationService.isCorrect(code)) {
            // add account
            UserAccount user = VerificationService.getPendingUser();
            if (user != null) {
                user.signup(); // INSERT INTO DATABASE NOW
                // Create a session for the new user so the main menu reflects the signed-in user
                try {
                    SessionManager.createSession(user.getGmail(), 30);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // switch to main menu
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            SceneSwitcher.switchTo(stage, "mainMenu.fxml");
        } else {
            showError("Incorrect code. Try again.");
        }
    }

    public void initializeWithEmail(String email) {
        emailedYouLabel.setText("We emailed you a six-digit code to " + email + ". Enter the code below:");
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

    @FXML
    private void cancel(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "signup.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
