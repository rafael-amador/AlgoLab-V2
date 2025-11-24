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
import com.algolab.util.PasswordResetService;

public class VerifyResetCodeController {

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

        String email = PasswordResetService.getEmail();
        if (email != null) {
            emailedYouLabel.setText("We emailed a code to " + email + ". Enter it below:");
        }
    }

    private void setupBox(TextField field, TextField next, TextField prev) {
        field.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("\\d?"))
                return null;
            return change;
        }));

        field.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() == 1 && next != null)
                next.requestFocus();
        });

        field.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case BACK_SPACE:
                    if (field.getText().isEmpty() && prev != null)
                        prev.requestFocus();
                    break;
                default:
                    break;
            }
        });

        field.setOnAction(e -> verifyCode(e));
    }

    @FXML
    private void verifyCode(ActionEvent e) {
        String code = d1.getText() + d2.getText() + d3.getText() + d4.getText() + d5.getText() + d6.getText();

        if (code.length() != 6) {
            showError("Please enter all 6 digits.");
            return;
        }

        if (PasswordResetService.verifyCode(code)) {
            // Navigate to Reset Password Screen
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            try {
                SceneSwitcher.switchTo(stage, "reset_password.fxml");
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Error loading reset screen.");
            }
        } else {
            showError("Incorrect code. Try again.");
        }
    }

    @FXML
    private void cancel(ActionEvent e) {
        PasswordResetService.clear();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        try {
            SceneSwitcher.switchTo(stage, "login.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
}
