package com.example.diplom;

import java.time.LocalDate;

public class ProjectSp {
    private int projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int hours; // Добавлено поле hours

    // Конструктор
    public ProjectSp(int projectId, String projectName, LocalDate startDate, LocalDate endDate, int hours) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hours = hours; // Инициализируется поле hours
    }

    // Геттеры и сеттеры
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}