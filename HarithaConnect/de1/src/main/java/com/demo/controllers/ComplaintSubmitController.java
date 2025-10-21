package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import com.demo.Session;

public class ComplaintSubmitController {

    @FXML private TextField mobileNumberField;
    @FXML private TextArea complaintDescriptionArea;
    @FXML private Button submitButton;
    @FXML private Button backButton;
    @FXML private VBox complaintsListContainer;
    @FXML private VBox complaintsSection;

    private static final String COMPLAINTS_FILE = "src/main/resources/com/demo/data/complaints.csv";

    @FXML
    private void initialize() {
        loadUserComplaints();
    }

    /**
     * Handles submission of a new complaint.
     */
    @FXML
    private void handleSubmitComplaint() {
        String mobile = mobileNumberField.getText().trim();
        String description = complaintDescriptionArea.getText().trim();

        if (mobile.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields before submitting.");
            return;
        }

        String username = Session.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            showAlert("Error", "User not logged in.");
            return;
        }

        try {
            Path filePath = Paths.get(COMPLAINTS_FILE);
            boolean addHeader = !Files.exists(filePath);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
                if (addHeader) {
                    bw.write("complaintId,submittedBy,description,status,submittedDate");
                    bw.newLine();
                }

                String complaintId = UUID.randomUUID().toString();
                String status = "Pending";
                String submittedDate = LocalDateTime.now().toString();

                String record = String.join(",", complaintId, username, description.replace(",", " "), status, submittedDate);
                bw.write(record);
                bw.newLine();
            }

            showAlert("Success", "Complaint submitted successfully!");
            mobileNumberField.clear();
            complaintDescriptionArea.clear();

            // refresh list
            loadUserComplaints();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save complaint.");
        }
    }

    /**
     * Loads all complaints submitted by the current logged-in user.
     */
    private void loadUserComplaints() {
        String username = Session.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            return;
        }

        complaintsListContainer.getChildren().clear();

        Path filePath = Paths.get(COMPLAINTS_FILE);
        if (!Files.exists(filePath)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            if (lines.size() <= 1) return; // only header

            complaintsSection.setVisible(true);

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",", -1);
                if (parts.length < 5) continue;

                String submittedBy = parts[1];
                String description = parts[2];
                String status = parts[3];
                String date = parts[4];

                if (submittedBy.equals(username)) {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-background-color: #f1f8e9; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #c5e1a5;");

                    Label descLabel = new Label("📝 " + description);
                    descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                    Label statusLabel = new Label("Status: " + status);
                    statusLabel.setStyle(status.equalsIgnoreCase("Pending")
                            ? "-fx-text-fill: orange; -fx-font-weight: bold;"
                            : "-fx-text-fill: green; -fx-font-weight: bold;");

                    Label dateLabel = new Label("Submitted: " + date.substring(0, 16));
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

                    card.getChildren().addAll(descLabel, statusLabel, dateLabel);
                    complaintsListContainer.getChildren().add(card);
                }
            }

            if (complaintsListContainer.getChildren().isEmpty()) {
                Label emptyLabel = new Label("No complaints submitted yet.");
                emptyLabel.setStyle("-fx-text-fill: #666;");
                complaintsListContainer.getChildren().add(emptyLabel);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the back button to go back to local dashboard.
     */
    @FXML
    private void handleBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/LocalDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Local Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to go back to dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
