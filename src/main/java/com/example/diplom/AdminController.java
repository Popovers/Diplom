package com.example.diplom;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeParseException;
import java.io.FileWriter;
import java.io.PrintWriter;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.temporal.ChronoUnit;
public class AdminController {

    private static final Logger logger = Logger.getLogger(AdminController.class.getName());


    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField searchUserField;
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
    private TextField projectNameField;
    @FXML
    private TextArea projectDescriptionArea;
    @FXML
    private TextField projectBudgetField;
    @FXML
    private TextField searchField;
    @FXML
    private Button createUserButton;
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
    private Button cancelButton;
    @FXML
    private TableView<Project> projectTableView;
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
    @FXML
    private DatePicker projectStartDatePicker;
    @FXML
    private DatePicker projectEndDatePicker;
    @FXML
    private Button generateReportButton;

    @FXML
    private TextField searchSpecialistField;
    @FXML
    private TableView<Specialist> specialistTableView;
    @FXML
    private TableColumn<Specialist, Integer> specialistIdColumn;
    @FXML
    private TableColumn<Specialist, String> firstNameColumn;
    @FXML
    private TableColumn<Specialist, String> lastNameColumn;
    @FXML
    private TableColumn<Specialist, String> middleNameColumn;
    @FXML
    private TableColumn<Specialist, String> contactInfoColumn;
    @FXML
    private TableColumn<Specialist, Integer> hoursColumn; // Исправленное название
    @FXML
    private TextField editHoursField; // Исправленное название
    @FXML
    private Button onSaveSpecialistChangesButtonClick;
    @FXML
    private Button cancelSpecialistButton;

    // Новая таблица для отображения проектов специалиста
    @FXML
    private TableView<ProjectSp> specialistProjectsTableView;
    @FXML
    private TableColumn<ProjectSp, Integer> projectIdCol;
    @FXML
    private TableColumn<ProjectSp, String> projectNameCol;
    @FXML
    private TableColumn<ProjectSp, LocalDate> projectStartDateCol;
    @FXML
    private TableColumn<ProjectSp, LocalDate> projectEndDateCol;
    @FXML
    private TableColumn<ProjectSp, Integer> projectHoursCol;

    // Константа для подключения к базе данных
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fors1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "r10270707";




    // Метод загрузки ролей в комбобокс
    private void loadRolesInComboBox() {
        newUserRoleComboBox.getItems().addAll("Админ", "Руководитель");
    }

    //
    private ObservableList<String> roles = FXCollections.observableArrayList();

    // Обработчик нажатия на кнопку создания пользователя
    @FXML
    public void onCreateUserButtonClick() {
        String username = newUsernameField.getText();
        String email = newEmailField.getText();
        String password = newPasswordField1.getText();
        String role = newUserRoleComboBox.getValue();

        // Проверка на пустые поля
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            logger.log(Level.WARNING, "Попытка создания пользователя с пустыми полями");
            showErrorDialog("Заполните все поля!");
            return;
        }

        // Проверка на валидность email
        if (!isValidEmail(email)) {
            logger.log(Level.WARNING, "Попытка создания пользователя с неверным email: " + email);
            showErrorDialog("Неверный формат email!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (userExists(connection, username, email)) {
                logger.log(Level.WARNING, "Попытка создания пользователя с существующим логином или email: " + username + " / " + email);
                showErrorDialog("Пользователь с таким логином или email уже существует!");
                return;
            }

            String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, password);
                statement.setString(4, role);
                statement.executeUpdate();
                logger.log(Level.INFO, "Создан новый пользователь: " + username);
            }

            loadUserData();
            clearFieldsUser();
            showInfoDialog("Пользователь успешно добавлен!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при добавлении пользователя", e);
            showErrorDialog("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    // Проверка существования пользователя с таким же логином или email
    private boolean userExists(Connection connection, String username, String email) throws SQLException {
        String query = "SELECT 1 FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Обработчик нажатия на кнопку сохранения изменений пользователя
    @FXML
    public void onSaveUserChangesButtonClick() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            logger.log(Level.WARNING, "Попытка обновления данных пользователя без выбора пользователя");
            showErrorDialog("Выберите пользователя для редактирования!");
            return;
        }

        String username = editUsernameField.getText();
        String email = editEmailField.getText();
        String password = editPasswordField1.getText();

        // Проверка на пустые поля
        if (username.isEmpty() || email.isEmpty()) {
            logger.log(Level.WARNING, "Попытка обновления данных пользователя с пустыми полями");
            showErrorDialog("Заполните все поля!");
            return;
        }

        // Проверка на валидность email
        if (!isValidEmail(email)) {
            logger.log(Level.WARNING, "Попытка обновления данных пользователя с неверным email: " + email);
            showErrorDialog("Неверный формат email!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, password);
                statement.setInt(4, selectedUser.getId());
                statement.executeUpdate();
                logger.log(Level.INFO, "Обновлены данные пользователя: " + username);
            }
            loadUserData();
            clearFieldsUserEdit();
            showInfoDialog("Данные пользователя успешно обновлены!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при обновлении данных пользователя", e);
            showErrorDialog("Ошибка при обновлении данных пользователя: " + e.getMessage());
        }
    }
    // Обработчик нажатия на кнопку удаления пользователя
    @FXML
    public void onDeleteUserButtonClick() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            logger.log(Level.WARNING, "Попытка удаления пользователя без выбора пользователя");
            showErrorDialog("Выберите пользователя для удаления!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, selectedUser.getId());
                statement.executeUpdate();
                logger.log(Level.INFO, "Удален пользователь: " + selectedUser.getUsername());
            }
            loadUserData();
            showInfoDialog("Пользователь успешно удален!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении пользователя", e);
            showErrorDialog("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    // Обработчик нажатия на кнопку поиска пользователя
    @FXML
    public void searchUser() {
        String searchText = searchUserField.getText().trim();
        logger.log(Level.INFO, "Поиск пользователя: " + searchText);

        if (searchText.isEmpty()) {
            loadUserData();
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username LIKE ? OR email LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + searchText + "%");
                statement.setString(2, "%" + searchText + "%");
                try (ResultSet resultSet = statement.executeQuery()) {
                    ObservableList<User> users = FXCollections.observableArrayList();
                    while (resultSet.next()) {
                        users.add(createUserFromResultSet(resultSet));
                    }
                    userTableView.setItems(users);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при поиске пользователей", e);
            showErrorDialog("Ошибка при поиске пользователей: " + e.getMessage());
        }
    }

    // Метод для создания объекта User из ResultSet
    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String role = resultSet.getString("role");
        logger.log(Level.INFO, "Создан объект User: " + username);
        return new User(id, username, email, role);
    }
    // Метод загрузки данных пользователей
    // Метод загрузки данных пользователей
    public void loadUserData() {
        logger.log(Level.INFO, "Загрузка данных пользователей");
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                ObservableList<User> users = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    users.add(createUserFromResultSet(resultSet));
                }
                userTableView.setItems(users);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке данных пользователей", e);
            showErrorDialog("Ошибка при загрузке данных пользователей: " + e.getMessage());
        }
    }

    // Метод загрузки данных проектов
    public void loadProjectData() {
        projectTableView.getItems().clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM projects");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                projectTableView.getItems().add(createProjectFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке данных о проектах", e);
            showErrorDialog("Ошибка при загрузке данных о проектах: " + e.getMessage());
        }
    }

    // Метод для создания объекта Project из ResultSet
    private Project createProjectFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
        int budget = resultSet.getInt("budget");
        return new Project(id, name, description, startDate, endDate, budget);
    }

    // Обработчик нажатия на кнопку поиска проекта
    @FXML
    public void searchProject() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadProjectData();
            return;
        }

        LocalDate searchDate = null;
        int searchBudget = 0;
        try {
            searchDate = LocalDate.parse(searchText);
        } catch (DateTimeParseException ignored) {
            try {
                searchBudget = Integer.parseInt(searchText);
            } catch (NumberFormatException ignored2) {
                // Игнорируем, если не удалось преобразовать в дату или число
            }
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM projects WHERE name LIKE ? OR description LIKE ? OR start_date = ? OR end_date = ? OR budget = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + searchText + "%");
                statement.setString(2, "%" + searchText + "%");
                statement.setObject(3, searchDate);
                statement.setObject(4, searchDate);
                statement.setInt(5, searchBudget);
                try (ResultSet resultSet = statement.executeQuery()) {
                    ObservableList<Project> projects = FXCollections.observableArrayList();
                    while (resultSet.next()) {
                        projects.add(createProjectFromResultSet(resultSet));
                    }
                    projectTableView.setItems(projects);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при поиске проектов", e);
            showErrorDialog("Ошибка при поиске проектов: " + e.getMessage());
        }
    }

    // Обработчик нажатия на кнопку создания проекта
    @FXML
    private void onCreateProjectButtonClick() {
        String projectName = projectNameField.getText();
        String projectDescription = projectDescriptionArea.getText();
        LocalDate startDate = projectStartDatePicker.getValue();
        LocalDate endDate = projectEndDatePicker.getValue();
        String budgetText = projectBudgetField.getText();

        // Проверка на пустые поля
        if (projectName.isEmpty() || startDate == null || endDate == null || budgetText.isEmpty()) {
            showErrorDialog("Заполните все поля!");
            return;
        }

        // Проверка на корректность дат
        if (endDate.isBefore(startDate)) {
            showErrorDialog("Дата окончания проекта не может быть раньше даты начала!");
            return;
        }

        try {
            int budget = Integer.parseInt(budgetText);
            // Проверка на отрицательный бюджет
            if (budget <= 0) {
                showErrorDialog("Бюджет должен быть положительным числом!");
                return;
            }

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO projects (name, description, start_date, end_date, budget) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, projectName);
                    statement.setString(2, projectDescription);
                    statement.setDate(3, Date.valueOf(startDate));
                    statement.setDate(4, Date.valueOf(endDate));
                    statement.setInt(5, budget);
                    statement.executeUpdate();

                    // Получаем сгенерированный ID проекта
                    int projectId = 0;
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            projectId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Не удалось получить ID созданного проекта.");
                        }
                    }

                    // Вставляем запись в таблицу project_specialists для руководителя
                    String roleQuery = "SELECT id FROM roles WHERE name = 'Руководитель'";
                    int roleId = 0;
                    try (PreparedStatement roleStatement = connection.prepareStatement(roleQuery);
                         ResultSet roleResultSet = roleStatement.executeQuery()) {
                        if (roleResultSet.next()) {
                            roleId = roleResultSet.getInt("id");
                        } else {
                            throw new SQLException("Роль 'Руководитель' не найдена.");
                        }
                    }

                    String specialistQuery = "INSERT INTO project_specialists (project_id, role_id) VALUES (?, ?)";
                    try (PreparedStatement specialistStatement = connection.prepareStatement(specialistQuery)) {
                        specialistStatement.setInt(1, projectId);
                        specialistStatement.setInt(2, roleId);
                        specialistStatement.executeUpdate();
                    }
                }

                loadProjectData();
                clearFields();
                showInfoDialog("Проект успешно создан!");
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Некорректный формат бюджета!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании проекта", e);
            showErrorDialog("Ошибка при создании проекта: " + e.getMessage());
        }
    }
    // Обработчик нажатия на кнопку обновления проекта
    @FXML
    private void updateProject() {
        Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showErrorDialog("Выберите проект для обновления!");
            return;
        }

        String newName = projectNameField.getText();
        String newDescription = projectDescriptionArea.getText();
        LocalDate newStartDate = projectStartDatePicker.getValue();
        LocalDate newEndDate = projectEndDatePicker.getValue();
        String newBudgetText = projectBudgetField.getText();

        // Проверка на пустые поля
        if (newName.isEmpty() || newStartDate == null || newEndDate == null || newBudgetText.isEmpty()) {
            showErrorDialog("Заполните все поля!");
            return;
        }

        // Проверка на корректность дат
        if (newEndDate.isBefore(newStartDate)) {
            showErrorDialog("Дата окончания проекта не может быть раньше даты начала!");
            return;
        }

        try {
            int newBudget = Integer.parseInt(newBudgetText);

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "UPDATE projects SET name = ?, description = ?, start_date = ?, end_date = ?, budget = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, newName);
                    statement.setString(2, newDescription);
                    statement.setDate(3, Date.valueOf(newStartDate));
                    statement.setDate(4, Date.valueOf(newEndDate));
                    statement.setInt(5, newBudget);
                    statement.setInt(6, selectedProject.getId());
                    statement.executeUpdate();
                }
                loadProjectData();
                clearFields();
                showInfoDialog("Данные проекта успешно обновлены!");
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Некорректный формат бюджета!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при обновлении данных проекта", e);
            showErrorDialog("Ошибка при обновлении данных проекта: " + e.getMessage());
        }
    }

    // Обработчик нажатия на кнопку удаления проекта
    @FXML
    private void onDeleteProjectButtonClick() {
        Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showErrorDialog("Выберите проект для удаления!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM projects WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, selectedProject.getId());
                statement.executeUpdate();
            }
            loadProjectData();
            showInfoDialog("Проект успешно удален!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении проекта", e);
            showErrorDialog("Ошибка при удалении проекта: " + e.getMessage());
        }
    }

    // Метод открытия окна авторизации
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при открытии окна авторизации", e);
            showErrorDialog("Ошибка при открытии окна авторизации: " + e.getMessage());
        }
    }

    // Метод загрузки данных проектных заявок
    private void loadProjectRequests() {
        projectRequests.clear();
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
                    projectRequests.add(createProjectRequestFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке данных о проектных заявках", e);
            showErrorDialog("Ошибка при загрузке данных о проектных заявках: " + e.getMessage());
        }
    }

    // Метод для создания объекта ProjectRequest из ResultSet
    private ProjectRequest createProjectRequestFromResultSet(ResultSet resultSet) throws SQLException {
        int projectId = resultSet.getInt("project_id");
        String projectName = resultSet.getString("project_name");
        String roleName = resultSet.getString("role_name");
        String startDate = resultSet.getString("start_date");
        String endDate = resultSet.getString("end_date");
        int roleId = resultSet.getInt("role_id");
        return new ProjectRequest(projectId, projectName, roleId, roleName, startDate, endDate);
    }

    // Метод загрузки списка специалистов по роли
    private ObservableList<String> loadSpecialistsByRole(int roleId) {
        ObservableList<String> specialists = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT CONCAT_WS(' ', s.first_name, s.last_name, s.middle_name) AS full_name " +
                    "FROM specialists s " +
                    "JOIN specialist_roles sr ON s.id = sr.specialist_id " +
                    "WHERE sr.role_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, roleId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        specialists.add(resultSet.getString("full_name"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке специалистов", e);
            showErrorDialog("Ошибка при загрузке специалистов: " + e.getMessage());
        }
        return specialists;
    }

    @FXML
    private void assignSpecialists() {
        String specialistName = specialistComboBox.getValue();
        ProjectRequest selectedRequest = projectRequestListView.getSelectionModel().getSelectedItem();

        if (specialistName == null || selectedRequest == null) {
            showErrorDialog("Выберите специалиста и проектную заявку!");
            return;
        }

        int projectId = selectedRequest.getId();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Находим ID специалиста
            String specialistQuery = "SELECT id FROM specialists WHERE CONCAT_WS(' ', first_name, last_name, middle_name) = ?";
            int specialistId = 0;
            try (PreparedStatement specialistStatement = connection.prepareStatement(specialistQuery)) {
                specialistStatement.setString(1, specialistName);
                try (ResultSet resultSet = specialistStatement.executeQuery()) {
                    if (resultSet.next()) {
                        specialistId = resultSet.getInt("id");
                    } else {
                        throw new SQLException("Специалист не найден.");
                    }
                }
            }

            // Обновляем project_specialists
            String updateQuery = "UPDATE project_specialists SET specialist_id = ? WHERE project_id = ? AND specialist_id IS NULL";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setInt(1, specialistId);
                statement.setInt(2, projectId);
                statement.executeUpdate();
            }

            // Получаем даты начала и окончания проекта
            String dateQuery = "SELECT start_date, end_date FROM project_specialists WHERE project_id = ?";
            try (PreparedStatement dateStatement = connection.prepareStatement(dateQuery)) {
                dateStatement.setInt(1, projectId);
                try (ResultSet resultSet = dateStatement.executeQuery()) {
                    if (resultSet.next()) {
                        LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();

                        // Вычисляем количество дней работы
                        long days = ChronoUnit.DAYS.between(startDate, endDate);

                        // Вычисляем рабочие часы между начальной и конечной датами
                        int workingHours = calculateWorkingHours(startDate, endDate);

                        // Обновляем количество часов в таблице specialists
                        String updateHoursQuery = "UPDATE specialists SET hours =hours + ? WHERE id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateHoursQuery)) {
                            updateStatement.setInt(1, workingHours);
                            updateStatement.setInt(2, specialistId);
                            updateStatement.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Информация о проекте не найдена.");
                    }
                }
            }

            loadProjectRequests();
            specialistComboBox.setValue(null);
            projectRequestListView.getSelectionModel().clearSelection();
            showInfoDialog("Специалист успешно добавлен к проекту!");
            loadSpecialistsData();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при добавлении специалиста к проекту", e);
            showErrorDialog("Ошибка при добавлении специалиста к проекту: " + e.getMessage());
        }
    }

    private int calculateWorkingHours(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        int workingHours = 0;
        for (int i = 0; i <= days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingHours += 8; // Добавляем 8 часов за каждый рабочий день
            }
        }
        return workingHours;
    }
    // Обработчик нажатия на пустую область ListView
    @FXML
    private void handleEmptyClickProject(MouseEvent event) {
        if (event.getTarget() instanceof Pane) {
            projectRequestListView.getSelectionModel().clearSelection();
        }
    }

    // Обработчик нажатия на кнопку "Назад"
    @FXML
    private void onCancelButtonClick() {
        openLoginWindow();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Обработчик нажатия на кнопку генерации отчета
    @FXML
    private void generateReport() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PrintWriter fileWriter = new PrintWriter(new FileWriter("report.txt"))) {
            String query = "SELECT p.name AS project_name, r.name AS role_name, " +
                    "ps.start_date, ps.end_date, s.first_name, s.last_name, s.middle_name " +
                    "FROM project_specialists ps " +
                    "JOIN projects p ON ps.project_id = p.id " +
                    "JOIN roles r ON ps.role_id = r.id " +
                    "JOIN specialists s ON ps.specialist_id = s.id";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                fileWriter.write("Отчет\n\n");
                while (resultSet.next()) {
                    String projectName = resultSet.getString("project_name");
                    String roleName = resultSet.getString("role_name");
                    String startDate = resultSet.getString("start_date");
                    String endDate = resultSet.getString("end_date");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String middleName = resultSet.getString("middle_name");
                    String fullName = String.format("%s %s %s", firstName, lastName, middleName);

                    fileWriter.write(String.format("Проект: %s\n", projectName));
                    fileWriter.write(String.format("Роль: %s\n", roleName));
                    fileWriter.write(String.format("Специалист: %s\n", fullName));
                    fileWriter.write(String.format("Период: %s - %s\n\n", startDate, endDate));
                }
            }
            showInfoDialog("Отчет успешно создан (report.txt)!");
        } catch (SQLException | IOException e) {
            logger.log(Level.SEVERE, "Ошибка при создании отчета", e);
            showErrorDialog("Ошибка при создании отчета: " + e.getMessage());
        }
    }


    @FXML
    private void handleEmptyClickSpecialist(MouseEvent event) {
        if (event.getTarget() instanceof Pane) {
            // Очистка поля для редактирования часов
            editHoursField.clear();

            // Снятие выделения со строки в таблице специалистов
            specialistTableView.getSelectionModel().clearSelection();

            // Очистка таблицы проектов специалиста
            specialistProjectsTableView.getItems().clear();
        }
    }

    @FXML
    private void searchSpecialist() {
        String searchText = searchSpecialistField.getText().trim();
        logger.log(Level.INFO, "Поиск специалиста: " + searchText);

        if (searchText.isEmpty()) {
            loadSpecialistsData();
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT s.id, s.first_name, s.last_name, s.middle_name, s.contact_info, s.hours " +
                            "FROM specialists s " +
                            "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR s.middle_name LIKE ?")) {
                statement.setString(1, "%" + searchText + "%");
                statement.setString(2, "%" + searchText + "%");
                statement.setString(3, "%" + searchText + "%");
                ResultSet resultSet = statement.executeQuery();
                ObservableList<Specialist> specialists = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    Specialist specialist = new Specialist(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("middle_name"),
                            resultSet.getString("contact_info"),
                            resultSet.getInt("hours")
                    );
                    specialists.add(specialist);
                }
                specialistTableView.setItems(specialists);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при поиске специалистов", e);
            showErrorDialog("Ошибка при поиске специалистов: " + e.getMessage());
        }
    }

    // Загрузка данных о специалистах
    public void loadSpecialistsData() {
        specialistTableView.getItems().clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT s.id, s.first_name, s.last_name, s.middle_name, s.contact_info, s.hours " +
                            "FROM specialists s")) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Specialist specialist = new Specialist(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("middle_name"),
                            resultSet.getString("contact_info"),
                            resultSet.getInt("hours")
                    );
                    specialistTableView.getItems().add(specialist);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке данных о специалистах", e);
            showErrorDialog("Ошибка при загрузке данных о специалистах: " + e.getMessage());
        }
    }

    // Метод для загрузки проектов, в которых участвует специалист
    public void loadSpecialistProjects(int specialistId) {
        specialistProjectsTableView.getItems().clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT p.id, p.name, ps.start_date, ps.end_date, " +
                    "SUM(DATEDIFF(ps.end_date, ps.start_date) * 8 - " +
                    "(DATEDIFF(ps.end_date, ps.start_date) DIV 7 * 2 * 8) - " +
                    "CASE " +
                    "WHEN WEEKDAY(ps.end_date) = 5 THEN 8 " +
                    "WHEN WEEKDAY(ps.end_date) = 6 THEN 8 " +
                    "ELSE 0 " +
                    "END) AS hours_worked " +
                    "FROM projects p " +
                    "JOIN project_specialists ps ON p.id = ps.project_id " +
                    "WHERE ps.specialist_id = ? " +
                    "GROUP BY p.id, p.name, ps.start_date, ps.end_date";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, specialistId);
                ResultSet resultSet = statement.executeQuery();
                ObservableList<ProjectSp> projects = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    ProjectSp project = new ProjectSp(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getDate("start_date").toLocalDate(),
                            resultSet.getDate("end_date").toLocalDate(),
                            resultSet.getInt("hours_worked") // Исправлено на "hours_worked"
                    );
                    projects.add(project);
                }
                specialistProjectsTableView.setItems(projects);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке проектов специалиста", e);
            showErrorDialog("Ошибка при загрузке проектов специалиста: " + e.getMessage());
        }
    }

    @FXML
    public void onSaveSpecialistChangesButtonClick() {
        // ... (реализация метода для обновления информации о специалисте в базе данных)
    }

    // Методы для очистки полей ввода
    private void clearFields() {
        projectNameField.clear();
        projectDescriptionArea.clear();
        projectStartDatePicker.setValue(null);
        projectEndDatePicker.setValue(null);
        projectBudgetField.clear();
    }

    private void clearFieldsUser() {
        newUsernameField.clear();
        newEmailField.clear();
        newPasswordField1.clear();
        newUserRoleComboBox.setValue(null);
    }

    private void clearFieldsUserEdit() {
        editUsernameField.clear();
        editEmailField.clear();
        editPasswordField1.clear();
    }

    // Обработчики нажатий на пустую область для очистки полей ввода
    @FXML
    private void handleEmptyClick(MouseEvent event) {
        if (event.getTarget() instanceof Pane) {
            clearFields();
        }
    }

    @FXML
    private void handleEmptyClickUser(MouseEvent event) {
        if (event.getTarget() instanceof Pane) {
            clearFieldsUserEdit();
        }
    }

    // Метод для отображения информационного диалогового окна
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для отображения диалогового окна об ошибке
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для валидации email
    private boolean isValidEmail(String email) {
        return email.matches(".+@.+\\..+");
    }

    // Метод инициализации контроллера
    public void initialize() {
        // Делаем кнопку создания пользователя неактивной, если поля не заполнены
        createUserButton.disableProperty().bind(
                newUsernameField.textProperty().isEmpty()
                        .or(newEmailField.textProperty().isEmpty())
                        .or(newPasswordField1.textProperty().isEmpty())
        );

        // Настраиваем столбцы таблицы пользователей
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Загружаем данные пользователя в поля редактирования при выборе строки в таблице
        userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editUsernameField.setText(newSelection.getUsername());
                editEmailField.setText(newSelection.getEmail());
            } else {
                clearFieldsUserEdit();
            }
        });

        // Загружаем данные проекта в поля редактирования при выборе строки в таблице
        projectTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                projectNameField.setText(newValue.getName());
                projectDescriptionArea.setText(newValue.getDescription());
                projectStartDatePicker.setValue(newValue.getStartDate());
                projectEndDatePicker.setValue(newValue.getEndDate());
                projectBudgetField.setText(String.valueOf(newValue.getBudget()));
            } else {
                clearFields();
            }
        });

        // Загружаем специалистов в комбобокс при выборе проектной заявки
        projectRequestListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                specialistComboBox.setItems(loadSpecialistsByRole(newValue.getRoleId()));
            }
        });
        editHoursField.setEditable(false); // Делает поле некликабельным
        // Настраиваем столбцы таблицы проектов
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        projectStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        projectEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        projectBudgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));


        // Инициализация для специалистов
        specialistIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hours"));

        // Инициализация таблицы проектов
        projectIdCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectNameCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        projectEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        projectHoursCol.setCellValueFactory(new PropertyValueFactory<>("hours")); // Инициализация нового столбца


        // Загружаем данные в таблицы и комбобоксы
        loadRolesInComboBox();
        loadUserData();
        loadProjectData();
        loadProjectRequests();
        loadSpecialistsData();
        projectRequestListView.setItems(projectRequests);

        // Обработчик для выбора строки в таблице
        specialistTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editHoursField.setText(String.valueOf(newValue.getHours())); // Заполнение поля для редактирования
                loadSpecialistProjects(newValue.getId()); // Загружаем проекты специалиста
            } else {
                editHoursField.clear(); // Очистка поля для редактирования
                specialistProjectsTableView.getItems().clear();
            }
        });
        // Обработчик для выбора строки в таблице проектов
        specialistProjectsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Загрузка количества часов в выбранном проекте в текстовое поле
                editHoursField.setText(String.valueOf(newValue.getHours()));
            } else {
                // Если нет выбранного проекта, очистить текстовое поле
                editHoursField.clear();
            }
            });
        // Обработчик для выбора строки в таблице проектов специалиста
        specialistProjectsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ProjectSp selectedProject = newValue;
                int hours = selectedProject.getHours();
                // Загрузка количества часов в выбранном проекте в текстовое поле
                editHoursField.setText(String.valueOf(hours));
            } else {
                // Если нет выбранного проекта, очистить текстовое поле
                editHoursField.clear();
            }
        });
    }
 }
