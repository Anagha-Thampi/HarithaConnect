package com.demo.controllers;

import com.demo.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Optional;

public class PanchayatDashController {

    @FXML
    private Button logoutButton,viewComplaintsButton;

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
        loadPage("/com/demo/ViewDumpReport.fxml", "View Reports");
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

            Stage stage;
            // Try to get the current window
            Window window = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);

            if (window instanceof Stage) {
                stage = (Stage) window;
            } else {
                stage = new Stage(); // create a new stage if none found
            }

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
