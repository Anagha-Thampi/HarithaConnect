package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

public class ForgotPasswordController {

    @FXML
    private Button homeButton;

    @FXML
    private Button aboutButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button sendButton;

    @FXML
    private Hyperlink backToLogin;

    @FXML
    private TextField emailField;

    private final String CSV_FILE = "src/main/resources/com/demo/data/localuserdata.csv";
    // ========================
    // Navigation
    // ========================

    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        switchScene("/com/demo/homepage.fxml", event);
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        switchScene("/com/demo/loginselection.fxml", event);
    }
    @FXML
    private void goToAbout(ActionEvent event) throws IOException {
        System.out.println("Home button clicked!");
        switchScene("/com/demo/about.fxml", event);
    }

    private void switchScene(String fxmlPath, ActionEvent event) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            showAlert("Error", "Cannot find FXML file: " + fxmlPath);
            return;
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    // ========================
    // Send Reset Email Logic
    // ========================

    @FXML
    private void sendResetLink(ActionEvent event) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showAlert("Error", "Please enter your registered email.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[6].equalsIgnoreCase(email)) {
                    String password = parts[1];
                    sendEmail(email, password);
                    showAlert("Success", "Your password has been sent to: " + email);
                    found = true;
                    break;
                }
            }

            if (!found) {
                showAlert("Error", "No account found with that email.");
            }

        } catch (IOException e) {
            showAlert("Error", "Error reading CSV file.");
            e.printStackTrace();
        }
    }

    // ========================
    // Email Sending
    // ========================
    private void sendEmail(String recipient, String password) {
        // Mock email sending — replace with real email logic (JavaMail, etc.)
        System.out.println("Sending password to " + recipient + ": " + password);
    }/*
    private void sendEmail(String recipient, String password) {
        final String senderEmail = "your.email@gmail.com";      // 🔸 Replace with your sender address
        final String senderPassword = "your-app-password";      // 🔸 Use Gmail App Password, not normal password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Haritha Connect - Password Reset");
            message.setText("Hello,\n\nYour password is: " + password +
                    "\n\nPlease keep it safe.\n\n- Haritha Connect Team");

            Transport.send(message);
            System.out.println("Password sent successfully to " + recipient);

        } catch (MessagingException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send email: " + e.getMessage());
        }
    }
*/
    // ========================
    // Utility
    // ========================

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
