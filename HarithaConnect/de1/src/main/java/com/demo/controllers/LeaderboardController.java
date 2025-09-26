package com.demo.controllers;

import com.demo.CleanestStreetBadge;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

import java.time.LocalDate;

public class LeaderboardController {

    @FXML
    private TableView<CleanestStreetBadge> leaderboardTable;

    @FXML
    private TableColumn<CleanestStreetBadge, String> streetColumn;

    @FXML
    private TableColumn<CleanestStreetBadge, String> weekColumn;

    @FXML
    public void initialize() {
        streetColumn.setCellValueFactory(cell -> cell.getValue().streetNameProperty());
        weekColumn.setCellValueFactory(cell -> cell.getValue().weekOfRecognitionProperty().asString());

        leaderboardTable.setItems(FXCollections.observableArrayList(
                new CleanestStreetBadge("MG Road", LocalDate.now().minusDays(7)),
                new CleanestStreetBadge("Park Avenue", LocalDate.now().minusDays(14)),
                new CleanestStreetBadge("Church Street", LocalDate.now().minusDays(21))
        ));
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
