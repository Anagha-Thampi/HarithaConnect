package com.demo.controllers;

import com.demo.MapBridge;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.demo.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReportDumpController {

    @FXML private ImageView logo;
    @FXML private Button backButton, logoutButton, reportButton;
    @FXML private TextField placeField, dateField, latField, lonField;
    @FXML private TextArea detailsField;
    @FXML private TableView<DumpReport> reportsTable;
    @FXML private TableColumn<DumpReport, String> colPlace, colDate, colStatus;
    @FXML private WebView mapView;

    private final String REPORTS_CSV = "src/main/resources/com/demo/data/dump_reports.csv";
    private final String LOCALUSER_CSV = "src/main/resources/com/demo/data/localuserdata.csv";
    private final ObservableList<DumpReport> userReports = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colPlace.setCellValueFactory(data -> data.getValue().placeProperty());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        loadUserReports();
        initializeMap();
    }

    /** 🌍 Initialize OpenStreetMap view using Leaflet */
    private void initializeMap() {
        WebEngine webEngine = mapView.getEngine();
        String mapHtml = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
          <style> html, body, #map { height: 100%; margin: 0; } </style>
        </head>
        <body>
          <div id="map"></div>
          <script>
            var map = L.map('map').setView([10.0, 76.5], 9);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              maxZoom: 19
            }).addTo(map);
            var marker;
            map.on('click', function(e) {
              if (marker) map.removeLayer(marker);
              marker = L.marker(e.latlng).addTo(map);
              var coords = e.latlng.lat.toFixed(6) + "," + e.latlng.lng.toFixed(6);
              if (window.javaConnector && window.javaConnector.updateCoords) {
                  window.javaConnector.updateCoords(coords);
              } else {
                  alert("Bridge not ready");
              }
            });
          </script>
        </body>
        </html>
        """;
        webEngine.loadContent(mapHtml);

        // Connect the MapBridge helper to this controller
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                webEngine.executeScript("window.javaConnector = {};");
                netscape.javascript.JSObject window =
                        (netscape.javascript.JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new MapBridge(this));
            }
        });
    }

    private void loadUserReports() {
        userReports.clear();
        String currentUser = Session.getCurrentUsername();
        try (BufferedReader br = new BufferedReader(new FileReader(REPORTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 10 && data[6].equalsIgnoreCase(currentUser)) {
                    userReports.add(new DumpReport(data[1], data[5], data[4]));
                }
            }
            reportsTable.setItems(userReports);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onReportButtonClick() {
        String place = placeField.getText().trim();
        String date = dateField.getText().trim();
        String details = detailsField.getText().trim();
        String lat = latField.getText().trim();
        String lon = lonField.getText().trim();
        String reportedBy = Session.getCurrentUsername();

        if (place.isEmpty() || date.isEmpty() || details.isEmpty() || lat.isEmpty() || lon.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all fields and select a location on the map.");
            return;
        }

        if (reportedBy == null) {
            showAlert(Alert.AlertType.ERROR, "No user logged in. Please log in again.");
            return;
        }

        // --- Get ward and panchayat name from localuserdata.csv ---
        String ward = "N/A", panchayatName = "N/A";
        File userFile = new File("src/main/resources/com/demo/data/localuserdata.csv");
        if (userFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 9 && data[0].equalsIgnoreCase(reportedBy)) {
                        ward = data[7];
                        panchayatName = data[8];
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String reportId = UUID.randomUUID().toString();
            String status = "Pending";

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(REPORTS_CSV, true))) {
                bw.write(String.join(",", reportId, place, lat, lon, status, date, reportedBy, details, ward, panchayatName));
                bw.newLine();
            }

            showAlert(Alert.AlertType.INFORMATION, "Report submitted successfully!");
            placeField.clear();
            dateField.clear();
            detailsField.clear();
            latField.clear();
            lonField.clear();

            loadUserReports();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error saving report. Please try again.");
        }
    }



    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
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

    public void updateCoordinates(String coords) {
        if (coords != null && coords.contains(",")) {
            String[] parts = coords.split(",");
            if (parts.length == 2) {
                latField.setText(parts[0].trim());
                lonField.setText(parts[1].trim());
            }
        }
    }

    public static class DumpReport {
        private final javafx.beans.property.SimpleStringProperty place, date, status;
        public DumpReport(String place, String date, String status) {
            this.place = new javafx.beans.property.SimpleStringProperty(place);
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }
        public String getPlace() { return place.get(); }
        public String getDate() { return date.get(); }
        public String getStatus() { return status.get(); }
        public javafx.beans.property.StringProperty placeProperty() { return place; }
        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty statusProperty() { return status; }
    }
}
