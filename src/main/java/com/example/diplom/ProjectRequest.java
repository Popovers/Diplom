package com.example.diplom;

public class ProjectRequest {
    private int id;
    private int projectId; // Добавлено поле для идентификатора проекта
    private int specialistId;
    private int roleId;
    private String startDate;
    private String endDate;

    public ProjectRequest(int id, int projectId, int specialistId, int roleId, String startDate, String endDate) {
        this.id = id;
        this.projectId = projectId;
        this.specialistId = specialistId;
        this.roleId = roleId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(int specialistId) {
        this.specialistId = specialistId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
