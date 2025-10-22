package com.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.demo.Session;
import com.demo.LocalUser;
import com.demo.KudumbasreeWorker;

public class BookedPickupsController {

    @FXML
    private VBox pickupListContainer;
    @FXML
    private ToggleButton allFilter, pendingFilter, completedFilter;
    @FXML
    private ToggleGroup filterGroup;
    @FXML
    private Button backButton;

    // Optional map view pane and details area (if you plan to add later)
    @FXML
    private WebView mapView;
    @FXML
    private VBox detailsBox;

    private List<Pickup> allPickups = new ArrayList<>();
    private Map<String, LocalUser> localUsers = new HashMap<>();

    @FXML
    private void initialize() {
        loadLocalUsers();
        loadPickups();

        // Initial display
        displayPickups("All");

        // Filter setup
        allFilter.setOnAction(e -> displayPickups("All"));
        pendingFilter.setOnAction(e -> displayPickups("Pending"));
        completedFilter.setOnAction(e -> displayPickups("Picked Up"));

        // Back button
        backButton.setOnAction(e -> goBack());
    }

    // Reads local user data
    private void loadLocalUsers() {
        Path path = Paths.get("src/main/resources/com/demo/data/localuserdata.csv");
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    LocalUser user = new LocalUser(
                            parts[0].trim(), // username
                            parts[3].trim(), // name
                            parts[5].trim(), // mobile
                            parts[7].trim(), // ward
                            parts[4].trim()  // address
                    );
                    localUsers.put(user.getUsername(), user);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Loads pickup data for this worker
    private void loadPickups() {
        Path path = Paths.get("src/main/resources/com/demo/data/pickups.csv");
        String currentWorker = Session.getCurrentUsername();

        if (currentWorker == null) {
            System.err.println("No worker logged in!");
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    String assignedWorker = parts[7].trim();
                    if (assignedWorker.equalsIgnoreCase(currentWorker)) {
                        Pickup p = new Pickup(
                                parts[0].trim(), // pickupId
                                parts[1].trim(), // username
                                parts[2].trim(), // wasteType
                                parts[3].trim(), // timeSlotId
                                parts[4].trim(), // lat
                                parts[5].trim(), // long
                                parts[6].trim(), // status
                                parts[7].trim(), // assignedWorker
                                parts[8].trim()  // createdAt
                        );
                        allPickups.add(p);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Filters and displays the list
    private void displayPickups(String filter) {
        pickupListContainer.getChildren().clear();

        List<Pickup> filtered = allPickups.stream()
                .filter(p -> filter.equals("All") || p.getStatus().equalsIgnoreCase(filter))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Label noData = new Label("No pickups found for this filter.");
            noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
            pickupListContainer.getChildren().add(noData);
            return;
        }

        for (Pickup p : filtered) {
            LocalUser user = localUsers.get(p.getUsername());
            if (user != null) {
                VBox card = createPickupCard(p, user);
                pickupListContainer.getChildren().add(card);
            }
        }
    }

    // Creates pickup card
    private VBox createPickupCard(Pickup p, LocalUser user) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; "
                + "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5,0,0,2);");

        Label idLabel = new Label("Pickup ID: " + p.getPickupId());
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label bookedBy = new Label("Booked by: " + user.getName());
        Label contact = new Label("📞 " + user.getMobile());
        Label address = new Label("📍 Address: " + user.getAddress() + ", Ward " + user.getWard());
        Label dateTime = new Label("🕒 " + p.getCreatedAtFormatted());
        Label status = new Label("Status: " + p.getStatus());
        status.setStyle(p.getStatus().equalsIgnoreCase("Picked Up")
                ? "-fx-text-fill: green; -fx-font-weight: bold;"
                : "-fx-text-fill: orange; -fx-font-weight: bold;");

        HBox buttons = new HBox(10);
        Button detailsBtn = new Button("View Details");
        Button markBtn = new Button(p.getStatus().equalsIgnoreCase("Picked Up") ? "Mark as Pending" : "Mark as Picked Up");

        detailsBtn.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 5;");
        markBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5;");

        detailsBtn.setOnAction(e -> showPickupDetails(p, user));
        markBtn.setOnAction(e -> togglePickupStatus(p, markBtn, status));

        buttons.getChildren().addAll(detailsBtn, markBtn);

        card.getChildren().addAll(idLabel, bookedBy, contact, address, dateTime, status, buttons);
        return card;
    }

    // Displays pickup details and map
    private void showPickupDetails(Pickup p, LocalUser user) {
        if (detailsBox == null || mapView == null) return;

        detailsBox.getChildren().clear();

        Label title = new Label("Pickup Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label waste = new Label("Waste Type: " + p.getWasteType());
        Label userInfo = new Label("Booked by: " + user.getName() + " (" + user.getMobile() + ")");
        Label address = new Label("Address: " + user.getAddress() + ", Ward " + user.getWard());
        Label created = new Label("Scheduled at: " + p.getCreatedAtFormatted());
        Label status = new Label("Current Status: " + p.getStatus());
        Label coords = new Label("Coordinates: " + p.getLatitude() + ", " + p.getLongitude());

        waste.setStyle("-fx-font-size: 14px;");
        userInfo.setStyle("-fx-font-size: 14px;");
        address.setStyle("-fx-font-size: 14px;");
        created.setStyle("-fx-font-size: 14px;");
        status.setStyle("-fx-font-size: 14px;");
        coords.setStyle("-fx-font-size: 14px;");

        VBox.setVgrow(mapView, Priority.ALWAYS);

        detailsBox.getChildren().addAll(title, waste, userInfo, address, created, status, coords, mapView);

        // Load map into the embedded WebView
        loadMap(p.getLatitude(), p.getLongitude(), user.getName());
    }


    private void loadMap(double lat, double lon, String name) {
        String html = """
            <html>
            <head>
              <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
              <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
            </head>
            <body>
              <div id="map" style="width:100%%; height:100%%;"></div>
              <script>
                var map = L.map('map').setView([%s, %s], 16);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {maxZoom:19}).addTo(map);
                L.marker([%s, %s]).addTo(map).bindPopup('<b>%s</b>').openPopup();
              </script>
            </body>
            </html>
        """.formatted(lat, lon, lat, lon, name);

        mapView.getEngine().loadContent(html);
    }
    private void loadMap(String latStr, String lonStr, String name) {
        try {
            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);

            String html = """
            <html>
            <head>
              <meta name='viewport' content='width=device-width, initial-scale=1.0'>
              <link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css'/>
              <script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>
            </head>
            <body style='margin:0'>
              <div id='map' style='width:100%%;height:300px;'></div>
              <script>
                var map = L.map('map').setView([%s, %s], 16);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19
                }).addTo(map);
                var marker = L.marker([%s, %s]).addTo(map)
                    .bindPopup('<b>%s</b><br>(%.5f, %.5f)').openPopup();
              </script>
            </body>
            </html>
        """.formatted(lat, lon, lat, lon, name, lat, lon);

            mapView.getEngine().loadContent(html);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Toggles pickup status and updates CSV
    private void togglePickupStatus(Pickup p, Button markBtn, Label statusLabel) {
        String newStatus = p.getStatus().equalsIgnoreCase("Picked Up") ? "Pending" : "Picked Up";
        p.setStatus(newStatus);

        updatePickupStatusInCSV(p.getPickupId(), newStatus);

        statusLabel.setText("Status: " + newStatus);
        statusLabel.setStyle(newStatus.equalsIgnoreCase("Picked Up")
                ? "-fx-text-fill: green; -fx-font-weight: bold;"
                : "-fx-text-fill: orange; -fx-font-weight: bold;");

        markBtn.setText(newStatus.equalsIgnoreCase("Picked Up") ? "Mark as Pending" : "Mark as Picked Up");
    }

    // Writes updated status to CSV
    private void updatePickupStatusInCSV(String pickupId, String newStatus) {
        Path path = Paths.get("src/main/resources/com/demo/data/pickups.csv");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();
            updated.add(lines.get(0)); // header
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",", -1);
                if (parts.length >= 8 && parts[0].trim().equals(pickupId)) {
                    parts[6] = newStatus;
                    updated.add(String.join(",", parts));
                } else {
                    updated.add(lines.get(i));
                }
            }
            Files.write(path, updated);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Navigation
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/WorkerDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Model classes
    private static class Pickup {
        private String pickupId, username, wasteType, timeSlotId, latitude, longitude, status, assignedWorker, createdAt;

        public Pickup(String pickupId, String username, String wasteType, String timeSlotId,
                      String latitude, String longitude, String status,
                      String assignedWorker, String createdAt) {
            this.pickupId = pickupId;
            this.username = username;
            this.wasteType = wasteType;
            this.timeSlotId = timeSlotId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.status = status;
            this.assignedWorker = assignedWorker;
            this.createdAt = createdAt;
        }

        public String getPickupId() { return pickupId; }
        public String getUsername() { return username; }
        public String getWasteType() { return wasteType; }
        public String getLatitude() { return latitude; }
        public String getLongitude() { return longitude; }
        public String getStatus() { return status; }
        public void setStatus(String s) { status = s; }
        public String getCreatedAtFormatted() {
            try {
                LocalDateTime dt = LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                return dt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
            } catch (Exception e) {
                return createdAt;
            }
        }
    }

    /*private static class LocalUser {
        private String username, name, mobile, ward, address;
        public LocalUser(String username, String name, String mobile, String ward, String address) {
            this.username = username;
            this.name = name;
            this.mobile = mobile;
            this.ward = ward;
            this.address = address;
        }
        public String getUsername() { return username; }
        public String getName() { return name; }
        public String getMobile() { return mobile; }
        public String getWard() { return ward; }
        public String getAddress() { return address; }
    }*/
}
