package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class PanchayatDashController {

    @FXML
    private Button viewComplaintsButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button viewLeaderboardButton;

    // --- Handlers for Dashboard Buttons ---

    @FXML
    private void handleViewComplaints() {
        showAlert("Complaints", "This will open the complaints review page.");
        // TODO: Load Complaints Page
    }

    @FXML
    private void handleViewReports() {
        showAlert("Reports", "This will open the real-time reports page.");
        // TODO: Load Reports Page
    }

    @FXML
    private void handleViewLeaderboard() {
        showAlert("Leaderboard", "This will open the cleanest street leaderboard.");
        // TODO: Load Leaderboard Page
    }

    // --- Utility Method ---
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
