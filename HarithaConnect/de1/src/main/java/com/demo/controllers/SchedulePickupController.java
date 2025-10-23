package com.demo.controllers;

import com.demo.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulePickupController {

    @FXML private TextField wardField;
    @FXML private DatePicker datePicker;
    @FXML private GridPane timeSlotGrid;
    @FXML private VBox scheduledList;
    @FXML private Button loadSlotsButton;
    @FXML private Button confirmButton;
    @FXML private VBox mapSection;
    @FXML private WebView mapView;
    @FXML private Label selectedLocationLabel;
    @FXML private Button backButton;
    private String selectedSlotId = null;
    private String selectedCoordinates = null;

    private static final String TIMESLOTS_FILE = "/com/demo/data/timeslots.csv";
    private static final String PICKUPS_FILE = "/com/demo/data/pickups.csv";
    private static final String WORKERS_FILE = "/com/demo/data/kudumbasreeuserdata.csv";


    @FXML
    public void initialize() {
        loadSlotsButton.setOnAction(e -> loadAvailableSlots());
        confirmButton.setOnAction(e -> confirmPickup());
        loadScheduledPickups();
    }

    private void loadAvailableSlots() {
        String wardInput = wardField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (wardInput.isEmpty() || date == null) {
            showAlert("Error", "Please enter ward number and date.");
            return;
        }

        List<String> workerIds = getWorkersByWard(wardInput);
        if (workerIds.isEmpty()) {
            showAlert("Error", "No Kudumbasree workers found for this ward.");
            return;
        }

        timeSlotGrid.getChildren().clear();

        try (InputStream is = getClass().getResourceAsStream(TIMESLOTS_FILE)) {
            if (is == null) throw new FileNotFoundException("Could not find timeslots.csv in resources.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0, col = 0;
            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                String slotId = parts[0];
                String workerId = parts[1];
                String slotDate = parts[2];
                String timeSlot = parts[3];
                int capacity = Integer.parseInt(parts[4]);
                boolean isOpen = Boolean.parseBoolean(parts[5]);

                // Debug line (optional)
                System.out.println("DEBUG: " + workerId + " | " + slotDate + " | " + date + " | open=" + isOpen + " | cap=" + capacity);

                if (workerIds.contains(workerId) && slotDate.equals(date.toString()) && isOpen && capacity > 0) {
                    Button btn = new Button(timeSlot + " (" + capacity + ")");
                    btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    btn.setOnAction(ev -> {
                        selectedSlotId = slotId;
                        timeSlotGrid.getChildren().forEach(n -> n.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));
                        btn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
                        showMap();
                        mapSection.setVisible(true);
                        confirmButton.setVisible(true);
                    });
                    timeSlotGrid.add(btn, col++, row);
                    if (col == 3) { col = 0; row++; }
                }
            }
            timeSlotGrid.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load time slots.");
        }
    }
    public void updateCoordinates(String coords) {
        System.out.println("Map clicked, received coords: " + coords);
        selectedCoordinates = coords;
        selectedLocationLabel.setText("Selected Location: " + coords);
    }


    private List<String> getWorkersByWard(String wardNo) {
        List<String> workerIds = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream("/com/demo/data/kudumbasreeuserdata.csv")) {
            if (is == null) {
                throw new FileNotFoundException("Could not find kudumbasreeuserdata.csv in resources.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                br.readLine(); // skip header
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7 && parts[6].trim().equals(wardNo.trim())) {
                        workerIds.add(parts[0]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workerIds;
    }


    @SuppressWarnings("removal")
    private void showMap() {
        WebEngine engine = mapView.getEngine();

        String html = """
        <html>
        <head>
          <meta name='viewport' content='width=device-width, initial-scale=1.0'>
          <link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css'/>
          <script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>
        </head>
        <body>
          <div id='map' style='width:100%;height:300px;'></div>
          <script>
            var map = L.map('map').setView([10.8505, 76.2711], 8);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19
            }).addTo(map);
            var marker;
            map.on('click', function(e) {
                if (marker) map.removeLayer(marker);
                marker = L.marker(e.latlng).addTo(map);
                var coords = e.latlng.lat.toFixed(5) + "," + e.latlng.lng.toFixed(5);
                if (window.java && window.java.updateCoords) {
                    window.java.updateCoords(coords);
                } else {
                    alert("Java bridge not connected!");
                }
            });
          </script>
        </body>
        </html>
    """;

        engine.loadContent(html);

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                try {
                    @SuppressWarnings("removal")
                    netscape.javascript.JSObject window =
                            (netscape.javascript.JSObject) engine.executeScript("window");
                    window.setMember("java", new MapBridge(this));
                    System.out.println("✅ MapBridge connected successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void confirmPickup() {
        if (selectedSlotId == null || selectedCoordinates == null) {
            showAlert("Error", "Select a time slot and location.");
            return;
        }

        String username = Session.getCurrentUsername();
        if (username == null) {
            showAlert("Error", "No user logged in.");
            return;
        }

        try {
            // ✅ 1. Update timeslot capacity (resources version)
            Path timeslotPath = Paths.get("src/main/resources/com/demo/data/timeslots.csv");
            List<String> lines = Files.readAllLines(timeslotPath);
            List<String> updated = new ArrayList<>();
            updated.add(lines.get(0)); // header

            String assignedWorker = ""; // <-- to store the worker username

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts[0].equals(selectedSlotId)) {
                    assignedWorker = parts[1]; // <-- get the worker username
                    int capacity = Integer.parseInt(parts[4]) - 1;
                    parts[4] = String.valueOf(Math.max(0, capacity));
                    if (capacity <= 0) parts[5] = "false";
                }
                updated.add(String.join(",", parts));
            }
            Files.write(timeslotPath, updated);

            // ✅ 2. Always write pickups.csv inside same resources folder
            Path pickupPath = Paths.get("src/main/resources/com/demo/data/pickups.csv");
            File pickupFile = pickupPath.toFile();

            boolean addHeader = !pickupFile.exists() || pickupFile.length() == 0;
            pickupFile.getParentFile().mkdirs(); // ensure directories exist

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(pickupFile, true))) {
                if (addHeader) {
                    bw.write("pickupId,username,wasteType,timeSlotId,location,status,assignedWorker,createdAt\n");
                }
                String pickupId = UUID.randomUUID().toString();
                String now = java.time.LocalDateTime.now().toString();
                String wasteType = "Mixed";
                bw.write(String.join(",", pickupId, username, wasteType, selectedSlotId, selectedCoordinates, "Pending", assignedWorker, now));
                bw.newLine();
            }

            showAlert("Success", "Pickup scheduled successfully!");
            loadScheduledPickups();

            // ✅ Debug: show where file was actually written
            System.out.println("DEBUG → pickup saved to: " + pickupFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to confirm pickup.");
        }
    }



    private void loadScheduledPickups() {
        scheduledList.getChildren().clear();
        String username = Session.getCurrentUsername();
        if (username == null) return;

        Path pickupPath = Paths.get("src/main/resources/com/demo/data/pickups.csv");
        if (!Files.exists(pickupPath)) return;

        try (BufferedReader br = new BufferedReader(new FileReader(pickupPath.toFile()))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8 && parts[1].equals(username)) {
                    Label lbl = new Label("Pickup ID: " + parts[0] + " | Status: " + parts[6]);
                    lbl.setPadding(new Insets(5));
                    lbl.setStyle("-fx-background-color: #e0ffe0; -fx-border-color: #a5d6a7;");
                    scheduledList.getChildren().add(lbl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/LocalDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
