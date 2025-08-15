package com.example.dao;

import com.example.model.Category;
import com.example.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name FROM categories ORDER BY name")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }

    public Category insert(String name) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO categories(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new Category(keys.getInt(1), name.trim());
            }
        }
        return null;
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM categories WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
