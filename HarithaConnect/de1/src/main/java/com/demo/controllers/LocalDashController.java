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

import java.io.IOException;
import java.util.Optional;

public class LocalDashController {
    @FXML private Button logoutButton;
    private void loadPage(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Find the currently open stage
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(w -> w.isShowing())
                    .findFirst()
                    .orElse(null);

            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToSchedulePickup() {
        loadPage("/com/demo/SchedulePickup.fxml", "Schedule Pickup");
    }

    @FXML
    private void goToReportDump() {
        loadPage("/com/demo/ReportDump.fxml", "Report Dump");
    }

    @FXML
    private void goToLeaderboard() {
        loadPage("/com/demo/cleanest.fxml", "Leaderboard");
    }

    @FXML
    private void goToEwaste() {
        loadPage("/com/demo/Ewastecentres.fxml", "Nearby E-Waste Centres");
    }

    @FXML
    private void goToComplaint() {
        loadPage("/com/demo/viewcomplaints.fxml", "Submit Complaint");
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
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
