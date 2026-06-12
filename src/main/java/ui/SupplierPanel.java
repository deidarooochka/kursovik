package ui;

import dao.SupplierDAO;
import model.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SupplierPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private SupplierDAO supplierDAO;
    private JTextField searchField;

    public SupplierPanel() {
        supplierDAO = new SupplierDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchSuppliers());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Название", "Телефон", "Адрес"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Изменить");
        JButton deleteButton = new JButton("Удалить");

        addButton.addActionListener(e -> addSupplier());
        editButton.addActionListener(e -> editSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Supplier> list = supplierDAO.getAll();
            for (Supplier s : list) {
                tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getPhone(), s.getAddress()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchSuppliers() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Supplier> list = supplierDAO.getAll();
            for (Supplier s : list) {
                if (s.getName().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getPhone(), s.getAddress()});
                }
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Ничего не найдено");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void addSupplier() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField address = new JTextField();
        Object[] fields = {"Название:", name, "Телефон:", phone, "Адрес:", address};

        int result = JOptionPane.showConfirmDialog(this, fields, "Добавить поставщика", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Supplier s = new Supplier(name.getText(), phone.getText(), address.getText());
                supplierDAO.add(s);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }

    private void editSupplier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String phone = (String) tableModel.getValueAt(row, 2);
        String address = (String) tableModel.getValueAt(row, 3);

        JTextField tfName = new JTextField(name);
        JTextField tfPhone = new JTextField(phone);
        JTextField tfAddress = new JTextField(address);
        Object[] fields = {"Название:", tfName, "Телефон:", tfPhone, "Адрес:", tfAddress};

        int result = JOptionPane.showConfirmDialog(this, fields, "Изменить поставщика", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Supplier s = new Supplier(tfName.getText(), tfPhone.getText(), tfAddress.getText());
                s.setId(id);
                supplierDAO.update(s);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Удалить поставщика?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                supplierDAO.delete(id);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }
}