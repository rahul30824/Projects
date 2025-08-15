package com.example.ui;

import com.example.service.ExpenseService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final ExpenseService service = new ExpenseService();

    public MainFrame() {
        super("Personal Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 640);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Expense", new AddExpensePanel(service));
        tabs.addTab("Expenses", new ExpenseListPanel(service));
        tabs.addTab("Categories", new ManageCategoriesPanel(service));

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
