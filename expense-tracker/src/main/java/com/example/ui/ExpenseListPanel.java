package com.example.ui;

import com.example.model.Category;
import com.example.model.Expense;
import com.example.service.ExpenseService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseListPanel extends JPanel {
    private final ExpenseService service;
    private final JTextField fromField = new JTextField(10);
    private final JTextField toField = new JTextField(10);
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JTextField keywordField = new JTextField(12);
    private final JLabel totalLabel = new JLabel("Total: 0.00");
    private final ExpenseTableModel tableModel = new ExpenseTableModel();
    private final JTable table = new JTable(tableModel);

    public ExpenseListPanel(ExpenseService service) {
        this.service = service;
        setLayout(new BorderLayout());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("From:"));
        filters.add(fromField);
        filters.add(new JLabel("To:"));
        filters.add(toField);
        filters.add(new JLabel("Category:"));
        filters.add(categoryBox);
        filters.add(new JLabel("Keyword:"));
        filters.add(keywordField);
        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Reset");
        filters.add(searchBtn);
        filters.add(resetBtn);
        filters.add(totalLabel);

        add(filters, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        fromField.setText(LocalDate.now().withDayOfMonth(1).toString());
        toField.setText(LocalDate.now().toString());

        reloadCategories();
        loadData();

        searchBtn.addActionListener(e -> loadData());
        resetBtn.addActionListener(e -> { 
            fromField.setText("");
            toField.setText("");
            keywordField.setText("");
            categoryBox.setSelectedIndex(-1);
            loadData();
        });

        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete selected");
        popup.add(deleteItem);
        deleteItem.addActionListener(e -> deleteSelected());
        table.setComponentPopupMenu(popup);
    }

    private void reloadCategories() {
        try {
            categoryBox.removeAllItems();
            List<Category> cats = service.categories();
            categoryBox.addItem(new Category(null, "All"));
            for (Category c : cats) categoryBox.addItem(c);
            categoryBox.setSelectedIndex(0);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadData() {
        try {
            LocalDate from = parseDate(fromField.getText());
            LocalDate to = parseDate(toField.getText());
            Category selected = (Category) categoryBox.getSelectedItem();
            Integer catId = (selected != null && selected.getId()!=null) ? selected.getId() : null;
            String keyword = keywordField.getText().trim();
            List<Expense> data = service.findExpenses(from, to, catId, keyword);
            tableModel.setData(data);
            double total = service.total(from, to, catId, keyword);
            totalLabel.setText(String.format("Total: %.2f", total));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Expense e = tableModel.get(row);
        int ok = JOptionPane.showConfirmDialog(this, "Delete expense #" + e.getId() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                service.deleteExpense(e.getId());
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;
        return LocalDate.parse(text.trim());
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class ExpenseTableModel extends AbstractTableModel {
        private final String[] cols = {"ID","Date","Amount","Category","Note"};
        private List<Expense> data = new ArrayList<>();

        public void setData(List<Expense> data) { this.data = data; fireTableDataChanged(); }
        public Expense get(int row) { return data.get(row); }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Expense e = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> e.getId();
                case 1 -> e.getDate();
                case 2 -> e.getAmount();
                case 3 -> e.getCategoryName();
                case 4 -> e.getNote();
                default -> "";
            };
        }
    }
}
