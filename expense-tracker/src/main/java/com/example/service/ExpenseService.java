package com.example.service;

import com.example.dao.CategoryDAO;
import com.example.dao.ExpenseDAO;
import com.example.model.Category;
import com.example.model.Expense;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ExpenseService {
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    public List<Category> categories() throws SQLException { return categoryDAO.findAll(); }
    public Category addCategory(String name) throws SQLException { return categoryDAO.insert(name); }
    public void deleteCategory(int id) throws SQLException { categoryDAO.delete(id); }

    public Expense addExpense(LocalDate date, double amount, Integer categoryId, String note) throws SQLException {
        return expenseDAO.insert(new Expense(null, date, amount, categoryId, null, note));
    }
    public List<Expense> findExpenses(LocalDate from, LocalDate to, Integer categoryId, String keyword) throws SQLException {
        return expenseDAO.find(from, to, categoryId, keyword);
    }
    public double total(LocalDate from, LocalDate to, Integer categoryId, String keyword) throws SQLException {
        return expenseDAO.total(from, to, categoryId, keyword);
    }
    public void deleteExpense(int id) throws SQLException { expenseDAO.delete(id); }
}
