package com.example.diplom;

public class ProjectRequest {
    private int id;
    private String projectName;
    private String role;
    private String startDate;
    private String endDate;
    private int roleId;
    private String roleName;

    public ProjectRequest(int id, String projectName, int roleId, String roleName, String startDate, String endDate) {
        this.id = id;
        this.projectName = projectName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.startDate = startDate;
        this.endDate = endDate;
        // Устанавливаем значение роли
        this.role = roleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public int getProjectId() {
        // Здесь должен быть ваш код для получения id проекта
        return 0; // Пример
    }

    public void setProjectId(int projectId) {
        // Здесь должен быть ваш код для установки id проекта
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Проект: " + projectName + ", Роль: " + role + ", Срок: " + startDate + " - " + endDate;
    }
}
