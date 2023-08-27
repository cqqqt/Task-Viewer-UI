package com.taskviewer.api.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class UserController {
    @FXML
    public Button showAllTasksButton;
    @FXML
    public Button closeTaskButton;
    @FXML
    private TableView<Task> tasksTableView;
    @FXML
    private TableColumn<Task, String> titleTableColumn;
    @FXML
    private TableColumn<Task, String> statusTableColumn;
    @FXML
    private TableColumn<Task, String> priorityTableColumn;
    @FXML
    private TableColumn<Task, String> usernameTableColumn;
    @FXML
    private Label taskNumberLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea aboutTextArea;
    @FXML
    private TextField priorityTextField;
    @FXML
    private TextField statusTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private ListView<Comment> commentsListView;
    @FXML
    private TextArea writeCommentTextArea;
    @FXML
    private Button addCommentButton;
    @FXML
    private Button updateCommentButton;
    @FXML
    private Button deleteCommentButton;
    @FXML
    private TextField searchCommentByUsernameTextField;
    @FXML
    private Button weeklyTasksButton;
    @FXML
    private TextField searchByTitleTextField;
    @FXML
    private TextField searchByStatusTextField;
    @FXML
    private TextField searchByPriorityTextField;
    @FXML
    private TextField searchByUsernameTextField;
    @FXML
    public Label helpLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button exitButton;

    private static String host = "http://localhost:8080";
    private APIClient apiClient;

    @FXML
    private void initialize() {
        apiClient = new APIClient(TokenManager.getInstance());

        try {
            // Выполнение API-запроса для получения данных о текущем пользователе
            User currentUser = apiClient.getCurrentUser();

            // Обновление Label с использованием полученных данных
            String fullName = currentUser.getFirstname() + " \"" + currentUser.getUsername() + "\" " + currentUser.getLastname();
            usernameLabel.setText(fullName);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки получения данных о пользователе из API
        }

        showAllTasksButton.setOnAction(event -> {
            fetchTasks();
        });
        helpLabel.setOnMouseClicked(event -> {

            String message = "Отображение задач:\n" +
                    "\n" +
                    "В левой части интерфейса находится таблица TableView, которая отображает список задач.\n" +
                    "Колонки таблицы представляют различные атрибуты задачи, такие как наименование, статус, приоритет и назначено на (задача назначена на пользователя с указанным никнеймом).\n" +
                    "Чтобы просмотреть подробности о задаче, выберите ее в таблице.\n" +
                    "\n" +
                    "Комментарии к задачам:\n" +
                    "\n" +
                    "В нижней части интерфейса вы можете видеть раздел комментариев \"Комментарии\".\n" +
                    "В списке ListView отображаются все комментарии, связанные с выбранной задачей.\n" +
                    "Чтобы добавить новый комментарий, введите текст в поле \"написать комментарий..\" и нажмите кнопку \"Добавить комментарий\".\n" +
                    "Чтобы обновить или удалить комментарий, выберите его в списке и нажмите соответствующую кнопку.\n" +
                    "Чтобы сортировать комментарии по id никнейма введите текст в поле \"id никнейма\".\n" +
                    "\n" +
                    "Поиск задач:\n" +
                    "\n" +
                    "Чтобы найти задачу по определенным критериям, воспользуйтесь полями поиска:\n" +
                    "- Никнейм: Введите никнейм в поле \"Никнейм\", чтобы отфильтровать задачи по назначенному пользователю.\n" +
                    "- Наименование: Введите наименование в поле \"Наименование\", чтобы отфильтровать задачи по наименованию.\n" +
                    "- Статус: Введите статус в поле \"Статус\", чтобы отфильтровать задачи по статусу.\n" +
                    "- Приоритет: Введите приоритет(1-5) в поле \"Приоритет\", чтобы отфильтровать задачи по статусу.\n" +
                    "Задачи возможно отфильтровать еще и с помощью кнопок:\n" +
                    "- Расписание на неделю: Нажмите на кнопку \"Мое расписание на неделю\", чтобы отфильтровать задачи за неделю.\n" +
                    "- Все задачи: Нажмите на кнопку \"Показать все задачи\", чтобы вывести все существующие в программе задачи(также сброс всех полей).\n" +
                    "\n" +
                    "Выход из программы:\n" +
                    "\n" +
                    "Чтобы выйти из программы, нажмите на крестик (закрытие окна) в правом верхнем углу интерфейса.\n";

            // Создание текстового элемента с сообщением
            Text text = new Text(message);
            text.setWrappingWidth(600);

            // Создание контейнера с прокруткой
            ScrollPane scrollPane = new ScrollPane(text);
            scrollPane.setPrefSize(650, 500);

            // Создание диалогового окна без заголовка
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Помощь");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            // Отображение диалогового окна и ожидание закрытия
            dialog.showAndWait();
        });
        closeTaskButton.setOnAction(event -> {
            try {
                closeTask();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        addCommentButton.setOnAction(event -> {
            try {
                addComment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        updateCommentButton.setOnAction(event -> {
            try {
                updateComment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        deleteCommentButton.setOnAction(event -> {
            try {
                deleteComment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Добавляем слушатель событий к полю searchByTitleTextField
        searchByTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        // Добавляем слушатель событий к полю searchByStatusTextField
        searchByStatusTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        exitButton.setOnAction(e -> {
            try {
                // Закрываем текущее окно
                Stage currentStage = (Stage) exitButton.getScene().getWindow();
                currentStage.close();

                // Открываем начальную форму при закрытии окна пользователя
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login.fxml"));
                Scene loginScene = new Scene(loginRoot);
                Stage loginStage = new Stage();
                loginStage.setScene(loginScene);
                loginStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Добавляем слушатель событий к полю searchByPriorityTextField
        searchByPriorityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        searchByUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        weeklyTasksButton.setOnAction(event -> searchWeeklyTasks());

        // Настройка соответствия полей модели Task со столбцами таблицы
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityTableColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        usernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Добавляем слушатель событий к текстовому полю searchCommentByUsernameTextField
        searchCommentByUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
            // Получаем текущее значение текстового поля
            int taskId = selectedTask.getId();
            // Проверяем, что поле не пустое
            if (!newValue.isEmpty()) {
                try {
                    // Преобразуем значение пользователя в int, если требуется
                    int userId = Integer.parseInt(newValue);

                    // Вызываем метод fetchCommentsByTask с заданным пользователем
                    fetchComments(taskId, userId);
                } catch (NumberFormatException e) {
                    // Обработка ошибки, если значение не является числом
                    e.printStackTrace();
                }
            }
        });


        tasksTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    int taskId = selectedTask.getId();
                    titleTextField.setText(selectedTask.getTitle());
                    aboutTextArea.setText(selectedTask.getAbout());
                    priorityTextField.setText(selectedTask.getPriority());
                    statusTextField.setText(selectedTask.getStatus());
                    usernameTextField.setText(selectedTask.getUsername());
                    dueDatePicker.setValue(LocalDate.parse(selectedTask.getDue(), DateTimeFormatter.ISO_DATE_TIME));

                    taskNumberLabel.setText("Задача №" + taskId);
                    if(Objects.equals(searchCommentByUsernameTextField.getText(), "")){
                        fetchComments(taskId, 0);
                    }
                    else{
                        fetchComments(taskId, Integer.parseInt(searchCommentByUsernameTextField.getText()));
                    }
                }
            } else {
                taskNumberLabel.setText("Задача №");
                clearTaskDetails();
                commentsListView.getItems().clear();
            }
        });

        fetchTasks();
    }

    private void fetchTasks() {
        try {
            // Очищаем таблицу перед загрузкой новых данных
            tasksTableView.getItems().clear();

            // Получаем значения полей поиска
            String title = searchByTitleTextField.getText();
            String status = searchByStatusTextField.getText();
            String priority = searchByPriorityTextField.getText();
            String username = searchByUsernameTextField.getText();

            // Проверяем, есть ли хотя бы одно непустое значение критериев поиска
            List<Task> tasks;
            if (!title.isEmpty() || !status.isEmpty() || !priority.isEmpty() || !username.isEmpty()) {
                // Получаем список задач с помощью API, передавая непустые значения критериев поиска
                tasks = apiClient.getTasks(title, username, status, priority, null, null);

                // Создаем объекты Task на основе полученных данных и добавляем их в таблицу
            }
            else {
                // Получаем список задач с помощью API
                tasks = apiClient.getTasks();

                // Создаем объекты Task на основе полученных данных и добавляем их в таблицу
            }
            tasksTableView.getItems().addAll(tasks);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки получения задач из API
        }
    }

    // Метод для закрытия задачи
    public void closeTask() throws IOException {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();

            // Отправка запроса на закрытие задачи с использованием API клиента
            apiClient.closeTask(taskId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успешно закрыто");
            alert.setHeaderText(null);
            alert.setContentText("Задача помечена как закрытая.");
            alert.showAndWait();
            fetchTasks();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Задача не выбрана.");
            alert.showAndWait();
        }
    }

    // Метод для добавления комментария
    public void addComment() throws IOException {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();
            String content = writeCommentTextArea.getText();

            // Отправка запроса на добавление комментария с использованием API клиента
            apiClient.addComment(taskId, content);

            writeCommentTextArea.clear();

            if(Objects.equals(searchCommentByUsernameTextField.getText(), "")){
                fetchComments(taskId, 0);
            }
            else{
                fetchComments(taskId, Integer.parseInt(searchCommentByUsernameTextField.getText()));
            }

        } else {
            System.out.println("No task selected.");
        }
    }

    // Метод для обновления комментария
    public void updateComment() throws IOException {
        Comment selectedComment = commentsListView.getSelectionModel().getSelectedItem();
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedComment != null) {
            int commentId = selectedComment.getId();
            int taskId = selectedTask.getId();
            String content = writeCommentTextArea.getText();

            // Отправка запроса на обновление комментария с использованием API клиента
            apiClient.updateComment(commentId, content);

            writeCommentTextArea.clear();

            if(Objects.equals(searchCommentByUsernameTextField.getText(), "")){
                fetchComments(taskId, 0);
            }
            else{
                fetchComments(taskId, Integer.parseInt(searchCommentByUsernameTextField.getText()));
            }

        } else {
            System.out.println("No comment selected.");
        }
    }

    // Метод для удаления комментария
    public void deleteComment() throws IOException {
        Comment selectedComment = commentsListView.getSelectionModel().getSelectedItem();
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedComment != null) {
            int commentId = selectedComment.getId();
            int taskId = selectedTask.getId();

            // Отправка запроса на удаление комментария с использованием API клиента
            apiClient.deleteComment(commentId);

            if(Objects.equals(searchCommentByUsernameTextField.getText(), "")){
                fetchComments(taskId, 0);
            }
            else{
                fetchComments(taskId, Integer.parseInt(searchCommentByUsernameTextField.getText()));
            }

        } else {
            System.out.println("No comment selected.");
        }
    }

    // Метод для получения комментариев
    public void fetchComments(int taskId, int userId) {
        try {
            // Очищаем список комментариев перед загрузкой новых данных
            commentsListView.getItems().clear();

            // Формируем запрос к API в зависимости от наличия идентификатора задачи и пользователя
            String endpoint = "/comments";
            if (userId > 0 && taskId > 0) {
                endpoint += "?user=" + userId + "&task=" + taskId;
            } else if (userId > 0) {
                endpoint += "?user=" + userId;
            } else if (taskId > 0) {
                endpoint += "?task=" + taskId;
            }

            // Получаем список комментариев с помощью API
            List<Comment> comments = apiClient.getComments(endpoint);

            // Добавляем только содержимое комментария в список
            commentsListView.getItems().addAll(comments);

        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки получения комментариев из API
        }
    }

    // Метод для поиска задач за неделю
    public void searchWeeklyTasks() {
        try {
            List<Task> weeklyTasks = apiClient.getWeeklyTasks();
            tasksTableView.getItems().clear();
            tasksTableView.getItems().addAll(weeklyTasks);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки получения задач за неделю через API
        }
    }

    // Метод для очистки полей задачи
    private void clearTaskDetails() {
        titleTextField.clear();
        aboutTextArea.clear();
        priorityTextField.clear();
        statusTextField.clear();
        usernameTextField.clear();
        dueDatePicker.setValue(null);
    }
}
