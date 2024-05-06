package com.example.diplom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Component
public class MainDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MainDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<String> getProjects() {
        String query = "SELECT name FROM projects";
        return jdbcTemplate.queryForList(query, String.class);
    }

    public List<String> getRequests() {
        String query = "SELECT * FROM project_specialists";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            int projectId = rs.getInt("project_id");
            int specialistId = rs.getInt("specialist_id");
            int roleId = rs.getInt("role_id");
            Date startDate = rs.getDate("start_date");
            Date endDate = rs.getDate("end_date");
            return "Проект: " + projectId + ", Специалист: " + specialistId + ", Роль: " + roleId +
                    ", Начало: " + startDate + ", Окончание: " + endDate;
        });
    }
}
