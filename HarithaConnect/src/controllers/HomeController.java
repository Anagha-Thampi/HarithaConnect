package HarithaConnect.src.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
        loadPage("/fxml/about.fxml", "About");
    }

    // Navigate to Login Page
    @FXML
    private void goToLogin() throws Exception {
        loadPage("/fxml/LocalLogin.fxml", "Login");
    }

    // Navigate to Registration Page
    @FXML
    private void goToRegister() throws Exception {
        loadPage("/fxml/register.fxml", "Register");
    }

    private void loadPage(String fxmlFile, String title) throws Exception {
        Stage stage = (Stage) aboutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}