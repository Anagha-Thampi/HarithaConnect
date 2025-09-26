package com.demo.controllers;

import com.demo.WastePickup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class SchedulePickupController {

    @FXML
    private DatePicker pickupDate;

    @FXML
    private ChoiceBox<String> pickupTime;

    @FXML
    private Label statusLabel;

    private WastePickup wastePickup; // domain class

    @FXML
    public void initialize() {
        pickupTime.getItems().addAll("Morning (8-10 AM)", "Afternoon (12-2 PM)", "Evening (4-6 PM)");
    }

    @FXML
    private void confirmPickup() {
        LocalDate date = pickupDate.getValue();
        String time = pickupTime.getValue();

        if (date == null || time == null) {
            statusLabel.setText("Please select both date and time.");
            return;
        }

        // Connect to WastePickup class
        wastePickup = new WastePickup("General Waste", date.atStartOfDay(), "User Location", "Scheduled", false);
        statusLabel.setText("Pickup scheduled: " + wastePickup.getWasteType() + " on " + date + " (" + time + ")");
    }
    @FXML
    private Button dashbutton;
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/demo/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goDash(ActionEvent event) {
        System.out.println("Home button clicked!");
        switchScene(event, "LocalDash.fxml");
    }
}
