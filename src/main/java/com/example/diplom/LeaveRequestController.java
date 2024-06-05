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
import java.time.LocalDate;
import java.util.logging.*;

public class LeaveRequestController {

    private static final Logger logger = Logger.getLogger(LeaveRequestController.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("leave-request-controller.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            System.err.println("Ошибка при настройке логгера: " + e.getMessage());
        }
    }

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> projectComboBox;

    @FXML
    private Button cancelButton;

    // ... (остальной код FXML элементов без изменений)

    // Default constructor for FXMLLoader
    public LeaveRequestController() {
    }

    // Constructor with Stage argument for your application logic (не используется в этом примере)
    // public LeaveRequestController(Stage stage) {
    //     this.stage = stage;
    // }

    @FXML
    private void initialize() {
        loadRoles();
        loadProjects();
    }

    private void loadRoles() {
        roleComboBox.getItems().clear();

        String url = "jdbc:mysql://localhost:3306/fors1";
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
            logger.log(Level.SEVERE, "Ошибка при загрузке ролей", e);
            showErrorDialog("Ошибка при загрузке ролей: " + e.getMessage());
        }
    }

    private void loadProjects() {
        String url = "jdbc:mysql://localhost:3306/fors1";
        String username = "root";
        String password = "r10270707";

        ObservableList<String> projectNames = FXCollections.observableArrayList();

        String query = "SELECT DISTINCT projects.name " +
                "FROM projects " +
                "LEFT JOIN project_specialists ON projects.id = project_specialists.project_id";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String projectName = resultSet.getString("name");
                projectNames.add(projectName);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке проектов", e);
            showErrorDialog("Ошибка при загрузке проектов: " + e.getMessage());
        }

        projectComboBox.setItems(projectNames);
    }

    @FXML
    private void onSaveRequestButtonClick() {
        String selectedProject = projectComboBox.getValue();
        String selectedRole = roleComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (selectedProject == null || selectedRole == null || startDate == null || endDate == null) {
            logger.warning("Попытка сохранить заявку с пустыми полями.");
            showErrorDialog("Заполните все поля!");
            return;
        }

        if (endDate.isBefore(startDate)) {
            logger.warning("Попытка сохранить заявку с датой окончания раньше даты начала.");
            showErrorDialog("Дата окончания заявки не может быть раньше даты начала!");
            return;
        }

        // Получение ID проекта и роли из базы данных
        int projectId = 0;
        int roleId = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors1", "root", "r10270707")) {
            String projectQuery = "SELECT id FROM projects WHERE name = ?";
            try (PreparedStatement projectStatement = connection.prepareStatement(projectQuery)) {
                projectStatement.setString(1, selectedProject);
                try (ResultSet projectResultSet = projectStatement.executeQuery()) {
                    if (projectResultSet.next()) {
                        projectId = projectResultSet.getInt("id");
                    } else {
                        logger.warning("Проект не найден: " + selectedProject);
                        showErrorDialog("Проект не найден: " + selectedProject);
                        return;
                    }
                }
            }

            String roleQuery = "SELECT id FROM roles WHERE name = ?";
            try (PreparedStatement roleStatement = connection.prepareStatement(roleQuery)) {
                roleStatement.setString(1, selectedRole);
                try (ResultSet roleResultSet = roleStatement.executeQuery()) {
                    if (roleResultSet.next()) {
                        roleId = roleResultSet.getInt("id");
                    } else {
                        logger.warning("Роль не найдена: " + selectedRole);
                        showErrorDialog("Роль не найдена: " + selectedRole);
                        return;
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении ID проекта или роли из базы данных: " + e.getMessage(), e);
            showErrorDialog("Ошибка при сохранении данных заявки: " + e.getMessage());
            return;
        }

        // Сохранение заявки в базу данных
        String insertQuery = "INSERT INTO project_specialists (project_id, role_id, start_date, end_date) VALUES (?, ?, ?, ?)";
        String url = "jdbc:mysql://localhost:3306/fors1";
        String username = "root";
        String password = "r10270707";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, projectId);
            statement.setInt(2, roleId);
            statement.setDate(3, Date.valueOf(startDate));
            statement.setDate(4, Date.valueOf(endDate));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Заявка успешно сохранена: Проект: " + selectedProject + ", Роль: " + selectedRole + ", Даты: " + startDate + " - " + endDate);
                showInfoDialog("Данные заявки успешно сохранены в базе данных.");
                // Очистка полей формы
                projectComboBox.getSelectionModel().clearSelection();
                roleComboBox.getSelectionModel().clearSelection();
                startDatePicker.getEditor().clear();
                endDatePicker.getEditor().clear();
            } else {
                logger.warning("Ошибка при сохранении данных заявки.");
                showErrorDialog("Ошибка при сохранении данных заявки.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при сохранении данных заявки: " + e.getMessage(), e);
            showErrorDialog("Ошибка при сохранении данных заявки: " + e.getMessage());
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
            logger.log(Level.SEVERE, "Ошибка при открытии главного окна: " + e.getMessage(), e);
            showErrorDialog("Ошибка при открытии главного окна: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelButtonClick() {
        // Закрытие окна формы без сохранения данных
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Метод для отображения диалогового окна об ошибке
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для отображения информационного диалогового окна
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}