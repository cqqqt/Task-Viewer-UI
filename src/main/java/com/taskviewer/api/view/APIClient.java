package com.taskviewer.api.view;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class APIClient {
    private static final String API_URL = "http://localhost:8080/api/v1";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private TokenManager tokenManager;

    public APIClient(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public String sendGetRequest(String endpoint) throws IOException {
        URL url = new URL(API_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + tokenManager.getAccessToken());

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } else {
            throw new IOException("Error response: " + responseCode);
        }
    }

    public String sendPostRequest(String endpoint, String body) throws IOException {
        URL url = new URL(API_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + tokenManager.getAccessToken());
        connection.setDoOutput(true);

        connection.getOutputStream().write(body.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } else {
            throw new IOException("Error response: " + responseCode);
        }
    }

    public String sendPutRequest(String endpoint, String body) throws IOException {
        URL url = new URL(API_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + tokenManager.getAccessToken());
        connection.setDoOutput(true);

        connection.getOutputStream().write(body.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } else {
            throw new IOException("Error response: " + responseCode);
        }
    }

    public String sendDeleteRequest(String endpoint) throws IOException {
        URL url = new URL(API_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + tokenManager.getAccessToken());

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } else {
            throw new IOException("Error response: " + responseCode);
        }
    }

    public String sendPatchRequest(String endpoint, String body) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPatch httpPatch = new HttpPatch(API_URL + endpoint);
        httpPatch.setHeader(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + tokenManager.getAccessToken());

        if (body != null) {
            StringEntity requestEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
            httpPatch.setEntity(requestEntity);
        }

        HttpResponse response = httpClient.execute(httpPatch);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } else {
            throw new IOException("Error response: " + statusCode);
        }

        return null;
    }


    public void createTask(String username, String title, String about, String status, String priority, String due) throws IOException {
        String endpoint = "/tasks";
        JsonObject taskJson = new JsonObject();
        taskJson.addProperty("username", username);
        taskJson.addProperty("title", title);
        taskJson.addProperty("about", about);
        taskJson.addProperty("status", status);
        taskJson.addProperty("priority", priority);
        taskJson.addProperty("due", due);

        Gson gson = new Gson();
        String body = gson.toJson(taskJson);

        sendPostRequest(endpoint, body);
    }

    public void deleteTask(int taskId) throws IOException {
        String endpoint = "/tasks/" + taskId;
        sendDeleteRequest(endpoint);
    }

    public User getCurrentUser() throws IOException {
        String endpoint = "/users/me";
        String response = sendGetRequest(endpoint);

        return User.fromJson(response);
    }

    public List<Task> getTasks() throws IOException {
        String endpoint = "/tasks";
        String response = sendGetRequest(endpoint);

        // Пример использования Gson для десериализации JSON
        Gson gson = new Gson();
        Type taskListType = new TypeToken<List<Task>>(){}.getType();

        return gson.fromJson(response, taskListType);
    }

    public List<Task> getTasks(String title, String username, String status, String priority, String estimate, String sort) throws IOException {
        // Формируем URL-запрос с параметрами
        String endpoint = "/tasks";
        StringBuilder urlBuilder = new StringBuilder(endpoint + "?");

        if (title != null && !title.isEmpty()) {
            urlBuilder.append("title=").append(URLEncoder.encode(title, StandardCharsets.UTF_8)).append("&");
        }
        if (username != null && !username.isEmpty()) {
            urlBuilder.append("username=").append(URLEncoder.encode(username, StandardCharsets.UTF_8)).append("&");
        }
        if (status != null && !status.isEmpty()) {
            urlBuilder.append("status=").append(URLEncoder.encode(status, StandardCharsets.UTF_8)).append("&");
        }
        if (priority != null && !priority.isEmpty()) {
            urlBuilder.append("priority=").append(URLEncoder.encode(priority, StandardCharsets.UTF_8)).append("&");
        }
        if (estimate != null && !estimate.isEmpty()) {
            urlBuilder.append("estimate=").append(URLEncoder.encode(estimate, StandardCharsets.UTF_8)).append("&");
        }
        if (sort != null && !sort.isEmpty()) {
            urlBuilder.append("sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8)).append("&");
        }

        String url = urlBuilder.toString();

        // Отправляем GET-запрос
        String response = sendGetRequest(url);

        // Пример использования Gson для десериализации JSON
        Gson gson = new Gson();
        Type taskListType = new TypeToken<List<Task>>(){}.getType();

        return gson.fromJson(response, taskListType);
    }

    public void updateTask(int taskId, String username, String title, String about, String status, String priority, String due) throws IOException {
        String endpoint = "/tasks/" + taskId;
        JsonObject taskJson = new JsonObject();
        taskJson.addProperty("username", username);
        taskJson.addProperty("title", title);
        taskJson.addProperty("about", about);
        taskJson.addProperty("status", status);
        taskJson.addProperty("priority", priority);
        taskJson.addProperty("due", due);

        Gson gson = new Gson();
        String body = gson.toJson(taskJson);

        sendPutRequest(endpoint, body);
    }

    public List<Task> getWeeklyTasksByNicknameId(String userId) throws IOException {
        String endpoint = "/weekly/" + userId;
        String response = sendGetRequest(endpoint);

        Gson gson = new Gson();
        Type taskListType = new TypeToken<List<Task>>(){}.getType();

        return gson.fromJson(response, taskListType);
    }

    public List<Comment> getComments(String endpoint) throws IOException {
        String response = sendGetRequest(endpoint);
        Type listType = new TypeToken<List<Comment>>() {}.getType();
        return new Gson().fromJson(response, listType);
    }

    public String closeTask(int taskId) throws IOException {
        String endpoint = "/tasks/close/" + taskId;
        return sendPatchRequest(endpoint, "");
    }

    public String assignTask(int taskId, String assigneeUsername) throws IOException {
        String endpoint = "/tasks/assign/" + taskId + "/" + assigneeUsername;
        return sendPostRequest(endpoint, "");
    }

    public String duplicateTask(int taskId) throws IOException {
        String endpoint = "/tasks/replicate/" + taskId;
        return sendPostRequest(endpoint, "");
    }

    public List<Task> getWeeklyTasks() throws IOException {
        String endpoint = "/weekly";
        String response = sendGetRequest(endpoint);
        // Преобразование JSON-ответа в список задач
        return convertJsonToTaskList(response);
    }

    public List<Task> convertJsonToTaskList(String json) {
        Gson gson = new Gson();
        TypeToken<List<Task>> token = new TypeToken<List<Task>>() {};
        return gson.fromJson(json, token.getType());
    }

    public void addComment(int taskId, String content) throws IOException {
        String endpoint = "/comments";
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("content", content);
        requestBody.addProperty("task", taskId);
        String response = sendPostRequest(endpoint, requestBody.toString());
    }

    public void updateComment(int commentId, String content) throws IOException {
        String endpoint = "/comments/" + commentId;
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("content", content);
        sendPutRequest(endpoint, requestBody.toString());
    }

    public void deleteComment(int commentId) throws IOException {
        String endpoint = "/comments/" + commentId;
        sendDeleteRequest(endpoint);
    }

    // Пример использования
    public static void main(String[] args) {
        TokenManager tokenManager = TokenManager.getInstance();
        APIClient apiClient = new APIClient(tokenManager);

        try {
            String response = apiClient.sendGetRequest("/tasks");
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
