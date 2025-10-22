package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

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
        loadPage("/com/demo/Submitcomplaints.fxml", "Review Complaints");
        // TODO: Load Complaints Page
    }

    @FXML
    private void handleViewReports() {
        showAlert("Reports", "This will open the real-time reports page.");
        // TODO: Load Reports Page
    }

    @FXML
    private void handleViewLeaderboard() {
        loadPage("/com/demo/cleanest.fxml", "Leaderboard");
    }

    // --- Utility Method ---
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadPage(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) (root.getScene() == null
                    ? Stage.getWindows().stream().filter(w -> w.isShowing()).findFirst().get()
                    : root.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
