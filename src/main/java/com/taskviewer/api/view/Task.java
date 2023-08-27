package com.taskviewer.api.view;

public class Task {
    private int id;
    private String username;
    private String title;
    private String about;
    private String status;
    private String priority;
    private String due;

    // Конструкторы, геттеры и сеттеры

    public Task() {
        // Пустой конструктор
    }

    public Task(String username, String title, String about, String status, String priority, String due) {
        this.username = username;
        this.title = title;
        this.about = about;
        this.status = status;
        this.priority = priority;
        this.due = due;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }
}
