package com.example.diplom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Parent;
public class HelloApplication extends Application {
    private static Stage primaryStage;
    private static Stage loginStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginView();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();
    }

    public static void showMainView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Main Window");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Закрыть окно авторизации после открытия основного окна
        closeLoginView();
    }

    public static void showAdminView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Admin.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Admin Window");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        centerStage();

        // Закрыть окно авторизации после открытия окна администратора
        closeLoginView();
    }

    public static void showLeaveRequestForm() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("LeaveRequest.fxml"));
            Parent root = loader.load();
            Stage leaveRequestStage = new Stage();
            leaveRequestStage.setTitle("Заявка на ресурсы");
            leaveRequestStage.setScene(new Scene(root));
            leaveRequestStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для центрирования окна
    private static void centerStage() {
        if (primaryStage != null) {
            primaryStage.centerOnScreen();
        }
    }

    // Закрыть окно авторизации
    private static void closeLoginView() {
        if (loginStage != null) {
            loginStage.close();
        }
    }
}
