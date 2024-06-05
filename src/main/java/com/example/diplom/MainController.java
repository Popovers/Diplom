package com.example.diplom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class MainController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/fors1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "r10270707";

    @FXML
    private ListView<String> projectListView;
    @FXML
    private Button cancelButton;
    @FXML
    private ListView<String> requestListView;

    private ObservableList<String> projectList = FXCollections.observableArrayList();
    private ObservableList<String> requestList = FXCollections.observableArrayList();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onLeaveRequestButtonClick() {
        HelloApplication.showLeaveRequestForm();
    }

    @FXML
    private void initialize() {
        loadProjects();
        loadRequests();
    }

    private void loadProjects() {
        projectList.clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name FROM projects")) {
            while (resultSet.next()) {
                projectList.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            showError("Ошибка при загрузке проектов: " + e.getMessage());
        }
        projectListView.setItems(projectList);
    }

    private void loadRequests() {
        requestList.clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT p.id AS project_id, p.name AS project_name, r.name AS role_name, " +
                    "ps.start_date, ps.end_date, r.id AS role_id " +
                    "FROM projects p " +
                    "JOIN project_specialists ps ON p.id = ps.project_id " +
                    "JOIN roles r ON ps.role_id = r.id " +
                    "WHERE ps.specialist_id IS NULL";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int projectId = resultSet.getInt("project_id");
                    String projectName = resultSet.getString("project_name");
                    String roleName = resultSet.getString("role_name");
                    Date startDate = resultSet.getDate("start_date");
                    Date endDate = resultSet.getDate("end_date");
                    int roleId = resultSet.getInt("role_id");

                    String requestInfo = String.format("Проект: %d (%s), Роль: %s (ID: %d), Начало: %s, Окончание: %s",
                            projectId, projectName, roleName, roleId, startDate, endDate);
                    requestList.add(requestInfo);
                }
            }
        } catch (SQLException e) {
            showError("Ошибка при загрузке заявок: " + e.getMessage());
        }
        requestListView.setItems(requestList);
    }

    @FXML
    private void onViewProjectButtonClick() {
        String selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            showProjectDetails(selectedProject);
        }
    }

    private void showProjectDetails(String projectName) {
        try {
            // Загрузка данных о проекте из базы данных
            String query = "SELECT * FROM projects WHERE name = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, projectName);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Создание строки с информацией о проекте
                    String projectInfo = String.format(
                            "Название: %s\nОписание: %s\nДата начала: %s\nДата окончания: %s\nБюджет: %d",
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getDate("start_date"),
                            resultSet.getDate("end_date"),
                            resultSet.getInt("budget")
                    );

                    // Отображение информации о проекте в диалоговом окне
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Информация о проекте");
                    alert.setHeaderText(null);
                    alert.setContentText(projectInfo);
                    alert.showAndWait();
                } else {
                    showError("Проект не найден.");
                }
            }
        } catch (SQLException e) {
            showError("Ошибка при загрузке данных о проекте: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelRequestButtonClick() {
        String selectedRequest = requestListView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            // Показать диалоговое окно подтверждения
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение отмены заявки");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите отменить эту заявку?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                // Пользователь нажал OK, отменяем заявку
                cancelRequest(selectedRequest);
            }
        }
    }

    private void cancelRequest(String requestInfo) {
        try {
            // Извлекаем ID проекта из строки requestInfo
            int projectId = Integer.parseInt(requestInfo.substring(requestInfo.indexOf("Проект: ") + 8, requestInfo.indexOf(" (")));

            // Удаляем заявку из базы данных
            String query = "DELETE FROM project_specialists WHERE project_id = ? AND specialist_id IS NULL";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, projectId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    // Заявка успешно удалена, обновляем список заявок
                    loadRequests();
                } else {
                    showError("Ошибка при отмене заявки: заявка не найдена.");
                }
            }
        } catch (SQLException e) {
            showError("Ошибка при отмене заявки: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Ошибка при отмене заявки: не удалось извлечь ID проекта.");
        }
    }

    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Ошибка при открытии окна авторизации: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelButtonClick() {
        // Получаем ссылку на Stage из кнопки
        Stage stage = (Stage) cancelButton.getScene().getWindow();

        // Открываем окно авторизации
        openLoginWindow();

        // Закрываем текущее окно
        stage.close();
    }

    private void showError(String message) {
        // TODO: Реализовать отображение сообщения об ошибке
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}