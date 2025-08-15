package com.example.ui;

import com.example.model.Category;
import com.example.service.ExpenseService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesPanel extends JPanel {
    private final ExpenseService service;
    private final CategoryTableModel tableModel = new CategoryTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField = new JTextField(15);

    public ManageCategoriesPanel(ExpenseService service) {
        this.service = service;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("New category:"));
        top.add(nameField);
        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete selected");
        top.add(addBtn);
        top.add(deleteBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        addBtn.addActionListener(e -> addCategory());
        deleteBtn.addActionListener(e -> deleteSelected());
    }

    private void loadData() {
        try {
            List<Category> cats = service.categories();
            tableModel.setData(cats);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addCategory() {
        try {
            String name = nameField.getText().trim();
            if (name.isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter a name");
                return;
            }
            service.addCategory(name);
            nameField.setText("");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Category c = tableModel.get(row);
        int ok = JOptionPane.showConfirmDialog(this, "Delete category '" + c.getName() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                service.deleteCategory(c.getId());
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class CategoryTableModel extends AbstractTableModel {
        private final String[] cols = {"ID","Name"};
        private List<Category> data = new ArrayList<>();

        public void setData(List<Category> data) { this.data = data; fireTableDataChanged(); }
        public Category get(int row) { return data.get(row); }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Category c = data.get(rowIndex);
            return columnIndex == 0 ? c.getId() : c.getName();
        }
    }
}
