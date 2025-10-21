package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class WorkerDashController {

    @FXML
    private Button viewComplaintsButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button viewLeaderboardButton;

    // --- Handlers for Dashboard Buttons ---

    @FXML
    private void handleaddslots(ActionEvent event) {
        loadPage("/com/demo/addAvailableSlotsK.fxml", "Add Slots",event);
    }

    @FXML
    private void handleViewReports(ActionEvent event) {
        showAlert("Reports", "This will open the real-time reports page.");
        // TODO: Load Reports Page
    }

    @FXML
    private void handleViewBooked(ActionEvent event) {
        loadPage("/com/demo/cleanest.fxml", "Leaderboard",event);
    }

    // --- Utility Method ---
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadPage(String fxmlPath, String title, ActionEvent event) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                showAlert("Error", "Cannot find FXML file: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load: " + fxmlPath);
        }
    }

}

