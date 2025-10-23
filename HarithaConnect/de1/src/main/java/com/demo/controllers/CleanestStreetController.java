package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import com.demo.Session;
import javafx.stage.Stage;

public class CleanestStreetController {

    @FXML private TableView<StreetData> streetsTable;
    @FXML private TableColumn<StreetData, String> rankCol;
    @FXML private TableColumn<StreetData, String> streetNameCol;
    @FXML private TableColumn<StreetData, String> cleanedCol;
    @FXML private Label cleanestPercentLabel;

    private static final String DUMP_REPORTS_FILE = "src/main/resources/com/demo/data/dump_reports.csv";

    @FXML
    public void initialize() {
        rankCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getRank())));
        streetNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStreetName()));
        cleanedCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCleanedPercentage() + "%"));

        loadCleanestStreets();
    }

    private void loadCleanestStreets() {
        try {
            String username = Session.getCurrentUsername();
            if (username == null) {
                showAlert("Error", "No user logged in.");
                return;
            }

            String userFile;
            String matchField = null;
            String matchValue = null;

            if (Session.getCurrentUser() instanceof com.demo.LocalUser) {
                userFile = "src/main/resources/com/demo/data/localuserdata.csv";
                matchField = "ward";
            } else if (Session.getCurrentUser() instanceof com.demo.PanchayatOfficial) {
                userFile = "src/main/resources/com/demo/data/panchayatuserdata.csv";
                matchField = "panchayatName";
            } else {
                showAlert("Access Denied", "Kudumbasree workers cannot view cleanest streets.");
                return;
            }

            // Get user's ward/panchayatName
            matchValue = getUserArea(userFile, username, matchField);
            if (matchValue == null) {
                showAlert("Error", "Could not find user data for filtering.");
                return;
            }

            List<String> lines = Files.readAllLines(Paths.get(DUMP_REPORTS_FILE));
            if (lines.size() <= 1) {
                showAlert("Info", "No dump reports available.");
                return;
            }

            Map<String, List<String[]>> grouped = new HashMap<>();
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 10) continue; // ensure correct length

                String street = parts[1].trim();
                String status = parts[4].trim();
                String ward = parts[8].trim();
                String panchayat = parts[9].trim();

                boolean match = (matchField.equals("ward") && ward.equalsIgnoreCase(matchValue))
                        || (matchField.equals("panchayatName") && panchayat.equalsIgnoreCase(matchValue));

                if (match) {
                    grouped.computeIfAbsent(street, k -> new ArrayList<>()).add(parts);
                }
            }

            if (grouped.isEmpty()) {
                showAlert("Info", "No dump reports in your area.");
                return;
            }

            // Calculate cleaned %
            List<StreetData> dataList = grouped.entrySet().stream()
                    .map(e -> {
                        long total = e.getValue().size();
                        long cleaned = e.getValue().stream().filter(p -> p[4].equalsIgnoreCase("Cleaned")).count();
                        int percent = (int) ((cleaned * 100.0) / total);
                        return new StreetData(e.getKey(), percent);
                    })
                    .sorted(Comparator.comparingInt(StreetData::getCleanedPercentage).reversed())
                    .collect(Collectors.toList());

            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).setRank(i + 1);
            }

            streetsTable.setItems(FXCollections.observableArrayList(dataList));

            StreetData top = dataList.get(0);
            cleanestPercentLabel.setText(top.getStreetName() + " (" + top.getCleanedPercentage() + "% cleaned)");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load data.");
        }
    }

    private String getUserArea(String userFile, String username, String field) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(userFile));
            if (lines.size() <= 1) return null;

            String[] headers = lines.get(0).split(",");
            int usernameIndex = 0;
            int fieldIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(field)) fieldIndex = i;
                if (headers[i].trim().equalsIgnoreCase("username") || headers[i].trim().equalsIgnoreCase("panchayatId")) usernameIndex = i;
            }

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts[usernameIndex].equalsIgnoreCase(username)) {
                    return parts[fieldIndex];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StreetData {
        private String streetName;
        private int cleanedPercentage;
        private int rank;

        public StreetData(String streetName, int cleanedPercentage) {
            this.streetName = streetName;
            this.cleanedPercentage = cleanedPercentage;
        }

        public String getStreetName() { return streetName; }
        public int getCleanedPercentage() { return cleanedPercentage; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }
    @FXML
    private void handleBackAction(ActionEvent event) {
        if (Session.getCurrentUser() instanceof com.demo.LocalUser) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/LocalDash.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Local Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to go back to dashboard.");
            }
        } else if (Session.getCurrentUser() instanceof com.demo.PanchayatOfficial) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/PanchayatDash.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Local Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to go back to dashboard.");
            }
        }
    }
}

