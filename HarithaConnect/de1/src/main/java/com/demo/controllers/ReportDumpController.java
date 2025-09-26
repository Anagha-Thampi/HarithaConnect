package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportDumpController {

    @FXML
    private TextField locationField;

    @FXML
    private TextArea detailsField;

    @FXML
    private Label statusLabel;
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
    @FXML
    private void submitReport() {
        String location = locationField.getText();
        String details = detailsField.getText();

        if (location.isEmpty() || details.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        // Later: save to DB
        statusLabel.setText("Dump reported successfully at " + location);
    }
}
