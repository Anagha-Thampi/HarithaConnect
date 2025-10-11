package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for SchedulePickup.fxml
 * Handles:
 *  - Loading wards from CSV
 *  - Displaying available time slots based on selected ward
 *  - Scheduling new pickups (writes to CSV)
 *  - Loading already scheduled pickups
 *  - Handling dashboard navigation and logout
 */
public class SchedulePickupController {

    @FXML private ComboBox<String> wardComboBox;
    @FXML private Button loadSlotsButton;
    @FXML private GridPane timeSlotGrid;
    @FXML private Button confirmPickupButton;
    @FXML private Label successMessage;
    @FXML private VBox scheduledPickupBox;
    @FXML private VBox pickupListBox;
    @FXML private Button backToDashboardButton;
    @FXML private MenuItem logoutMenuItem;

    // File paths — adjust to match your project structure
    private final String WARD_SLOTS_CSV = "src/main/resources/data/ward_slots.csv";
    private final String SCHEDULED_PICKUPS_CSV = "src/main/resources/data/scheduled_pickups.csv";

    // Keeps selected slot in memory
    private String selectedSlot = null;

    @FXML
    public void initialize() {
        loadWardList();
        setupActions();
        loadScheduledPickups();
    }

    /**
     * Loads list of wards into ComboBox from the ward_slots.csv
     * Format: WardName,Slot1|Slot2|Slot3
     */
    private void loadWardList() {
        Set<String> wards = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(WARD_SLOTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length > 0) wards.add(parts[0].trim());
            }
            wardComboBox.getItems().setAll(wards);
        } catch (IOException e) {
            System.err.println("Error loading wards: " + e.getMessage());
        }
    }

    /**
     * Sets up all button/menu actions.
     */
    private void setupActions() {
        loadSlotsButton.setOnAction(e -> onLoadSlots());
        confirmPickupButton.setOnAction(e -> onConfirmPickup());
        backToDashboardButton.setOnAction(this::handleBackToDashboard);
        logoutMenuItem.setOnAction(this::handleLogout);
    }

    /**
     * Loads time slots for the selected ward.
     */
    private void onLoadSlots() {
        String selectedWard = wardComboBox.getValue();
        if (selectedWard == null) {
            showAlert("Please select a ward first.");
            return;
        }

        timeSlotGrid.getChildren().clear();

        List<String> slots = getSlotsForWard(selectedWard);
        if (slots.isEmpty()) {
            showAlert("No slots found for this ward.");
            timeSlotGrid.setVisible(false);
            confirmPickupButton.setVisible(false);
            return;
        }

        int col = 0, row = 0;
        ToggleGroup slotGroup = new ToggleGroup();

        for (String slot : slots) {
            RadioButton rb = new RadioButton(slot);
            rb.setToggleGroup(slotGroup);
            rb.setStyle("-fx-font-size: 14px;");
            timeSlotGrid.add(rb, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        slotGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null)
                selectedSlot = ((RadioButton) newToggle).getText();
        });

        timeSlotGrid.setVisible(true);
        confirmPickupButton.setVisible(true);
    }

    /**
     * Fetches time slots for a ward from CSV file.
     */
    private List<String> getSlotsForWard(String ward) {
        try (BufferedReader br = new BufferedReader(new FileReader(WARD_SLOTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(ward)) {
                    return Arrays.stream(parts[1].split("\\|"))
                            .map(String::trim)
                            .collect(Collectors.toList());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading ward slots CSV: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Confirm and schedule a pickup.
     */
    private void onConfirmPickup() {
        String ward = wardComboBox.getValue();
        if (ward == null || selectedSlot == null) {
            showAlert("Please select a ward and time slot first.");
            return;
        }

        // Check if already scheduled
        if (isPickupAlreadyScheduled(ward, selectedSlot)) {
            showAlert("A pickup for this ward and slot is already scheduled.");
            return;
        }

        try (FileWriter fw = new FileWriter(SCHEDULED_PICKUPS_CSV, true)) {
            fw.write(ward + "," + selectedSlot + "\n");
            successMessage.setVisible(true);
            loadScheduledPickups();
        } catch (IOException e) {
            showAlert("Error saving scheduled pickup.");
        }
    }

    /**
     * Checks if a pickup is already scheduled.
     */
    private boolean isPickupAlreadyScheduled(String ward, String slot) {
        try (BufferedReader br = new BufferedReader(new FileReader(SCHEDULED_PICKUPS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(ward) && parts[1].equalsIgnoreCase(slot))
                    return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    /**
     * Loads and displays scheduled pickups.
     */
    private void loadScheduledPickups() {
        pickupListBox.getChildren().clear();

        List<String> pickups = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SCHEDULED_PICKUPS_CSV))) {
            String line;
            while ((line = br.readLine()) != null)
                pickups.add(line);
        } catch (IOException e) {
            System.err.println("Error loading scheduled pickups: " + e.getMessage());
        }

        if (pickups.isEmpty()) {
            scheduledPickupBox.setVisible(false);
            return;
        }

        scheduledPickupBox.setVisible(true);
        for (String pickup : pickups) {
            String[] parts = pickup.split(",");
            if (parts.length == 2) {
                Label label = new Label("Ward: " + parts[0] + "  |  Slot: " + parts[1]);
                label.setStyle("-fx-background-color: #f0fff0; -fx-padding: 6; -fx-border-color: #a8e6a3; -fx-border-radius: 5;");
                label.setTextAlignment(TextAlignment.CENTER);
                pickupListBox.getChildren().add(label);
            }
        }
    }

    /**
     * Alert helper.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Haritha Connect");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handle going back to dashboard.
     */
    private void handleBackToDashboard(ActionEvent event) {
        System.out.println("Navigating back to dashboard...");
        // TODO: Replace with scene switching logic if needed
    }

    /**
     * Handle logout.
     */
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out...");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
