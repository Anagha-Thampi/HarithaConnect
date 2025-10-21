package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import com.demo.Session;
import com.demo.KudumbasreeWorker;
import com.demo.TimeSlot;

public class AddTimeSlotsController {

    @FXML private DatePicker datePicker;
    @FXML private TextField timeRangeField;
    @FXML private TextField maxSlotsField;
    @FXML private Label statusLabel;
    @FXML private VBox slotList;
    @FXML private Button addSlotButton;
    @FXML private Button backButton;

    private static final String TIMESLOT_FILE = "src/main/resources/com/demo/data/timeslots.csv";

    @FXML
    private void initialize() {
        loadExistingSlots();

        addSlotButton.setOnAction(event -> handleAddSlot());
        backButton.setOnAction(this::handleBackAction);
    }

    /**
     * Add a new time slot for the current logged-in worker using KudumbasreeWorker.addAvailableTimeSlots()
     */
    private void handleAddSlot() {
        LocalDate selectedDate = datePicker.getValue();
        String timeRange = timeRangeField.getText().trim();
        String capacityText = maxSlotsField.getText().trim();

        if (selectedDate == null || timeRange.isEmpty() || capacityText.isEmpty()) {
            showStatus("Please fill all fields.", "red");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("Enter a valid number for slots.", "red");
            return;
        }

        String workerUsername = Session.getCurrentUsername();
        if (workerUsername == null || workerUsername.isEmpty()) {
            showStatus("No worker logged in.", "red");
            return;
        }

        try {
            // Create KudumbasreeWorker instance
            KudumbasreeWorker worker = new KudumbasreeWorker(workerUsername,"");

            // Create new TimeSlot object
            String slotId = UUID.randomUUID().toString();
            TimeSlot slot = new TimeSlot(slotId, workerUsername, selectedDate.toString(), timeRange, capacity, true);

            // Use existing addAvailableTimeSlots() function
            worker.addAvailableTimeSlots(Collections.singletonList(slot));

            showStatus("Slot added successfully!", "#2d5a27");
            clearFields();
            loadExistingSlots();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Error saving slot.", "red");
        }
    }

    /**
     * Loads existing time slots for this worker and shows in the right panel.
     */
    private void loadExistingSlots() {
        slotList.getChildren().clear();

        String workerUsername = Session.getCurrentUsername();
        if (workerUsername == null || workerUsername.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(TIMESLOT_FILE);
        if (!Files.exists(filePath)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            if (lines.size() <= 1) return; // only header

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",", -1);
                if (parts.length < 6) continue;

                String slotWorker = parts[1];
                String date = parts[2];
                String timeSlot = parts[3];
                String capacity = parts[4];
                String isOpen = parts[5];

                if (slotWorker.equals(workerUsername)) {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-background-color: #f9fff9; -fx-padding: 10; -fx-border-color: #bcdcbc; -fx-background-radius: 5;");

                    Label dateLabel = new Label("📅 " + date);
                    dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d5a27;");

                    Label timeLabel = new Label("⏰ " + timeSlot);
                    timeLabel.setStyle("-fx-text-fill: #333;");

                    Label capacityLabel = new Label("Slots: " + capacity);
                    capacityLabel.setStyle("-fx-text-fill: #555;");

                    Label openLabel = new Label("Status: " + (isOpen.equals("true") ? "Open" : "Closed"));
                    openLabel.setStyle(isOpen.equals("true")
                            ? "-fx-text-fill: green; -fx-font-weight: bold;"
                            : "-fx-text-fill: red; -fx-font-weight: bold;");

                    card.getChildren().addAll(dateLabel, timeLabel, capacityLabel, openLabel);
                    slotList.getChildren().add(card);
                }
            }

            if (slotList.getChildren().isEmpty()) {
                Label empty = new Label("No slots added yet.");
                empty.setStyle("-fx-text-fill: #666;");
                slotList.getChildren().add(empty);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        datePicker.setValue(null);
        timeRangeField.clear();
        maxSlotsField.clear();
    }

    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    /**
     * Goes back to the Kudumbasree dashboard.
     */
    private void handleBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/WrokerDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Kudumbasree Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Error loading dashboard.", "red");
        }
    }
}
