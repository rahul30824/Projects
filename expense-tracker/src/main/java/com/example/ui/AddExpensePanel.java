package com.example.ui;

import com.example.model.Category;
import com.example.service.ExpenseService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AddExpensePanel extends JPanel {
    private final ExpenseService service;
    private final JTextField dateField = new JTextField(10);
    private final JTextField amountField = new JTextField(10);
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JTextField noteField = new JTextField(30);

    public AddExpensePanel(ExpenseService service) {
        this.service = service;
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y=0;

        gc.gridx=0; gc.gridy=y; add(new JLabel("Date (YYYY-MM-DD):"), gc);
        gc.gridx=1; gc.gridy=y++; add(dateField, gc);

        gc.gridx=0; gc.gridy=y; add(new JLabel("Amount:"), gc);
        gc.gridx=1; gc.gridy=y++; add(amountField, gc);

        gc.gridx=0; gc.gridy=y; add(new JLabel("Category:"), gc);
        gc.gridx=1; gc.gridy=y++; add(categoryBox, gc);

        gc.gridx=0; gc.gridy=y; add(new JLabel("Note:"), gc);
        gc.gridx=1; gc.gridy=y++; add(noteField, gc);

        JButton saveBtn = new JButton("Save Expense");
        gc.gridx=1; gc.gridy=y++; add(saveBtn, gc);

        dateField.setText(LocalDate.now().toString());
        reloadCategories();

        saveBtn.addActionListener(e -> saveExpense());
    }

    private void reloadCategories() {
        try {
            categoryBox.removeAllItems();
            List<Category> cats = service.categories();
            for (Category c : cats) categoryBox.addItem(c);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveExpense() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            double amount = Double.parseDouble(amountField.getText().trim());
            Category cat = (Category) categoryBox.getSelectedItem();
            Integer catId = (cat != null) ? cat.getId() : null;
            String note = noteField.getText().trim();
            service.addExpense(date, amount, catId, note);
            JOptionPane.showMessageDialog(this, "Saved!");
            amountField.setText("");
            noteField.setText("");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
