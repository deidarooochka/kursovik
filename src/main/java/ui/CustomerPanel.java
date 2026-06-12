package ui;

import dao.CustomerDAO;
import model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private CustomerDAO customerDAO;
    private JTextField searchField;

    public CustomerPanel() {
        customerDAO = new CustomerDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchCustomers());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Имя", "Фамилия", "Телефон", "Дата рождения"};
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

        addButton.addActionListener(e -> addCustomer());
        editButton.addActionListener(e -> editCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Customer> list = customerDAO.getAll();
            for (Customer c : list) {
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getPhone(),
                        c.getBirthDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchCustomers() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        String searchDigitsOnly = searchText.replaceAll("\\D", "");
        boolean hasDigits = !searchDigitsOnly.isEmpty();

        tableModel.setRowCount(0);
        try {
            List<Customer> list = customerDAO.getAll();
            for (Customer c : list) {
                String firstName = c.getFirstName() != null ? c.getFirstName().toLowerCase() : "";
                String lastName = c.getLastName() != null ? c.getLastName().toLowerCase() : "";

                boolean matchesName = firstName.contains(searchText) || lastName.contains(searchText);

                boolean matchesPhone = false;
                if (hasDigits && c.getPhone() != null && !c.getPhone().isEmpty()) {
                    String phoneDigitsOnly = c.getPhone().replaceAll("\\D", "");
                    matchesPhone = phoneDigitsOnly.contains(searchDigitsOnly);
                }

                if (matchesName || matchesPhone) {
                    tableModel.addRow(new Object[]{
                            c.getId(), c.getFirstName(), c.getLastName(), c.getPhone(), c.getBirthDate()
                    });
                }
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Ничего не найдено");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void addCustomer() {
        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField phone = new JTextField();
        JTextField birthDate = new JTextField("ГГГГ-ММ-ДД");

        Object[] fields = {
                "Имя:", firstName,
                "Фамилия:", lastName,
                "Телефон:", phone,
                "Дата рождения (ГГГГ-ММ-ДД):", birthDate
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Добавить покупателя", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer c = new Customer();
                c.setFirstName(firstName.getText());
                c.setLastName(lastName.getText());
                c.setPhone(phone.getText());

                String birthDateStr = birthDate.getText();
                if (birthDateStr != null && !birthDateStr.isEmpty() && !birthDateStr.equals("ГГГГ-ММ-ДД")) {
                    c.setBirthDate(Date.valueOf(birthDateStr));
                }

                customerDAO.add(c);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Покупатель добавлен!");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: неверный формат даты. Используйте ГГГГ-ММ-ДД");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }

    private void editCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите покупателя");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String firstName = (String) tableModel.getValueAt(row, 1);
        String lastName = (String) tableModel.getValueAt(row, 2);
        String phone = (String) tableModel.getValueAt(row, 3);
        Date birthDate = (Date) tableModel.getValueAt(row, 4);

        JTextField tfFirstName = new JTextField(firstName);
        JTextField tfLastName = new JTextField(lastName);
        JTextField tfPhone = new JTextField(phone != null ? phone : "");

        JTextField tfBirthDate = new JTextField();
        if (birthDate != null) {
            tfBirthDate.setText(birthDate.toString());
        } else {
            tfBirthDate.setText("");
        }

        Object[] fields = {
                "Имя:", tfFirstName,
                "Фамилия:", tfLastName,
                "Телефон:", tfPhone,
                "Дата рождения (ГГГГ-ММ-ДД):", tfBirthDate
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Изменить покупателя", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer c = new Customer();
                c.setId(id);
                c.setFirstName(tfFirstName.getText());
                c.setLastName(tfLastName.getText());
                c.setPhone(tfPhone.getText());

                String birthDateStr = tfBirthDate.getText();
                if (birthDateStr != null && !birthDateStr.isEmpty()) {
                    c.setBirthDate(Date.valueOf(birthDateStr));
                } else {
                    c.setBirthDate(null);
                }

                customerDAO.update(c);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Данные обновлены!");

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: неверный формат даты. Используйте ГГГГ-ММ-ДД");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите покупателя");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1) + " " + (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить покупателя \"" + name + "\"?\n(Если у него есть продажи, удалить не получится)",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerDAO.delete(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Покупатель удалён");
            } catch (SQLException e) {
                if (e.getMessage().contains("REFERENTIAL")) {
                    JOptionPane.showMessageDialog(this, "Нельзя удалить покупателя с продажами!\nСначала удалите его продажи.");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
                }
            }
        }
    }
}