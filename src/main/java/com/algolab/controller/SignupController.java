package com.algolab.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;
import com.algolab.util.SceneSwitcher;
import com.algolab.util.EmailSender;
import com.algolab.util.VerificationService;
import com.algolab.model.UserAccount;

public class SignupController {

    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 6;

    @FXML
    private TextField gmailTextField;
    @FXML
    private PasswordField passwordTextField;

    @FXML
    private StackPane errorContainer;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        hideError();

        gmailTextField.setOnAction(e -> signup(e));
        passwordTextField.setOnAction(e -> signup(e));
    }

    public void signup(ActionEvent e) {
        String gmail = gmailTextField.getText();
        String password = passwordTextField.getText();

        UserAccount user = new UserAccount(gmail, password);
        try {
            if (inputIsValid()) {
                // Check username / email availability BEFORE verification
                if (!user.isAvailable()) {
                    showError(user.getLastError()); // show "Email already registered" etc.
                    return;
                }
                // 1. start verification
                VerificationService.startVerification(gmail);

                // 2. save user temporarily (do NOT insert into DB yet)
                VerificationService.setPendingUser(user);

                // 3. get code
                String code = VerificationService.getCode();

                // 4. send email
                EmailSender.sendVerificationEmail(gmail, code);

                // 5. go to verification page
                switchToVerification(e);
            }
        } catch (IOException error) {
            System.out.println("error: " + error.getMessage());
        }
    }

    public void switchToLogIn(ActionEvent e) throws IOException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        SceneSwitcher.switchTo(stage, "login.fxml");
    }

    public boolean inputIsValid() {
        boolean isValid = true;
        // PASSWORD
        if (isEmpty(passwordTextField)) {
            showErrorHighlight(passwordTextField);
            showError("password is empty");
            isValid = false;
        } else if (!isBigger(passwordTextField)) {
            showErrorHighlight(passwordTextField);
            showError("Password must contain at least " + MIN_PASSWORD_LENGTH + " characters.");
            isValid = false;
        } else if (!hasCapital(passwordTextField)) {
            showErrorHighlight(passwordTextField);
            showError("Password must contain at least one uppercase letter.");
            isValid = false;
        } else if (!hasLowerCase(passwordTextField)) {
            showErrorHighlight(passwordTextField);
            showError("Password must contain at least one lowercase letter.");
            isValid = false;
        } else if (!hasNumber(passwordTextField)) {
            showErrorHighlight(passwordTextField);
            showError("Password must contain at least one number.");
            isValid = false;
        } else {
            clearErrorHighlight(passwordTextField);
        }

        // GMAIL
        if (!isGmail(gmailTextField)) {
            showErrorHighlight(gmailTextField);
            showError("Wrong gmail format. Example: abc123@gmail.com");
            isValid = false;
        } else {
            clearErrorHighlight(gmailTextField);
        }

        return isValid;
    }

    public boolean isEmpty(TextField text) {
        return text.getText().trim().isEmpty();
    }

    private boolean isGmail(TextField field) {
        String email = field.getText();
        // IMPROVED: Better email validation
        return email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$");
    }

    // Password Strength Tests

    private boolean isBigger(TextField field) {
        return (field.getText().length() >= MIN_PASSWORD_LENGTH);
    }

    private boolean hasLowerCase(TextField field) {
        return field.getText().matches(".*[a-z].*");
    }

    private boolean hasCapital(TextField field) {
        return field.getText().matches(".*[A-Z].*");
    }

    private boolean hasNumber(TextField field) {
        return field.getText().matches(".*[0-9].*");
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

    @FXML
    public void switchToVerification(ActionEvent e) throws IOException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        VerificationController controller = new VerificationController();
        controller.initializeWithEmail(VerificationService.getEmail());
        SceneSwitcher.switchToWithController(stage, "common_verification.fxml", controller);
    }

}