package com.taskviewer.api.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.stage.Stage;

public class RegistrationController implements Initializable {
    @FXML
    private TextField NicknameInput;

    @FXML
    private TextField firstnameInput;

    @FXML
    private TextField lastnameInput;

    @FXML
    private PasswordField PasswordInput;

    @FXML
    private Button registrationButton;

    @FXML
    private TextField emailInput;

    private String host = "http://localhost:8080";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registrationButton.setOnAction(this::handleRegistration);
    }

    @FXML
    void handleRegistration(ActionEvent event) {
        String username = NicknameInput.getText();
        String password = PasswordInput.getText();
        String email = emailInput.getText();
        String firstName = firstnameInput.getText();
        String lastName = lastnameInput.getText();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка регистрации");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, заполните все обязательные поля.");
            alert.showAndWait();
            return;
        }

        // Проверка формата email
        String emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
        if (!email.matches(emailRegex)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка регистрации");
            alert.setHeaderText(null);
            alert.setContentText("Неверный формат email.");
            alert.showAndWait();
            return;
        }

        try {
            // Отправляем POST запрос на регистрацию на сервере
            String response = sendPOSTRequest(host + "/api/v1/auth/register", "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\",\"firstname\":\"" + firstName + "\",\"lastname\":\"" + lastName + "\"}");

            // Обрабатываем ответ от сервера
            if (response.equals("success")) {
                showSuccessMessage("Регистрация успешна!");
                openLoginWindow();
            } else {
                String errorMessage = extractErrorMessage(response);
                showErrorMessage(errorMessage);
            }
        } catch (IOException e) {
            // Обработка ошибки ввода-вывода, например, если не удалось установить соединение с сервером
            showErrorMessage("Произошла ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            // Обработка любой другой ошибки
            showErrorMessage("Произошла ошибка: " + e.getMessage());
        }
    }

    private void showErrorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешно");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Закрытие текущего окна
            ((Stage) registrationButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractErrorMessage(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return jsonObject.get("content").getAsString();
    }

    private String sendPOSTRequest(String url, String body) throws IOException {
        // Создаем объект для отправки запроса
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Отправляем тело запроса
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(body.getBytes());
        outputStream.flush();
        outputStream.close();

        // Обрабатываем ответ от сервера
        int statusCode = connection.getResponseCode();
        if (statusCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            bufferedReader.close();
            return response.toString();
        } else {
            throw new IOException("Server returned error code: " + statusCode);
        }
    }
}
