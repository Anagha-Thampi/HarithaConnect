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
import java.util.*;
import java.util.stream.Collectors;

public class EwasteCentresController {

    @FXML
    private ComboBox<String> wardComboBox;
    @FXML
    private VBox centresList;
    @FXML
    private Label noDataLabel;
    @FXML
    private WebView mapView;
    @FXML
    private Label centreNameLabel, wardLabel, distanceLabel;
    @FXML
    private Button backButton;

    private List<EwasteCentre> allCentres = new ArrayList<>();
    @FXML
    private void initialize() {
        loadCsvData();

        // Extract unique ward names dynamically
        Set<String> wardNames = allCentres.stream()
                .map(EwasteCentre::getWardName)
                .collect(Collectors.toCollection(TreeSet::new));

        wardComboBox.getItems().addAll(wardNames);
        wardComboBox.setOnAction(e -> {
            String selectedWard = wardComboBox.getValue();
            if (selectedWard != null) displayCentresForWard(selectedWard);
        });

        backButton.setOnAction(e -> goBack());
    }


    // Reads data from CSV
    // Reads data from CSV
    private void loadCsvData() {
        try (InputStream is = getClass().getResourceAsStream("/com/demo/data/ewaste.csv")) {
            if (is == null) {
                System.err.println("CSV file not found in resources!");
                return;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                br.readLine(); // skip header
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 9) {
                        EwasteCentre c = new EwasteCentre(
                                parts[0].trim(), // wardNo
                                parts[1].trim(), // name
                                parts[2].trim(), // wardName
                                parts[3].trim(), // place
                                parts[4].trim(), // phone
                                parts[5].trim(), // activeTime
                                parts[6].trim(), // status
                                parts[7].trim(), // lat
                                parts[8].trim()  // lon
                        );
                        allCentres.add(c);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    // Filters and displays centres
    private void displayCentresForWard(String wardName) {
        centresList.getChildren().clear();
        List<EwasteCentre> centres = allCentres.stream()
                .filter(c -> c.getWardName().equalsIgnoreCase(wardName))
                .collect(Collectors.toList());

        if (centres.isEmpty()) {
            noDataLabel.setVisible(true);
            return;
        }
        noDataLabel.setVisible(false);

        ScrollPane scrollPane = new ScrollPane();
        VBox contentBox = new VBox(15);
        contentBox.setStyle("-fx-padding: 5;");
        scrollPane.setContent(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        for (EwasteCentre c : centres) {
            VBox card = createCentreCard(c);
            contentBox.getChildren().add(card);
        }

        centresList.getChildren().add(scrollPane);
        loadMap(centres);
    }

    // Creates individual card for each centre
    private VBox createCentreCard(EwasteCentre c) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; "
                + "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5,0,0,2);");

        HBox header = new HBox(10);
        Label nameLabel = new Label(c.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1f4c44;");
        Label status = new Label(c.getStatus());
        if (c.getStatus().equalsIgnoreCase("Active")) {
            status.setStyle("-fx-background-color: #e0f8e9; -fx-text-fill: #28a745; -fx-padding: 3 8; -fx-background-radius: 5;");
        } else {
            status.setStyle("-fx-background-color: #fdecea; -fx-text-fill: #dc3545; -fx-padding: 3 8; -fx-background-radius: 5;");
        }
        header.getChildren().addAll(nameLabel, status);

        Label place = new Label(c.getPlace());
        place.setStyle("-fx-text-fill: #555;");
        Label phone = new Label("📞 " + c.getPhone());
        phone.setStyle("-fx-text-fill: #555;");
        Label time = new Label("🕒 " + c.getActiveTime());
        time.setStyle("-fx-text-fill: #555;");

        Button mapButton = new Button("View on Map");
        mapButton.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
        mapButton.setOnAction(e -> {
            showCentreOnMap(c);
        });

        card.getChildren().addAll(header, place, phone, time, mapButton);
        return card;
    }

    // Loads map with all markers for the ward
    private void loadMap(List<EwasteCentre> centres) {
        StringBuilder jsMarkers = new StringBuilder();
        for (EwasteCentre c : centres) {
            jsMarkers.append(String.format(
                    "L.marker([%s,%s]).addTo(map).bindPopup('<b>%s</b><br>%s');",
                    c.getLatitude(), c.getLongitude(),
                    c.getName(), c.getPlace()
            ));
        }

        String html = """
            <html>
            <head>
              <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
              <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
            </head>
            <body>
              <div id="map" style="width:100%%; height:100%%;"></div>
              <script>
                var map = L.map('map').setView([10.15, 76.45], 13);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {maxZoom:19}).addTo(map);
                %s
              </script>
            </body>
            </html>
        """.formatted(jsMarkers.toString());

        mapView.getEngine().loadContent(html);
    }

    // Shows selected centre on map and fills info labels
    private void showCentreOnMap(EwasteCentre c) {
        centreNameLabel.setText("Centre Name: " + c.getName());
        wardLabel.setText("Ward: " + c.getWardName());
        distanceLabel.setText("Place: " + c.getPlace());

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
                L.marker([%s, %s]).addTo(map)
                  .bindPopup('<b>%s</b><br>%s').openPopup();
              </script>
            </body>
            </html>
        """.formatted(c.getLatitude(), c.getLongitude(),
                c.getLatitude(), c.getLongitude(),
                c.getName(), c.getPlace());

        mapView.getEngine().loadContent(html);
    }

    // Back navigation
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/LocalDash.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Model class for CSV data
    public static class EwasteCentre {
        private final String wardNo, name, wardName, place, phone, activeTime, status, latitude, longitude;
        public EwasteCentre(String wardNo, String name, String wardName, String place,
                            String phone, String activeTime, String status,
                            String latitude, String longitude) {
            this.wardNo = wardNo;
            this.name = name;
            this.wardName = wardName;
            this.place = place;
            this.phone = phone;
            this.activeTime = activeTime;
            this.status = status;
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public String getWardNo() { return wardNo; }
        public String getName() { return name; }
        public String getWardName() { return wardName; }
        public String getPlace() { return place; }
        public String getPhone() { return phone; }
        public String getActiveTime() { return activeTime; }
        public String getStatus() { return status; }
        public String getLatitude() { return latitude; }
        public String getLongitude() { return longitude; }
    }
}