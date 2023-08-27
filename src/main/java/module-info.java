module com.taskviewer.api.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.poi.ooxml;

    opens com.taskviewer.api.view to javafx.fxml, com.google.gson;
    exports com.taskviewer.api.view;
}
