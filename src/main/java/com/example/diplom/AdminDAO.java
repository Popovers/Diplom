//package com.example.diplom;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//@Repository
//public class AdminDao {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private static final class AdminMapper implements RowMapper<Admin> {
//        @Override
//        public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
//            Admin admin = new Admin();
//            admin.setId(rs.getLong("id"));
//            admin.setName(rs.getString("name"));
//            admin.setEmail(rs.getString("email"));
//            return admin;
//        }
//    }
//
//    public List<Admin> findAll() {
//        return jdbcTemplate.query("SELECT * FROM admins", new AdminMapper());
//    }
//
//    public Admin findById(Long id) {
//        return jdbcTemplate.queryForObject("SELECT * FROM admins WHERE id = ?", new Object[]{id}, new AdminMapper());
//    }
//
//    public int save(Admin admin) {
//        return jdbcTemplate.update("INSERT INTO admins (name, email) VALUES (?, ?)",
//                admin.getName(), admin.getEmail());
//    }
//
//    public int update(Admin admin) {
//        return jdbcTemplate.update("UPDATE admins SET name = ?, email = ? WHERE id = ?",
//                admin.getName(), admin.getEmail(), admin.getId());
//    }
//
//    public int deleteById(Long id) {
//        return jdbcTemplate.update("DELETE FROM admins WHERE id = ?", id);
//    }
//}
