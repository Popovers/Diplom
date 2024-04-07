package com.example.diplom;
import java.time.LocalDate;

public class Main {

    private int projectId;
    private int specialistId;
    private int roleId;
    private LocalDate startDate;
    private LocalDate endDate;

    public Main(int projectId, int specialistId, int roleId, LocalDate startDate, LocalDate endDate) {
        this.projectId = projectId;
        this.specialistId = specialistId;
        this.roleId = roleId;
        this.startDate = startDate;
        this.endDate = endDate;
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
}
