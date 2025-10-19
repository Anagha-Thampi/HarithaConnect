/*package com.demo.controllers;

import com.demo.Complaint;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class ComplaintController {

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea detailsField;

    @FXML
    private Label statusLabel;

    private Complaint complaint;

    @FXML
    private void submitComplaint() {
        String subject = subjectField.getText();
        String details = detailsField.getText();

        if (subject.isEmpty() || details.isEmpty()) {
            statusLabel.setText("Please complete all fields.");
            return;
        }

        // Connect to Complaint class
        complaint = new Complaint(details, "Pending", LocalDateTime.now(), "LocalUser");
        statusLabel.setText("Complaint submitted: " + subject + " (Status: " + complaint.getStatus() + ")");
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
}*/
