package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LocalDashController {

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
        loadPage("/com/demo/Ewaste.fxml", "Nearby E-Waste Centres");
    }

    @FXML
    private void goToComplaint() {
        loadPage("/com/demo/viewcomplaints.fxml", "Submit Complaint");
    }
}
