package com.example.model;

import java.time.LocalDate;

public class Expense {
    private Integer id;
    private LocalDate date;
    private double amount;
    private Integer categoryId;
    private String categoryName;
    private String note;

    public Expense() { }

    public Expense(Integer id, LocalDate date, double amount, Integer categoryId, String categoryName, String note) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.note = note;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
