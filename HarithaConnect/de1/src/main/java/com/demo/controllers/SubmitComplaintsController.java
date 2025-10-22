package com.demo.controllers;

import com.demo.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SubmitComplaintsController {

    @FXML
    private VBox complaintsContainer;

    @FXML
    private Label noComplaintsLabel;

    @FXML
    public void initialize() {
        loadComplaints();
    }

    private void loadComplaints() {
        complaintsContainer.getChildren().clear();
        List<String[]> complaints = DataManager.readCsv("complaints.csv");

        if (complaints.isEmpty()) {
            noComplaintsLabel.setVisible(true);
            return;
        }

        noComplaintsLabel.setVisible(false);

        for (String[] data : complaints) {
            if (data.length < 5) continue;

            String complaintId = data[0];
            String username = data[1];
            String name = getNameFromLocalUserData(username);
            String description = data[2];
            String status = data[3];
            String submittedDate = data[4];

            VBox card = new VBox(8);
            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 20;" +
                            "-fx-border-color: #dfe6e9;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
                            "-fx-font-family: 'Segoe UI';"
            );

            // Left accent strip
            Region accent = new Region();
            accent.setPrefWidth(5);
            accent.setStyle("-fx-background-color: " + getStatusColor(status) + ";");

            Label idLabel = new Label("Complaint ID: " + complaintId);
            idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-text-fill: #1e5631;");

            Label descLabel = new Label(description);
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333333;");

            Label userLabel = new Label("Submitted By: " + name );
            userLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #555555;");

            Label dateLabel = new Label("Submitted Date: " + submittedDate);
            dateLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #555555;");

            Button statusButton = new Button(status);
            statusButton.setStyle(getStatusButtonStyle(status));
            statusButton.setOnAction(e -> {
                String newStatus = showStatusChangeDialog(status);
                if (newStatus != null && !newStatus.equals(status)) {
                    statusButton.setText(newStatus);
                    statusButton.setStyle(getStatusButtonStyle(newStatus));
                    updateComplaintStatus(complaintId, newStatus);
                    accent.setStyle("-fx-background-color: " + getStatusColor(newStatus) + ";");
                }
            });

            HBox topRow = new HBox(10, accent, idLabel);
            topRow.setStyle("-fx-alignment: center-left;");

            HBox bottomRow = new HBox(10, statusButton);
            bottomRow.setStyle("-fx-alignment: center-left; -fx-padding: 5 0 0 0;");

            card.getChildren().addAll(topRow, descLabel, userLabel, dateLabel, bottomRow);
            complaintsContainer.getChildren().add(card);
        }
    }

    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending": return "#e74c3c";
            case "in progress": return "#f39c12";
            case "completed": return "#27ae60";
            default: return "#7f8c8d";
        }
    }

    private String getStatusButtonStyle(String status) {
        return "-fx-background-color: " + getStatusColor(status) + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 15;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 4 12 4 12;";
    }

    private String getNameFromLocalUserData(String username) {
        List<String[]> users = DataManager.readCsv("localuserdata.csv");
        for (String[] u : users) {
            if (u.length >= 4 && u[0].equals(username)) {
                return u[3]; // name column
            }
        }
        return username; // fallback
    }

    private String showStatusChangeDialog(String currentStatus) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(currentStatus, "Pending", "In Progress", "Completed");
        dialog.setTitle("Update Complaint Status");
        dialog.setHeaderText("Select new status");
        dialog.setContentText("Status:");
        return dialog.showAndWait().orElse(null);
    }

    private void updateComplaintStatus(String complaintId, String newStatus) {
        List<String[]> rows = DataManager.readCsv("complaints.csv");
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r.length > 0 && r[0].equals(complaintId)) {
                r[3] = newStatus;
                rows.set(i, r);
                break;
            }
        }
        DataManager.overwriteCsv("complaints.csv", rows);
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/PanchayatDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) complaintsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}