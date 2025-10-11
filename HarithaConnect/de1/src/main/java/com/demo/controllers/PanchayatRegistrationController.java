package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class PanchayatRegistrationController {

    @FXML private TextField fullNameField;
    @FXML private TextField panchayatIdField;
    @FXML private TextField designationField;
    @FXML private TextField officialEmailField;
    @FXML private TextField mobileField;
    @FXML private TextField officeAddressField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button createAccountButton;
    @FXML private Button homeButton;

    // ✅ Same path used for Panchayat login and registration
    private static final String CSV_PATH = "src/main/resources/com/demo/data/panchayatuserdata.csv";

    @FXML
    public void initialize() {
        createAccountButton.setOnAction(this::handleCreateAccount);
    }

    // ---------------------------
    // ✅ Handle Account Creation
    // ---------------------------
    private void handleCreateAccount(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String panchayatId = panchayatIdField.getText().trim();
        String designation = designationField.getText().trim();
        String email = officialEmailField.getText().trim();
        String mobile = mobileField.getText().trim();
        String officeAddress = officeAddressField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Basic field validation
        if (fullName.isEmpty() || panchayatId.isEmpty() || designation.isEmpty()
                || email.isEmpty() || officeAddress.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all required fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR,"Error", "Please enter a valid email address!");
            return;
        }

        if (!isStrongPassword(password)) {
            showAlert(Alert.AlertType.ERROR,"Weak Password",
                    "Password must be at least 8 characters long and include:\n" +
                            "• an uppercase letter\n• a lowercase letter\n• a number\n• a special character");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }
        if (isUsernameTaken(panchayatId)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Panchayat ID", "ID is alread registered. Please try again.");
            return;
        }


        try {
            File file = new File(CSV_PATH);
            boolean fileExists = file.exists();

            // ✅ Append new data to existing CSV
            try (FileWriter writer = new FileWriter(file, true)) {
                if (!fileExists) {
                    writer.write("FullName,PanchayatID,Designation,Email,Mobile,OfficeAddress,Password\n");
                }
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                        panchayatId, password, fullName, designation, email, mobile, officeAddress));
            }

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Account created successfully! Redirecting to login...");

            goToLogin(event);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error",
                    "Could not save registration data: " + e.getMessage());
        }
    }

    // ---------------------------
    // ✅ Redirect to Login Page
    // ---------------------------
    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/demo/PanchayatLogin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Unable to open login page: " + e.getMessage());
        }
    }

    // ---------------------------
    // ✅ Go Back to Homepage
    // ---------------------------
    @FXML
    private void goHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/demo/HomePage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Unable to return to homepage: " + e.getMessage());
        }
    }

    // ---------------------------
    // ✅ Alert Utility
    // ---------------------------
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean isUsernameTaken(String username) {
        try {
            if (!Files.exists(Paths.get(CSV_PATH))) return false;

            return Files.lines(Paths.get(CSV_PATH))
                    .skip(1) // skip header
                    .map(line -> line.split(",")[0].trim()) // first column = username
                    .anyMatch(existing -> existing.equals(username));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isStrongPassword(String password) {
        // At least one uppercase, one lowercase, one digit, one special char, 8+ length
        String strongPassRegex =
                "^(?=.*[0-9])" +           // digit
                        "(?=.*[a-z])" +            // lowercase
                        "(?=.*[A-Z])" +            // uppercase
                        "(?=.*[@#$%^&+=!])" +      // special char
                        "(?=\\S+$).{8,}$";         // no spaces + min 8 chars
        return Pattern.matches(strongPassRegex, password);
    }
}