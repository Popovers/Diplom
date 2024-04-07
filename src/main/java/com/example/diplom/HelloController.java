package com.example.diplom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void onLoginButtonClick() {
        // TODO: Implement login functionality
        // Get the username and password from the text fields
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate the username and password
        // If the credentials are valid, log the user in
        // Otherwise, display an error message
    }
}
