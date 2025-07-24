module com.example.relwey {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;

    opens backend to javafx.base, javafx.fxml;

    opens com.example.relwey to javafx.fxml;
    exports com.example.relwey;
}