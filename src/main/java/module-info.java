module com.example.diplom {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires spring.beans;
    requires spring.context;
    requires spring.jdbc;
    opens com.example.diplom to javafx.fxml;
    exports com.example.diplom;
}