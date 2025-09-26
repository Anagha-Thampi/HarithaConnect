package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class loginSelectionPage {

    // ===== Utility to change scene =====
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

    // ===== Navigation Handlers =====
    @FXML
    private void goHome(ActionEvent event) {
        switchScene(event, "homepage.fxml");
    }

    @FXML
    private void goAbout(ActionEvent event) {
        switchScene(event, "about.fxml");
    }

    @FXML
    private void goLogin(ActionEvent event) {
        switchScene(event, "LoginSelection.fxml");
    }

    @FXML
    private void goContact(ActionEvent event) {
        switchScene(event, "contact.fxml");
    }

    @FXML
    private void goSignup(ActionEvent event) {
        switchScene(event, "signup.fxml");
    }

    // ===== Login Selection Buttons =====
    @FXML
    private void loginAsLocalUser(ActionEvent event) {
        switchScene(event, "LocalLogin.fxml");
    }

    @FXML
    private void loginAsPanchayatOfficial(ActionEvent event) {
        switchScene(event, "PanchayatLogin.fxml");
    }

    @FXML
    private void loginAsKudumbashreeWorker(ActionEvent event) {
        switchScene(event, "WorkerLogin.fxml");
    }
}