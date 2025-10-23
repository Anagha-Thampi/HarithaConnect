package com.demo.controllers;

import com.demo.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class WorkerDashController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button viewComplaintsButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button viewLeaderboardButton;

    // --- Handlers for Dashboard Buttons ---

    @FXML
    private void handleaddslots(ActionEvent event) {
        loadPage("/com/demo/addAvailableSlotsK.fxml", "Add Slots", event);
    }



    @FXML
    private void handleViewReports(ActionEvent event) {
        loadPage("/com/demo/ViewDumpReport.fxml", "Booked", event);
    }



    @FXML
    private void handleViewBooked(ActionEvent event) {
        loadPage("/com/demo/viewBookedPickups.fxml", "Booked", event);
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

    @FXML
    private void onLogoutButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Your unsaved progress will be lost.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Session.clear();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/LoginSelection.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

