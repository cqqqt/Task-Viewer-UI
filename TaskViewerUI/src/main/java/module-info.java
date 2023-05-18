module com.taskviewer.api.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;


    opens com.taskviewer.api.view to javafx.fxml;
    exports com.taskviewer.api.view;
}