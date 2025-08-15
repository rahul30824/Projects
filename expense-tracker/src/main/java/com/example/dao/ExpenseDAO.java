package com.example.dao;

import com.example.model.Expense;
import com.example.util.DB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public Expense insert(Expense e) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO expenses(date, amount, category_id, note) VALUES (?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getDate().toString());
            ps.setDouble(2, e.getAmount());
            if (e.getCategoryId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, e.getCategoryId());
            ps.setString(4, e.getNote());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
        }
        return e;
    }

    public List<Expense> find(LocalDate from, LocalDate to, Integer categoryId, String keyword) throws SQLException {
        List<Expense> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT e.id AS expense_id, e.date, e.amount, e.category_id, " +
            "       c.name AS category_name, e.note " +
            "FROM expenses e LEFT JOIN categories c ON e.category_id = c.id " +
            "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (from != null) { sql.append(" AND e.date >= ?"); params.add(from.toString()); }
        if (to != null)   { sql.append(" AND e.date <= ?"); params.add(to.toString()); }
        if (categoryId != null) { sql.append(" AND e.category_id = ?"); params.add(categoryId); }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (e.note LIKE ? OR c.name LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw);
        }
        sql.append(" ORDER BY e.date DESC, e.id DESC");

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expense e = new Expense(
                        rs.getInt("expense_id"),                    // <-- use alias
                        LocalDate.parse(rs.getString("date")),
                        rs.getDouble("amount"),
                        (Integer) rs.getObject("category_id"),      // nullable
                        rs.getString("category_name"),
                        rs.getString("note")
                    );
                    list.add(e);
                }
            }
        }
        return list;
    }

    public double total(LocalDate from, LocalDate to, Integer categoryId, String keyword) throws SQLException {
        // single table -> no ambiguity, but we still qualify for clarity
        StringBuilder sql = new StringBuilder("SELECT SUM(amount) FROM expenses WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (from != null) { sql.append(" AND date >= ?"); params.add(from.toString()); }
        if (to != null)   { sql.append(" AND date <= ?"); params.add(to.toString()); }
        if (categoryId != null) { sql.append(" AND category_id = ?"); params.add(categoryId); }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND note LIKE ?");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
        }

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM expenses WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
