module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;

    exports org.example;
    opens org.example to javafx.fxml;

    exports org.example.controller;
    opens org.example.controller to javafx.fxml;

    exports org.example.model;
    opens org.example.model to javafx.base, javafx.fxml;

    exports org.example.dao;
    opens org.example.dao to javafx.base;

    opens org.example.utils to javafx.base;
}