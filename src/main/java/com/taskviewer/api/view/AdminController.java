package com.taskviewer.api.view;

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
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class AdminController {
    @FXML
    public Button showAllTasksButton;
    @FXML
    public Label helpLabel;
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
    private Label usernameLabel;
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
    private Button createTaskButton;
    @FXML
    private Button updateTaskButton;
    @FXML
    private Button closeTaskButton;
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
    private TextField assignTaskTextField;
    @FXML
    private Button assignTaskButton;
    @FXML
    private Button duplicateTaskButton;
    @FXML
    private Button deleteTaskButton;
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
    private Button reportButton;
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

        createTaskButton.setOnAction(event -> {
            try {
                if (usernameTextField.getText().isEmpty() || titleTextField.getText().isEmpty() || statusTextField.getText().isEmpty() || priorityTextField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка создания задачи");
                    alert.setHeaderText(null);
                    alert.setContentText("Пожалуйста, заполните все обязательные поля.");
                    alert.showAndWait();
                }
                else {
                    if (priorityTextField.getText().matches("[1-5]")) {
                        createTask();
                    }
                    else
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка создания задачи");
                        alert.setHeaderText(null);
                        alert.setContentText("Поле приоритета может иметь только цифры от 1 до 5!");
                        alert.showAndWait();
                    }
                }
            } catch (IOException ignored) {

            }
        });
        updateTaskButton.setOnAction(event -> {
            updateTask();
        });

        reportButton.setOnAction(event -> {
            generateReport();
        });

        helpLabel.setOnMouseClicked(event -> {

            String message = "Отображение задач:\n" +
                    "\n" +
                    "В левой части интерфейса находится таблица TableView, которая отображает список задач.\n" +
                    "Колонки таблицы представляют различные атрибуты задачи, такие как наименование, статус, приоритет и назначено на (задача назначена на пользователя с указанным никнеймом).\n" +
                    "Чтобы просмотреть подробности о задаче, выберите ее в таблице.\n" +
                    "\n" +
                    "Действия с задачами:\n" +
                    "\n" +
                    "Дублирование задачи: Нажмите на кнопку \"Дублировать\", чтобы создать копию выбранной задачи.\n" +
                    "Создание новой задачи: В правой части интерфейса заполните поля \"Наименование\"(наименование задачи), \"Описание\"(описание задачи), \"Приоритет\"(приоритет может быть только от 1 до 5, соответственно от максимального до минимального), \"Статус\"(статус задачи может быть разным, все зависит от выбора администратора), \"Назначено на\"(назначить задачу на пользователя по никнейму) и \"Срок выполнения\"(срок до которого пользователь должен выполнить задачу) для создания новой задачи. Затем нажмите кнопку \"Создать задачу\".\n" +
                    "Обновление задачи: После выбора задачи в таблице, вы можете внести изменения в поля и нажать кнопку \"Обновить\", чтобы сохранить изменения.\n" +
                    "Закрытие задачи: После выбора задачи в таблице, нажмите кнопку \"Закрыть задачу\", чтобы пометить ее как закрытую(done).\n" +
                    "Удаление задачи: Выберите задачу в таблице и нажмите кнопку \"Удалить\", чтобы удалить ее.\n" +
                    "Назначить также: После выбора задачи в таблице, вы можете назначить выбранную задачу еще на другого пользователя, заполнив поле \"никнейм\", затем нажав на кнопку \"Назначить также\".\n" +
                    "Отчет: Выберите задачу в таблице и нажмите кнопку \"Отчет\", чтобы создать отчет с задачей и информацией о ней. Если не выбрать конкретную задачу и нажать на кнопку \"Отчет\", будет создан отчет со всеми задачами, их информацией и комментариями.\n" +
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
                    "- 7 дней: Введите id никнейма пользователя в поле \"id никнейма\", чтобы отфильтровать задачи по назначенному пользователю за неделю.\n" +
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

        assignTaskButton.setOnAction(event -> {
            try {
                assignTask();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        duplicateTaskButton.setOnAction(event -> {
            try {
                duplicateTask();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        deleteTaskButton.setOnAction(event -> {
            try {
                deleteTask();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        weeklyTasksButton.setOnAction(event -> searchWeeklyTasks());

        // Добавляем слушатель событий к полю searchByTitleTextField
        searchByTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        // Добавляем слушатель событий к полю searchByStatusTextField
        searchByStatusTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        // Добавляем слушатель событий к полю searchByPriorityTextField
        searchByPriorityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

        searchByUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchTasks();
        });

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
                    if(Objects.equals(searchCommentByUsernameTextField.getText(), "")) {
                        fetchComments(taskId, 0);
                    }

                    else {
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
    public void generateReport() {
        File reportsFolder = new File("reports");
        if (!reportsFolder.exists()) {
            boolean created = reportsFolder.mkdir();
            if (created) {
                System.out.println("The reports folder has been successfully created");
            } else {
                System.out.println("Error creating reports folder!");
            }
        }

        XWPFDocument document = new XWPFDocument();
        ObservableList<Task> selectedTasks = tasksTableView.getSelectionModel().getSelectedItems();

        if (selectedTasks.isEmpty()) {
            generateFullReport(document);
        } else {
            generateTaskReport(document, selectedTasks.get(0));
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream("reports/report" + System.currentTimeMillis() + ".docx")) {
            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateFullReport(XWPFDocument document) {
        try {
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Отчет со всеми задачами и комментариями");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            List<Task> tasks = tasksTableView.getItems();

            for (Task task : tasks) {
                XWPFParagraph titleTaskNumber = document.createParagraph();
                XWPFRun taskNumberRun = titleTaskNumber.createRun();
                taskNumberRun.setText("Задача №" + task.getId());
                // Task table
                XWPFTable taskTable = document.createTable(6, 2);
                taskTable.setWidth("100%");
                taskTable.setCellMargins(100, 100, 100, 100);

                // Task title
                XWPFTableRow titleRow = taskTable.getRow(0);
                titleRow.getCell(0).setText("Наименование задачи:");
                titleRow.getCell(1).setText(task.getTitle());

                // Task description
                XWPFTableRow descriptionRow = taskTable.getRow(1);
                descriptionRow.getCell(0).setText("Описание задачи:");
                String description = task.getAbout();
                String descriptionText = (description != null) ? description : "не указано";
                descriptionRow.getCell(1).setText(descriptionText);

                // Task status
                XWPFTableRow statusRow = taskTable.getRow(2);
                statusRow.getCell(0).setText("Статус задачи:");
                statusRow.getCell(1).setText(task.getStatus());

                // Task priority
                XWPFTableRow priorityRow = taskTable.getRow(3);
                priorityRow.getCell(0).setText("Приоритет задачи:");
                priorityRow.getCell(1).setText(task.getPriority());

                // Task due date
                XWPFTableRow dueDateRow = taskTable.getRow(4);
                dueDateRow.getCell(0).setText("Срок выполнения:");
                String dueDateString = task.getDue();
                LocalDateTime dueDate = LocalDateTime.parse(dueDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDueDate = formatter.format(dueDate);
                dueDateRow.getCell(1).setText(formattedDueDate);

                // Task username
                XWPFTableRow usernameRow = taskTable.getRow(5);
                usernameRow.getCell(0).setText("Назначено на пользователя:");
                usernameRow.getCell(1).setText(task.getUsername());

                // Task comments
                String endpoint = "/comments";
                endpoint += "?task=" + task.getId();
                List<Comment> comments = apiClient.getComments(endpoint);

                XWPFParagraph commentsParagraph = document.createParagraph();
                commentsParagraph.setStyle("Heading2");
                XWPFRun commentsRun = commentsParagraph.createRun();
                commentsRun.setText("Комментарии:");
                commentsRun.setBold(true);
                commentsRun.setFontSize(14);
                commentsRun.setColor("0000FF");

                for (Comment comment : comments) {
                    XWPFParagraph commentParagraph = document.createParagraph();
                    commentParagraph.setStyle("Normal");
                    XWPFRun commentRun = commentParagraph.createRun();
                    commentRun.setText(comment.getContent());
                }

                // Add space between tasks
                document.createParagraph().setSpacingAfterLines(200);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успешно");
            alert.setHeaderText(null);
            alert.setContentText("Отчет с задачами успешно создан.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка создания отчета с задачами!");
            alert.showAndWait();
        }
    }

    private void generateTaskReport(XWPFDocument document, Task task) {
        try {
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Отчет по задаче");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            XWPFParagraph titleTaskNumber = document.createParagraph();
            XWPFRun taskNumberRun = titleTaskNumber.createRun();
            taskNumberRun.setText("Задача №" + task.getId());
            // Task table
            XWPFTable taskTable = document.createTable(6, 2);
            taskTable.setWidth("100%");
            taskTable.setCellMargins(100, 100, 100, 100);

            // Task title
            XWPFTableRow titleRow = taskTable.getRow(0);
            titleRow.getCell(0).setText("Наименование задачи:");
            titleRow.getCell(1).setText(task.getTitle());

            // Task description
            XWPFTableRow descriptionRow = taskTable.getRow(1);
            descriptionRow.getCell(0).setText("Описание задачи:");
            String description = task.getAbout();
            String descriptionText = (description != null) ? description : "не указано";
            descriptionRow.getCell(1).setText(descriptionText);

            // Task status
            XWPFTableRow statusRow = taskTable.getRow(2);
            statusRow.getCell(0).setText("Статус задачи:");
            statusRow.getCell(1).setText(task.getStatus());

            // Task priority
            XWPFTableRow priorityRow = taskTable.getRow(3);
            priorityRow.getCell(0).setText("Приоритет задачи:");
            priorityRow.getCell(1).setText(task.getPriority());

            // Task due date
            XWPFTableRow dueDateRow = taskTable.getRow(4);
            dueDateRow.getCell(0).setText("Срок выполнения:");
            String dueDateString = task.getDue();
            LocalDateTime dueDate = LocalDateTime.parse(dueDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDueDate = formatter.format(dueDate);
            dueDateRow.getCell(1).setText(formattedDueDate);

            // Task username
            XWPFTableRow usernameRow = taskTable.getRow(5);
            usernameRow.getCell(0).setText("Назначено на пользователя:");
            usernameRow.getCell(1).setText(task.getUsername());

            // Task comments
            String endpoint = "/comments";
            endpoint += "?task=" + task.getId();
            List<Comment> comments = apiClient.getComments(endpoint);

            XWPFParagraph commentsParagraph = document.createParagraph();
            commentsParagraph.setStyle("Heading2");
            XWPFRun commentsRun = commentsParagraph.createRun();
            commentsRun.setText("Комментарии:");
            commentsRun.setBold(true);
            commentsRun.setFontSize(14);
            commentsRun.setColor("0000FF");

            for (Comment comment : comments) {
                XWPFParagraph commentParagraph = document.createParagraph();
                commentParagraph.setStyle("Normal");
                XWPFRun commentRun = commentParagraph.createRun();
                commentRun.setText(comment.getContent());
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успешно");
            alert.setHeaderText(null);
            alert.setContentText("Отчет с задачей успешно создан.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка создания отчета с задачей!");
            alert.showAndWait();
        }
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

    private void fetchWeeklyTasksByNicknameId(String userId) {
        try {
            // Получаем задачи на неделю для указанного пользователя с помощью API
            List<Task> tasks = apiClient.getWeeklyTasksByNicknameId(userId);
            tasksTableView.getItems().clear();
            tasksTableView.getItems().addAll(tasks);
            // Обновляем представление (например, таблицу или список) с полученными задачами
            // ...
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки получения задач из API
        }
    }

    // Метод для создания новой задачи
    public void createTask() throws IOException {
        String username = usernameTextField.getText();
        String title = titleTextField.getText();
        String about = aboutTextArea.getText();
        String status = statusTextField.getText();
        String priority = priorityTextField.getText();
        LocalDateTime dueDateTime = dueDatePicker.getValue().atTime(0, 0, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String due = dueDateTime.format(formatter);

        // Отправка запроса на создание задачи с использованием API клиента
        apiClient.createTask(username, title, about, status, priority, due);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Создано");
        alert.setHeaderText(null);
        alert.setContentText("Задача создана.");
        alert.showAndWait();
        fetchTasks();
    }

    // Метод для обновления задачи
    public void updateTask() {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();
            String username = usernameTextField.getText();
            String title = titleTextField.getText();
            String about = aboutTextArea.getText();
            String status = statusTextField.getText();
            String priority = priorityTextField.getText();
            LocalDateTime dueDateTime = dueDatePicker.getValue().atTime(0, 0, 0, 0);
            String due = dueDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

            if (username.isEmpty() || title.isEmpty() || status.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка обновления задачи");
                alert.setHeaderText(null);
                alert.setContentText("Пожалуйста, заполните все обязательные поля.");
                alert.showAndWait();
                return;
            }

            try {
                // Отправка запроса на обновление задачи с использованием API клиента
                apiClient.updateTask(taskId, username, title, about, status, priority, due);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Обновлено");
                alert.setHeaderText(null);
                alert.setContentText("Задача обновлена.");
                alert.showAndWait();
                fetchTasks();
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки обновления задачи через API
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Задача не выбрана.");
            alert.showAndWait();
        }
    }

    // Метод для удаления задачи
    public void deleteTask() throws IOException {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText(null);
            alert.setContentText("Вы действительно хотите удалить задачу?");

            ButtonType yesButton = new ButtonType("Да");
            ButtonType noButton = new ButtonType("Нет");

            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                // Отправка запроса на удаление задачи с использованием API клиента
                apiClient.deleteTask(taskId);
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Удалено");
                alert2.setHeaderText(null);
                alert2.setContentText("Задача удалена.");
                alert2.showAndWait();
                fetchTasks();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Задача не выбрана.");
            alert.showAndWait();
        }
    }

    // Метод для закрытия задачи
    public void closeTask() throws IOException {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText(null);
            alert.setContentText("Вы действительно хотите закрыть задачу?");

            ButtonType yesButton = new ButtonType("Да");
            ButtonType noButton = new ButtonType("Нет");

            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                // Отправка запроса на закрытие задачи с использованием API клиента
                apiClient.closeTask(taskId);
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Успешно закрыто");
                alert2.setHeaderText(null);
                alert2.setContentText("Задача помечена как закрытая.");
                alert2.showAndWait();
                fetchTasks();
            }
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Создано");
            alert.setHeaderText(null);
            alert.setContentText("Комментарий создан.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Задача не выбрана.");
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Обновлено");
            alert.setHeaderText(null);
            alert.setContentText("Комментарий обновлен.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Комментарий не выбран.");
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Удалено");
            alert.setHeaderText(null);
            alert.setContentText("Комментарий удален.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Комментарий не выбран.");
            alert.showAndWait();
        }
    }


    // Метод для назначения задачи
    public void assignTask() throws IOException {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();
            String assigneeUsername = assignTaskTextField.getText();

            // Отправка запроса на назначение задачи с использованием API клиента
            apiClient.assignTask(taskId, assigneeUsername);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Назначено");
            alert.setHeaderText(null);
            alert.setContentText("Задача назначена.");
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

    // Метод для дублирования задачи
    public void duplicateTask() throws IOException {
        Task selectedTask = tasksTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int taskId = selectedTask.getId();

            // Отправка запроса на дублирование задачи с использованием API клиента
            apiClient.duplicateTask(taskId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Дублировано");
            alert.setHeaderText(null);
            alert.setContentText("Задача дублирована.");
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
