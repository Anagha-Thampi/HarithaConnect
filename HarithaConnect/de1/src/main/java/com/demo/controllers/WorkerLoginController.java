package com.demo.controllers;

import com.demo.KudumbasreeWorker;
import com.demo.LocalUser;
import com.demo.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WorkerLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        User user = new KudumbasreeWorker(username, password);

        if (user.login(username, password)) {
            System.out.println("Worker user logged in: " + user.getUsername());
            loadPage("/com/demo/WorkerDash.fxml", "Local Dashboard");
        } else {
            showAlert("Error", "Invalid credentials for Kudumbasree User.");
        }
    }

    // Navigate to the Register Page
    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        loadPage("/com/demo/WorkerRegistration.fxml", "Register Account");
    }

    // Navigate to the Forgot Password Page
    @FXML
    private void goToForgotPassword(ActionEvent event) throws IOException {
        loadPage("/com/demo/ForgotPassword.fxml", "Forgot Password");
    }

    private void loadPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load: " + fxmlPath);
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void goToLogin() throws Exception {
        System.out.println("log button clicked!");
        loadPage("/com/demo/LoginSelection.fxml", "Login");
    }

    @FXML
    private void goHome(ActionEvent event) {
        System.out.println("Home button clicked!");
        loadPage("/com/demo/homepage.fxml", "Home");
    }

}
