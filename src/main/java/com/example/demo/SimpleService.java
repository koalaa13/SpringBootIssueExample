package com.example.demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

@Service
public class SimpleService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;

    @PostConstruct
    public void createTable() {
        final String sql = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(255), company " +
                "VARCHAR(255))";
        jdbcTemplate.execute(sql);
    }

    public void add(String name, String company) {
        final String sql = "INSERT INTO users (name, company) VALUES (?, ?)";
        jdbcTemplate.update(sql, name, company);
    }

    public List<UserDao> findByName(String name) {
        final String sql = "SELECT * FROM users WHERE name = ?";
        return jdbcTemplate.query(sql, new RowMapper<UserDao>() {
            @Override
            public UserDao mapRow(ResultSet rs, int rowNum) throws SQLException {
                var dao = new UserDao();
                dao.id = rs.getLong("id");
                dao.name = rs.getString("name");
                dao.company = rs.getString("company");
                return dao;
            }
        }, name);
    }

    public void incorrectBulkUpdate(Map<String, List<String>> info) {
        final String sql = "UPDATE users SET company = :company WHERE name IN (:names)";
        SqlParameterSource[] params = info.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e1.getValue().size(), e2.getValue().size()))
                .map(e -> new MapSqlParameterSource()
                        .addValue("company", e.getKey())
                        .addValue("names", e.getValue())
                )
                .toArray(SqlParameterSource[]::new);
        jdbcOperations.batchUpdate(sql, params);
    }

    public void correctBulkUpdate(Map<String, List<String>> info) {
        final String sql = "UPDATE users SET company = :company WHERE name IN (:names)";
        SqlParameterSource[] params = info.entrySet().stream()
                .sorted((e1, e2) -> -Integer.compare(e1.getValue().size(), e2.getValue().size()))
                .map(e -> new MapSqlParameterSource()
                        .addValue("company", e.getKey())
                        .addValue("names", e.getValue())
                )
                .toArray(SqlParameterSource[]::new);
        jdbcOperations.batchUpdate(sql, params);
    }
}
