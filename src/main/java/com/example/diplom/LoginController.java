package com.example.diplom;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Проверка учетных данных
        if (authenticate(username, password)) {
            try {
                // Открытие второго окна
                if (username.equals("admin") && password.equals("admin")) {
                    HelloApplication.showAdminView();
                } else {
                    HelloApplication.showMainView();
                }
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Отображение сообщения об ошибке
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Неправильный логин или пароль");
            alert.showAndWait();
        }
    }

    private boolean authenticate(String username, String password) {
        // Установка соединения с базой данных
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            // Подготовка запроса
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            // Выполнение запроса
            ResultSet resultSet = statement.executeQuery();

            // Проверка результатов
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
