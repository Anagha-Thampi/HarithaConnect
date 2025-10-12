package com.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class HomeController {

    @FXML
    private Button aboutButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    // Navigate to About Page
    @FXML
    private void goToAbout() throws Exception {
        System.out.println("Home button clicked!");
        loadPage("/com/demo/about.fxml", "About");
    }

    // Navigate to Login Page
    @FXML
    private void goToLogin() throws Exception {
        System.out.println("log button clicked!");
        loadPage("/com/demo/LoginSelection.fxml", "Login");
    }


    // Navigate to Registration Page
    @FXML
    private void goToRegister() throws Exception {
        System.out.println("Home button clicked!");
        loadPage("/com/demo/Registration.fxml", "Register");
    }

    @FXML
    private void goHome(ActionEvent event) {
        System.out.println("Home button clicked!");
        // your logic here
    }
    private void loadPage(String fxmlFile, String title) throws Exception {
        Stage stage = (Stage) aboutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    public void hoverCard(MouseEvent mouseEvent) {
    }

    public void unhoverCard(MouseEvent mouseEvent) {
    }
}