package com.example.diplom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean authenticate(String username, String password) {
        System.out.println("Attempting to authenticate user: " + username);

        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

        try {
            int count = jdbcTemplate.queryForObject(query, Integer.class, username, password);
            System.out.println("Authentication successful for user: " + username);
            return count == 1;
        } catch (Exception e) {
            System.out.println("Error during authentication for user: " + username);
            e.printStackTrace();
            return false;
        }
    }
}
