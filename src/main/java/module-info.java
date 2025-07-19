module com.example.relwey {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens com.example.relwey to javafx.fxml;
    exports com.example.relwey;
}