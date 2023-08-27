package com.taskviewer.api.view;

public class Comment {
    private int id;
    private String content;
    private int taskId;

    public Comment(int id, String content, int taskId) {
        this.id = id;
        this.content = content;
        this.taskId = taskId;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getTaskId() {
        return taskId;
    }

    // Другие геттеры и сеттеры, если необходимо

    @Override
    public String toString() {
        return content;
    }
}
