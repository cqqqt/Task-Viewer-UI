package com.taskviewer.api.view;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.google.gson.JsonObject;


public class LoginController implements Initializable {

    @FXML
    private TextField NicknameInput;

    @FXML
    private PasswordField PasswordInput;

    @FXML
    private Button loginButton;

    @FXML
    private Button registrationButton;

    private String host = "http://localhost:8080";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(this::handleLogin);
        registrationButton.setOnAction(this::handleRegistration);
    }

    @FXML
    private void handleRegistration(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registration.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка исключения
        }
    }

    private void handleLogin(ActionEvent event) {
        String username = NicknameInput.getText();
        String password = PasswordInput.getText();

        if (username.isEmpty()) {
            showErrorMessage("Ошибка авторизации", "Введите никнейм");
            return;
        }

        if (password.isEmpty()) {
            showErrorMessage("Ошибка авторизации", "Введите пароль");
            return;
        }

        try {
            // Отправляем POST запрос на авторизацию на сервере
            String response = sendPOSTRequest(host + "/api/v1/auth/login", "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
            if (response.startsWith("{\"access\"")) {
                // Обрабатываем ответ от сервера
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response);
                JsonNode accessTokenNode = jsonNode.get("access").get("token");
                JsonNode refreshTokenNode = jsonNode.get("refresh").get("token");

                if (accessTokenNode != null) {
                    String accessToken = accessTokenNode.asText();
                    String refreshToken = refreshTokenNode.asText();

                    // Если авторизация прошла успешно, сохраняем токен в TokenManager
                    TokenManager tokenManager = TokenManager.getInstance();
                    tokenManager.setTokens(accessToken, refreshToken);

                    // Открываем соответствующее окно на основе роли пользователя
                    String role = getRoleFromToken(accessToken);
                    if (role.equals("ADMIN")) {
                        openAdminWindow(event);
                    } else {
                        openUserWindow(event);
                    }
                } else {
                    // Если в ответе нет токена доступа, выводим сообщение об ошибке
                    String errorMessage = extractErrorMessage(response);
                    showErrorMessage("Ошибка авторизации", errorMessage);
                }
            } else {
                String errorMessage = extractErrorMessage(response);
                showErrorMessage("Ошибка", errorMessage);
            }

        } catch (IOException e) {
            // Обрабатываем ошибку ввода-вывода, например, если не удалось установить соединение с сервером
            showErrorMessage("Ошибка", "Произошла ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            // Обрабатываем любую другую ошибку при парсинге ответа от сервера
            showErrorMessage("Ошибка", "Произошла ошибка: " + e.getMessage());
        }
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private String getRoleFromToken(String token) {
        // Парсим токен, чтобы получить роль пользователя
        JsonObject jsonResponse = JsonParser.parseString(new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]))).getAsJsonObject();
        return jsonResponse.get("role").getAsString();
    }

    private String extractErrorMessage(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return jsonObject.get("content").getAsString();
    }

    private void openAdminWindow(ActionEvent event) throws IOException {
        // Открываем окно администратора
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin.fxml")));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (javafx.fxml.LoadException e) {
            e.printStackTrace(); // Вывод трассировки стека вызовов в консоль
        }
    }

    private void openUserWindow(ActionEvent event) throws IOException {
        // Открываем окно пользователя
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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
        if (statusCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned error code: " + statusCode);
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }

        bufferedReader.close();
        return response.toString();
    }
}