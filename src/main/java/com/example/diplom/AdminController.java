package com.example.diplom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AdminController {

    @FXML
    private TextField newUsernameField;

    @FXML
    private TextField newEmailField;

    @FXML
    private PasswordField newPasswordField1;


    @FXML
    private TextField editUsernameField;

    @FXML
    private TextField editEmailField;

    @FXML
    private PasswordField editPasswordField1;

    @FXML
    private ListView<String> rolesListView;

    @FXML
    private TextField projectNameField;

    @FXML
    private TextArea projectDescriptionArea;

    @FXML
    private TextField projectStartDateField;

    @FXML
    private TextField projectEndDateField;

    @FXML
    private TextField projectBudgetField;

    @FXML
    private TextField editProjectNameField;

    @FXML
    private TextArea editProjectDescriptionArea;

    @FXML
    private TextField editProjectStartDateField;

    @FXML
    private TextField editProjectEndDateField;

    @FXML
    private TextField editProjectBudgetField;

    @FXML
    private ListView<String> specialistsListView;

    @FXML
    private Button createUserButton;

    @FXML
    private Button logOutButton;

    @FXML
    private ComboBox<String> newUserRoleComboBox;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private Button cancelButton; // Добавили ссылку на кнопку "Назад"

    // Объявление TableView
    @FXML
    private TableView<Project> projectTableView;

    // Объявление столбцов
    @FXML
    private TableColumn<Project, Integer> projectIdColumn;
    @FXML
    private TableColumn<Project, String> projectNameColumn;
    @FXML
    private TableColumn<Project, String> projectDescriptionColumn;
    @FXML
    private TableColumn<Project, LocalDate> projectStartDateColumn;
    @FXML
    private TableColumn<Project, LocalDate> projectEndDateColumn;
    @FXML
    private TableColumn<Project, Integer> projectBudgetColumn;



    @FXML
    private ListView<ProjectRequest> projectRequestListView;

    private ObservableList<ProjectRequest> projectRequests = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> specialistComboBox;



//Метод загрузки данных из БД в комбобокс
    private void loadRolesInComboBox() {
    List<String> roles = new ArrayList<>();
    roles.add("Админ");
    roles.add("Руководитель");

    newUserRoleComboBox.getItems().setAll(roles);
}

//Кнопка создания нового пользователя
    public void onCreateUserButtonClick() {
        // Получение данных из текстовых полей
        String username = newUsernameField.getText();
        String email = newEmailField.getText();
        String password = newPasswordField1.getText();
        String role = newUserRoleComboBox.getValue();

        // Попытка подключения к базе данных
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            // Подготовка запроса на вставку нового пользователя
            String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, role);




            // Выполнение запроса на вставку
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                // Успешное добавление пользователя
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Пользователь успешно добавлен.");
                alert.showAndWait();

                // Обновление списка пользователей
                loadUserData();

                // Очистка полей после добавления пользователя
                newUsernameField.clear();
                newEmailField.clear();
                newPasswordField1.clear();
                newUserRoleComboBox.setValue(null);
            }
        } catch (SQLException e) {
            // Обработка ошибок подключения или выполнения запроса
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка при добавлении пользователя.");
            alert.showAndWait();
        }
    }

//Кпопка обновления данных пользователя
    public void onSaveUserChangesButtonClick() {
        // Получение данных из текстовых полей
        String username = editUsernameField.getText();
        String email = editEmailField.getText();
        String password = editPasswordField1.getText();

        // Получение выбранного пользователя из TableView
        TableView<User> tableView = userTableView; // замените userTableView на fx:id вашей таблицы
        ObservableList<User> selectedUsers = tableView.getSelectionModel().getSelectedItems();

        // Проверка, был ли выбран пользователь для редактирования
        if (!selectedUsers.isEmpty()) {
            // Попытка подключения к базе данных
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
                // Подготовка запроса на обновление данных пользователя
                String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);

                for (User user : selectedUsers) {
                    statement.setString(1, username);
                    statement.setString(2, email);
                    statement.setString(3, password);
                    statement.setInt(4, user.getId());

                    // Выполнение запроса на обновление
                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        // Успешное обновление данных пользователя
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Данные пользователя успешно обновлены.");
                        alert.showAndWait();
                    }
                }

                // Обновление списка пользователей
                loadUserData();

                // Очистка полей после сохранения изменений
                editUsernameField.clear();
                editEmailField.clear();
                editPasswordField1.clear();
            } catch (SQLException e) {
                // Обработка ошибок подключения или выполнения запроса
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка при обновлении данных пользователя.");
                alert.showAndWait();
            }
        } else {
            // Вывод сообщения об ошибке, если пользователь не выбран
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Выберите пользователя для редактирования.");
            alert.showAndWait();
            loadUserData();
        }
    }

//Кнопка удаления пользователя
    public void onDeleteUserButtonClick() {
        // Получение выбранного пользователя из TableView
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();

        // Проверка, был ли выбран пользователь для удаления
        if (selectedUser != null) {
            // Получение ID выбранного пользователя
            int userId = selectedUser.getId();

            // Попытка подключения к базе данных и удаления пользователя
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
                // Подготовка запроса на удаление пользователя
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userId);

                // Выполнение запроса на удаление
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Успешное удаление пользователя
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Пользователь успешно удален.");
                    alert.showAndWait();

                    // Обновление списка пользователей после удаления
                    loadUserData();
                }
            } catch (SQLException e) {
                // Обработка ошибок подключения или выполнения запроса
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка при удалении пользователя.");
                alert.showAndWait();
            }
        } else {
            // Вывод сообщения об ошибке, если пользователь не выбран
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Выберите пользователя для удаления.");
            alert.showAndWait();
        }
    }

//Метод загрузки данных пользователей
    public void loadUserData() {
        ObservableList<User> userList = FXCollections.observableArrayList();

        // Попытка подключения к базе данных и запроса данных пользователей
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            String query = "SELECT id, username, email, role FROM users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Заполнение списка данными пользователей
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                User user = new User(id, username, email, role);
                userList.add(user);
            }

            // Установка данных пользователей в TableView
            userTableView.setItems(userList);
        } catch (SQLException e) {
            // Обработка ошибок подключения или выполнения запроса
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка при загрузке данных пользователей.");
            alert.showAndWait();
        }
    }

//Метод загрузки данных проектов
    public void loadProjectData() {
        // Очистка таблицы проектов перед загрузкой данных
        projectTableView.getItems().clear();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM projects");
             ResultSet resultSet = statement.executeQuery()) {

            // Проверка успешного подключения
            if (!connection.isValid(5)) {
                showErrorDialog("Не удалось установить подключение к базе данных.");
                return;
            }

            // Обход результатов запроса и добавление проектов в таблицу
            while (resultSet.next()) {
                int projectId = resultSet.getInt("id");
                String projectName = resultSet.getString("name");
                String projectDescription = resultSet.getString("description");
                LocalDate startDate = resultSet.getDate("start_date") == null ? null : resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date") == null ? null : resultSet.getDate("end_date").toLocalDate();
                int budget = resultSet.getInt("budget");

                // Создание объекта проекта
                Project project = new Project(projectId, projectName, projectDescription, startDate, endDate, budget);

                // Добавление объекта в таблицу
                projectTableView.getItems().add(project);
            }
        } catch (SQLException e) {
            // Обработка ошибок подключения или выполнения запроса
            showErrorDialog("Ошибка при загрузке данных о проектах: " + e.getMessage());
        }

        // Настройка отображения данных в TableView
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        projectStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        projectEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        projectBudgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));
    }

//Метод отображения ошибок
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//Метод загрузки данных специалистов
    private void loadSpecialistsData() {
        // Получение данных о специалистах из базы данных
        // Здесь должен быть код для загрузки данных из таблицы специалистов
        // ...

        // Обновление списка специалистов в пользовательском интерфейсе
        List<String> specialistList = new ArrayList<>();
        // Здесь должен быть код для заполнения списка specialistList данными из базы данных
        // ...

        specialistsListView.getItems().setAll(specialistList);
    }

//Создание нового проекта
    @FXML
    private void onCreateProjectButtonClick() {
        // Получение данных о новом проекте из текстовых полей
        String projectName = projectNameField.getText();
        String projectDescription = projectDescriptionArea.getText();
        String startDateText = projectStartDateField.getText();
        String endDateText = projectEndDateField.getText();
        String budgetText = projectBudgetField.getText();

        // Проверка корректности введенных данных
        if (projectName.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty() || budgetText.isEmpty()) {
            showErrorDialog("Заполните все поля.");
            return;
        }

        try {
            // Преобразование текстовых данных в нужные типы
            LocalDate startDate = LocalDate.parse(startDateText);
            LocalDate endDate = LocalDate.parse(endDateText);
            int budget = Integer.parseInt(budgetText);

            // Попытка подключения к базе данных
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
                // Подготовка запроса на добавление нового проекта
                String query = "INSERT INTO projects (name, description, start_date, end_date, budget) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, projectName);
                statement.setString(2, projectDescription);
                statement.setDate(3, Date.valueOf(startDate));
                statement.setDate(4, Date.valueOf(endDate));
                statement.setInt(5, budget);

                // Выполнение запроса на добавление
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    // Успешное добавление проекта
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Проект успешно создан.");
                    alert.showAndWait();

                    // Очистка полей после добавления проекта
                    projectNameField.clear();
                    projectDescriptionArea.clear();
                    projectStartDateField.clear();
                    projectEndDateField.clear();
                    projectBudgetField.clear();

                    // Обновление отображения списка проектов
                    loadProjectData();
                }
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            // Обработка ошибок ввода данных
            showErrorDialog("Некорректный формат даты или бюджета.");
        } catch (SQLException e) {
            // Обработка ошибок подключения или выполнения запроса
            showErrorDialog("Ошибка при создании проекта: " + e.getMessage());
        }
    }

//Метод отображения ошибок
    private void showWarningDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//Метод обновления записей проекта
    @FXML
    private void updateProject() {
        Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            // Получение новых данных из соответствующих текстовых полей
            String newName = editProjectNameField.getText();
            String newDescription = editProjectDescriptionArea.getText();
            LocalDate newStartDate = LocalDate.parse(editProjectStartDateField.getText());
            LocalDate newEndDate = LocalDate.parse(editProjectEndDateField.getText());
            int newBudget = Integer.parseInt(editProjectBudgetField.getText());

            // Обновление данных проекта
            selectedProject.setName(newName);
            selectedProject.setDescription(newDescription);
            selectedProject.setStartDate(newStartDate);
            selectedProject.setEndDate(newEndDate);
            selectedProject.setBudget(newBudget);

            // Обновление данных в базе данных
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
                String query = "UPDATE projects SET name = ?, description = ?, start_date = ?, end_date = ?, budget = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, newName);
                statement.setString(2, newDescription);
                statement.setDate(3, Date.valueOf(newStartDate));
                statement.setDate(4, Date.valueOf(newEndDate));
                statement.setInt(5, newBudget);
                statement.setInt(6, selectedProject.getId());
                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    // Успешное обновление данных проекта
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Данные проекта успешно обновлены.");
                    alert.showAndWait();
                    loadProjectData(); // Обновление таблицы проектов
                }
            } catch (SQLException e) {
                showErrorDialog("Ошибка при обновлении данных проекта: " + e.getMessage());
            }
        } else {
            showWarningDialog("Пожалуйста, выберите проект для обновления.");
        }
        loadProjectData();
    }

//Метод удаления проекта
    @FXML
    private void onDeleteProjectButtonClick() {
        Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            deleteProject(selectedProject);
        } else {
            showWarningDialog("Пожалуйста, выберите проект для удаления.");
        }
    }

//Метод удаления проекта
    private void deleteProject(Project project) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            String query = "DELETE FROM projects WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, project.getId());
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Проект успешно удален.");
                alert.showAndWait();
                loadProjectData(); // Обновление таблицы проектов
            }
        } catch (SQLException e) {
            showErrorDialog("Ошибка при удалении проекта: " + e.getMessage());
        }
    }

    //Метод открытия окна авторизации
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

    // Метод загрузки данных проектов
    private void loadProjectRequests() {
        // Создаем ObservableList<ProjectRequest> для хранения информации о заявках
        ObservableList<ProjectRequest> projectRequests = FXCollections.observableArrayList();

        // Получение данных о заявках из базы данных и добавление их в список
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            String query = "SELECT projects.id AS project_id, projects.name AS project_name, roles.name AS role_name, " +
                    "project_specialists.start_date, project_specialists.end_date, roles.id AS role_id " +
                    "FROM projects " +
                    "JOIN project_specialists ON projects.id = project_specialists.project_id " +
                    "JOIN roles ON project_specialists.role_id = roles.id " +
                    "WHERE project_specialists.specialist_id IS NULL";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int projectId = resultSet.getInt("project_id");
                String projectName = resultSet.getString("project_name");
                String roleName = resultSet.getString("role_name");
                String startDate = resultSet.getString("start_date");
                String endDate = resultSet.getString("end_date");
                int roleId = resultSet.getInt("role_id");

                ProjectRequest projectRequest = new ProjectRequest(projectId, projectName, roleId, roleName, startDate, endDate);
                projectRequests.add(projectRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Присваиваем заполненный список переменной класса для дальнейшего использования
        this.projectRequests = projectRequests;
    }

    private ObservableList<String> loadSpecialistsByRole(int roleId) {
        ObservableList<String> specialists = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            String query = "SELECT CONCAT_WS(' ', s.first_name, s.last_name, s.middle_name) AS full_name " +
                    "FROM specialists s " +
                    "JOIN specialist_roles sr ON s.id = sr.specialist_id " +
                    "WHERE sr.role_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roleId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                specialists.add(fullName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при загрузке специалистов", ButtonType.OK);
            alert.showAndWait();
        }
        return specialists; // Возвращаем список специалистов
    }
    @FXML
    private void assignSpecialists(ActionEvent event) {
        ProjectRequest selectedProjectRequest = projectRequestListView.getSelectionModel().getSelectedItem();
        if (selectedProjectRequest == null) {
            System.out.println("Заявка не выбрана");
            return; // Возвращаемся, не выполняя дальнейшие действия
        }

        int roleId = selectedProjectRequest.getRoleId();
        System.out.println("Выбранная заявка: " + selectedProjectRequest.getProjectName() + ", roleId = " + roleId);

        // Получение выбранного специалиста из комбобокса
        String selectedSpecialist = specialistComboBox.getSelectionModel().getSelectedItem();
        if (selectedSpecialist == null) {
            System.out.println("Специалист не выбран");
            return; // Возвращаемся, не выполняя дальнейшие действия
        }

        // Добавление специалиста в базу данных для выбранного проекта
        addSpecialistToProject(selectedSpecialist, selectedProjectRequest.getId());
    }

    private void addSpecialistToProject(String specialistName, int projectId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            // Получаем id специалиста по его имени
            String specialistIdQuery = "SELECT id FROM specialists WHERE CONCAT_WS(' ', first_name, last_name, middle_name) = ?";
            PreparedStatement specialistIdStatement = connection.prepareStatement(specialistIdQuery);
            specialistIdStatement.setString(1, specialistName);
            ResultSet specialistIdResult = specialistIdStatement.executeQuery();
            if (specialistIdResult.next()) {
                int specialistId = specialistIdResult.getInt("id");

                // Обновляем запись в таблице project_specialists, добавляя specialistId
                String updateQuery = "UPDATE project_specialists SET specialist_id = ? WHERE project_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, specialistId);
                updateStatement.setInt(2, projectId);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Специалист успешно добавлен к проекту");
                } else {
                    System.out.println("Не удалось добавить специалиста к проекту");
                }
            } else {
                System.out.println("Специалист с таким именем не найден");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedSpecialistId(String selectedSpecialist) {
        int specialistId = -1;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fors", "root", "r10270707")) {
            String query = "SELECT id FROM specialists WHERE CONCAT_WS(' ', first_name, last_name, middle_name) = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, selectedSpecialist);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                specialistId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return specialistId;
    }
    // Метод закрытия окна авторизации
    @FXML
    private void onCancelButtonClick() {
        // Закрытие окна формы без сохранения данных
//        stage.close();
        openLoginWindow();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }

    //Инициализация класса
    public void initialize() {
        // Привязываем свойство disable кнопки createUserButton к состоянию пустоты полей
        createUserButton.disableProperty().bind(
                newUsernameField.textProperty().isEmpty()
                        .or(newEmailField.textProperty().isEmpty())
                        .or(newPasswordField1.textProperty().isEmpty())
        );
        // Устанавливаем соответствие между полями класса User и столбцами TableView
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        projectRequestListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int roleId = newValue.getRoleId(); // Получение ID роли выбранной заявки
                System.out.println("Выбранная заявка: " + newValue.getProjectName() + ", roleId = " + roleId);

                // Загрузка специалистов для выбранной роли
                ObservableList<String> specialists = loadSpecialistsByRole(roleId);
                specialistComboBox.setItems(specialists);
            }
        });

        loadRolesInComboBox();
        loadUserData();
        loadProjectData();
        loadProjectRequests();
        projectRequestListView.setItems(projectRequests);
    }

}

