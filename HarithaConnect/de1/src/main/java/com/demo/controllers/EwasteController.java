package com.demo.controllers;

import com.demo.EWasteCenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class EwasteController {

    @FXML
    private ListView<String> centresList;

    @FXML
    public void initialize() {
        // Connect to EwasteCenter class
        EWasteCenter c1 = new EWasteCenter("Green Earth Recycling", "MG Road", true, true, Arrays.asList("Plastic", "Metal"));
        EWasteCenter c2 = new EWasteCenter("Eco Drop", "Park Avenue", true, false, Arrays.asList("Electronics", "Glass"));

        centresList.getItems().addAll(
                c1.getName() + " - " + c1.getLocation() + " (Govt Approved: " + c1.isGovtApproved() + ")",
                c2.getName() + " - " + c2.getLocation() + " (Govt Approved: " + c2.isGovtApproved() + ")"
        );
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
