package com.example.diplom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        // Привязываем состояние кнопки "войти" к содержимому полей логина и пароля
        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty())
        );
    }

    @FXML
    private void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Проверка на пустые поля
        if (username.isEmpty() || password.isEmpty()) {
            // Отображение сообщения об ошибке
            Alert emptyFieldsAlert = new Alert(Alert.AlertType.ERROR);
            emptyFieldsAlert.setTitle("Error");
            emptyFieldsAlert.setHeaderText(null);
            emptyFieldsAlert.setContentText("Пожалуйста, введите логин или пароль.");
            emptyFieldsAlert.showAndWait();
            return;
        }

        // Проверка учетных данных
        User user = authenticate(username, password);
        if (user != null) {
            try {
                // Открытие второго окна в зависимости от роли пользователя
                if (user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("админ")) {
                    HelloApplication.showAdminView();
                } else {
                    HelloApplication.showMainView();
                }

                // Закрытие текущего окна
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Отображение сообщения об ошибке
            Alert authErrorAlert = new Alert(Alert.AlertType.ERROR);
            authErrorAlert.setTitle("Error");
            authErrorAlert.setHeaderText(null);
            authErrorAlert.setContentText("Неправильный логин или пароль.");
            authErrorAlert.showAndWait();
        }
    }

    private User authenticate(String username, String password) {
        // Установка соединения с базой данных
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            // Подготовка запроса
            String query = "SELECT id, username, email, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            // Выполнение запроса
            ResultSet resultSet = statement.executeQuery();

            // Проверка результатов
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                return new User(id, username, email, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
