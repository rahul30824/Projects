package com.example.util;

import java.sql.*;
import java.io.File;

public class DB {
    // Keep DB file inside project/db for a predictable path
    private static final String DB_DIR = "db";
    private static final String URL = "jdbc:sqlite:" + DB_DIR + "/expenses.db";

    static {
        File dir = new File(DB_DIR);
        if (!dir.exists()) dir.mkdirs();
        init();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void init() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA synchronous = NORMAL");

            st.execute(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "  id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT UNIQUE NOT NULL" +
                ")"
            );

            st.execute(
                "CREATE TABLE IF NOT EXISTS expenses (" +
                "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  date        TEXT NOT NULL," +
                "  amount      REAL NOT NULL," +
                "  category_id INTEGER," +
                "  note        TEXT," +
                "  FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE SET NULL" +
                ")"
            );

            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_categories_name ON categories(name)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_expenses_date ON expenses(date)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_expenses_category_id ON expenses(category_id)");

            // View avoids 'ambiguous column name: id' in joins
            st.execute(
                "CREATE VIEW IF NOT EXISTS v_expenses_with_category AS " +
                "SELECT e.id AS expense_id, e.date AS date, e.amount AS amount, e.note AS note, " +
                "       c.id AS category_id, c.name AS category_name " +
                "FROM expenses e LEFT JOIN categories c ON e.category_id = c.id"
            );

            // Seed defaults
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM categories")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String[] defaults = {
                        "Food","Transport","Rent","Utilities",
                        "Entertainment","Health","Shopping","Other"
                    };
                    try (PreparedStatement ps =
                             conn.prepareStatement("INSERT INTO categories(name) VALUES (?)")) {
                        for (String d : defaults) {
                            ps.setString(1, d);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
