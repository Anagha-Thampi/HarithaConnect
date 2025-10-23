package com.demo.controllers;

import com.demo.DataManager;
import com.demo.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;


//Controller for viewing dump reports (for Panchayat officials & Kudumbasree workers)

public class ViewDumpReportController {
    @FXML
    private VBox detailsContainer;


    @FXML
    private VBox reportsContainer;
    @FXML private Button backButton;
    private BorderPane rootLayout; // to dynamically add right-side detail box
    @FXML
    private WebView mapView;
    @FXML
    private Label lblLocation,lblStatus,lblDetails,lblReportedBy,lblDate;


    @FXML
    public void initialize() {
        reportsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Safe to access scene now
                rootLayout = (BorderPane) reportsContainer.getScene().lookup(".border-pane");

                loadReports();

                String currentUserType = Session.getCurrentUserType();
                String currentUserId = Session.getCurrentUsername();
                String filterWard = null;
                String filterPanchayat = null;

                if ("KudumbasreeWorker".equals(currentUserType)) {
                    filterWard = getWorkerWard(currentUserId);
                } else if ("PanchayatOfficial".equals(currentUserType)) {
                    filterPanchayat = getPanchayatName(currentUserId);
                }

                // You can now safely use rootLayout and filters here
            }
        });
    }



    //Load dump reports from CSV based on logged in user

    private void loadReports() {
        reportsContainer.getChildren().clear();

        Object currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user logged in.");
            return;
        }

        boolean isKudumbasree = Session.isKudumbasreeUser();
        boolean isPanchayat = Session.isPanchayatUser();

        String userWard;
        String userPanchayat;

        String currentUserId = Session.getCurrentUsername();
        if (isKudumbasree) {
            userPanchayat = null;
            userWard = getWorkerWard(currentUserId);
        } else {
            userWard = null;
            if (isPanchayat) {
                userPanchayat = getPanchayatName(currentUserId);
            } else {
                userPanchayat = null;
            }
        }


        List<String[]> rows = DataManager.readCsv("dump_reports.csv");

        if (rows.isEmpty()) {
            Label emptyLabel = new Label("No dump reports found.");
            reportsContainer.getChildren().add(emptyLabel);
            return;
        }

        // Filter reports based on who’s logged in
        List<String[]> filtered = rows.stream().filter(r -> {
            if (r.length < 10) return false;
            if (isKudumbasree && userWard != null)
                return r[8].trim().equalsIgnoreCase(userWard.trim());
            else if (isPanchayat && userPanchayat != null)
                return r[9].trim().equalsIgnoreCase(userPanchayat.trim());
            return false;
        }).collect(Collectors.toList());

        for (String[] row : filtered) {
            VBox card = createReportCard(row, isKudumbasree);
            reportsContainer.getChildren().add(card);
        }
    }


    private VBox createReportCard(String[] row, boolean showCleanButton) {
        String reportId = row[0];
        String locationText = row[1];
        String lat = row[2];
        String lon = row[3];
        String status = row[4];
        String reportedDate = row[5];
        String reportedBy = row[6];
        String details = row[7];
        String ward = row[8];
        String panchayatName = row[9];

        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #ccc; -fx-border-radius: 8;");
        box.setPrefWidth(800);
        box.setPadding(new Insets(10));

        Label lblTitle = new Label("Report ID: " + reportId);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblLoc = new Label("Location: " + locationText);
        Label lblDate = new Label("Date: " + reportedDate);
        Label lblBy = new Label("Reported By: " + reportedBy);
        Label lblStat = new Label("Status: " + status);

        HBox buttonRow = new HBox(10);
        Button btnView = new Button("View Details");
        btnView.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        btnView.setOnAction(e -> showDetails(reportId, locationText, lat, lon, status, reportedDate, reportedBy, details));

        buttonRow.getChildren().add(btnView);

        if (showCleanButton) {
            Button btnClean = new Button("Mark as Cleaned");
            btnClean.setStyle("-fx-background-color: #1B5E20; -fx-text-fill: white;");
            btnClean.setOnAction(e -> markAsCleaned(reportId));
            buttonRow.getChildren().add(btnClean);
        }

        box.getChildren().addAll(lblTitle, lblLoc, lblDate, lblBy, lblStat, buttonRow);
        return box;
    }


    private void showDetails(String id, String locationText, String lat, String lon,
                             String status, String reportedDate, String reportedBy, String details) {

        VBox detailBox = new VBox(12);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #388E3C; -fx-border-radius: 10;");
        detailBox.setPrefWidth(400);

        Label title = new Label("Dump Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1B5E20;");

        lblLocation = new Label("📍 " + locationText);
        lblDate = new Label("Reported On: " + reportedDate);
        lblReportedBy = new Label("Reported By: " + reportedBy);
        lblStatus = new Label("Status: " + status);
        lblDetails = new Label("Details: " + details);
        lblDetails.setWrapText(true);

        mapView = new WebView();
        mapView.setPrefHeight(300);
        mapView.setPrefWidth(380);

        WebEngine engine = mapView.getEngine();
        String html = """
                <html><body style='margin:0;'>
                <div id='map' style='width:100%%;height:100%%;'></div>
                <script>
                function initMap(){
                    var map = L.map('map').setView([%s,%s], 16);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        maxZoom:19
                    }).addTo(map);
                    L.marker([%s,%s]).addTo(map).bindPopup('%s').openPopup();
                }
                </script>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"/>
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js" onload="initMap()"></script>
                </body></html>
                """.formatted(lat, lon, lat, lon, locationText);

        engine.loadContent(html);

        detailBox.getChildren().addAll(title, lblLocation, lblDate, lblReportedBy, lblStatus, lblDetails, mapView);

        // Add detail box to right side of main layout

        detailsContainer.getChildren().clear();
        detailsContainer.getChildren().add(detailBox);

    }


    private void markAsCleaned(String reportId) {
        Path path = Paths.get("src/main/resources/com/demo/data/dump_reports.csv");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(reportId + ",")) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 10) {
                        parts[4] = "Cleaned";
                        line = String.join(",", parts);
                    }
                }
                updated.add(line);
            }
            Files.write(path, updated);
            showAlert("Success", "Marked as Cleaned!");
            loadReports();
        } catch (IOException e) {
            showAlert("Error", "Could not update report status: " + e.getMessage());
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

    // Simple alert helper
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private String getWorkerWard(String workerId) {
        for (String[] row : DataManager.readCsv("kudumbasreeuserdata.csv")) {
            if (row.length > 0 && row[0].equals(workerId)) {
                // format: workerId,password,name,mobile,email,address,ward
                return row[row.length - 1]; // ward is the last column
            }
        }
        return null;
    }

    private String getPanchayatName(String panchayatId) {
        for (String[] row : DataManager.readCsv("panchayatuserdata.csv")) {
            if (row.length > 0 && row[0].equals(panchayatId)) {
                // format: panchayatId,password,fullName,designation,email,mobile,officeAddress,panchayatName
                return row[row.length - 1]; // last column
            }
        }
        return null;
    }

}
