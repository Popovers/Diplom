package com.example.diplom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LeaveRequestController {


    @FXML
    private ComboBox<String> roleComboBox;



    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> projectComboBox;

    @FXML
    private Button cancelButton; // Добавили ссылку на кнопку "Назад"



//    private Stage stage;

    // Default constructor for FXMLLoader
    public LeaveRequestController() {
    }

    // Constructor with Stage argument for your application logic
//    public LeaveRequestController(Stage stage) {
//        this.stage = stage;
//    }

    @FXML
    private void initialize() {

        loadRoles();
        loadProjects();
    }

    private void loadRoles() {
        roleComboBox.getItems().clear(); // Очищаем список ролей перед загрузкой новых данных

        // Выполняем запрос к базе данных для загрузки всех ролей
        String url = "jdbc:mysql://localhost:3306/fors";
        String username = "root";
        String password = "r10270707";
        String query = "SELECT name FROM roles";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String roleName = resultSet.getString("name");
                roleComboBox.getItems().add(roleName);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке ролей: " + e.getMessage());
        }
        roleComboBox.hide();
    }


    private void loadProjects() {
        // Загрузка списка проектов из базы данных
        String url = "jdbc:mysql://localhost:3306/fors";
        String username = "root";
        String password = "r10270707";

        ObservableList<String> projectNames = FXCollections.observableArrayList(); // Список для хранения названий проектов

        // SQL запрос, который соединяет таблицы projects и project_specialists по project_id
        String query = "SELECT projects.name " +
                "FROM projects " +
                "LEFT JOIN project_specialists ON projects.id = project_specialists.project_id";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String projectName = resultSet.getString("name");
                projectNames.add(projectName); // Добавление названия проекта в список
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке проектов: " + e.getMessage());
        }

        // Установка списка названий проектов в комбобокс
        projectComboBox.setItems(projectNames);
    }

    @FXML
    private void onSaveRequestButtonClick() {
        // Получение данных из полей формы
        String selectedProject = projectComboBox.getValue(); // Получаем выбранный проект
        String selectedRole = roleComboBox.getValue();
        String startDate = startDatePicker.getValue().toString();
        String endDate = endDatePicker.getValue().toString();

        // Далее следует сохранение данных заявки в базу данных

        // Запрос SQL для вставки данных заявки в таблицу project_specialists
        String insertQuery = "INSERT INTO project_specialists (project_id, role_id, start_date, end_date) " +
                "SELECT projects.id, roles.id, ?, ? " +
                "FROM projects, roles " +
                "WHERE projects.name = ? AND roles.name = ?";


        String url = "jdbc:mysql://localhost:3306/fors";
        String username = "root";
        String password = "r10270707";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);
            statement.setString(3, selectedProject);
            statement.setString(4, selectedRole);

            int rowsAffected = statement.executeUpdate(); // Выполнение запроса на вставку данных

            if (rowsAffected > 0) {
                System.out.println("Данные заявки успешно сохранены в базе данных.");
            } else {
                System.out.println("Ошибка при сохранении данных заявки.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении данных заявки: " + e.getMessage());
        }

    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при открытии окна авторизации: " + e.getMessage());
        }
    }


    @FXML
    private void onCancelButtonClick() {
        // Закрытие окна формы без сохранения данных
//        stage.close();
        openMainWindow();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }
}
