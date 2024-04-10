package com.example.diplom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginView();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAdminView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Admin Window");
        primaryStage.setScene(scene);
        primaryStage.show();
        centerStage();
        primaryStage.show();
    }
    // Метод для отображения формы заявки на ресурсы
    public static void showLeaveRequestForm() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("LeaveRequest.fxml"));
            Scene scene = new Scene(loader.load());
            Stage leaveRequestStage = new Stage();
            leaveRequestStage.setScene(scene);
            leaveRequestStage.setTitle("Заявка на ресурсы");
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
}
