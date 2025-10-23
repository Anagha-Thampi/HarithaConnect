package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class LocalRegistrationController {

    @FXML private TextField houseNumberField;
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField addressField;
    @FXML private TextField mobileField;
    @FXML private TextField WardField;
    @FXML private TextField PanchayatField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;


    private final String CSV_PATH = "src/main/resources/com/demo/data/localuserdata.csv";

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        String houseNumber = houseNumberField.getText().trim();
        String name = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String address = addressField.getText().trim();
        String mobile = mobileField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String ward=WardField.getText().trim();
        String panchayat=PanchayatField.getText().trim();

        // Validation
        if (houseNumber.isEmpty() || name.isEmpty() || username.isEmpty() ||
                address.isEmpty() || ward.isEmpty()||panchayat.isEmpty()||mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all required fields.");
            return;
        }
        if(email.isEmpty()){email="nil";}


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

        if (!mobile.matches("\\d{10}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile", "Enter a valid 10-digit mobile number.");
            return;
        }
        if (isUsernameTaken(username)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Username", "This username is already taken. Please choose another.");
            return;
        }
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
            boolean fileExists = Files.exists(Paths.get(CSV_PATH));

            try (FileWriter writer = new FileWriter(CSV_PATH, true)) {
                if (!fileExists) {
                    writer.write("Username,Password,HouseNumber,FullName,Address,Mobile,Email,Ward\n");
                }
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%s",
                        username, password, houseNumber, name, address, mobile, email,ward,panchayat,System.lineSeparator()));
            }

            // Show alert and redirect to login
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! Redirecting to login...");
            alert.showAndWait();

            goToLogin(event);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save user data: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/localLogin.fxml"));
            BorderPane loginPage = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginPage, 1000, 700));
            stage.setTitle("User Login - Haritha Connect");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load login page: " + e.getMessage());
        }
    }

    @FXML
    private void goToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/homepage.fxml"));
            BorderPane homePage = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homePage, 1000, 700));
            stage.setTitle("Haritha Connect - Home");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load homepage: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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