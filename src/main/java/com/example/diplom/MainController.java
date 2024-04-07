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

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class MainController {

    @FXML
    private ListView<String> projectListView;

    @FXML
    private Button cancelButton; // Добавили ссылку на кнопку "Назад"
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
        // Показываем форму заявки на ресурсы
        HelloApplication.showLeaveRequestForm();
    }
    @FXML
    private void initialize() {
        loadProjects();
        loadRequests();
    }

    private void loadProjects() {
        // Загрузка списка проектов из базы данных
        String url = "jdbc:mysql://localhost:3306/fors";
        String username = "root";
        String password = "r10270707";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name FROM projects")) {
            while (resultSet.next()) {
                String projectName = resultSet.getString("name");
                projectList.add(projectName);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке проектов: " + e.getMessage());
        }

        projectListView.setItems(projectList);
    }

    private void loadRequests() {
        // Загрузка списка заявок из базы данных
        String url = "jdbc:mysql://localhost:3306/fors";
        String username = "root";
        String password = "r10270707";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM project_specialists")) {
            while (resultSet.next()) {
                int projectId = resultSet.getInt("project_id");
                int specialistId = resultSet.getInt("specialist_id");
                int roleId = resultSet.getInt("role_id");
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");

                String requestInfo = "Проект: " + projectId + ", Специалист: " + specialistId + ", Роль: " + roleId +
                        ", Начало: " + startDate + ", Окончание: " + endDate;
                requestList.add(requestInfo);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке заявок: " + e.getMessage());
        }

        requestListView.setItems(requestList);
    }

    @FXML
    private void onViewProjectButtonClick() {
        int selectedIndex = projectListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedProject = projectList.get(selectedIndex);
            showProjectDetails(selectedProject);
        }
    }

    private void showProjectDetails(String projectName) {
        // Код для показа деталей выбранного проекта
        System.out.println("Выбран проект: " + projectName);
    }



    private void showRequestForm(String projectName) {
        // Код для показа формы заявки на ресурсы проекта
        System.out.println("Оставление заявки на проект: " + projectName);
    }

    @FXML
    private void onCancelRequestButtonClick() {
        int selectedIndex = requestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedRequest = requestList.get(selectedIndex);
            cancelRequest(selectedRequest);
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
            System.err.println("Ошибка при открытии окна авторизации: " + e.getMessage());
        }
    }


    @FXML
    private void onCancelButtonClick() {
        // Закрытие окна формы без сохранения данных
//        stage.close();
        openLoginWindow();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }
    private void cancelRequest(String requestInfo) {
        // Код для отмены выбранной заявки
        System.out.println("Отмена заявки: " + requestInfo);
    }
}