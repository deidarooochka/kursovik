package ui;

import dao.EmployeeDAO;
import model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class EmployeePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private EmployeeDAO employeeDAO;
    private JTextField searchField;

    public EmployeePanel() {
        employeeDAO = new EmployeeDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchEmployees());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Имя", "Фамилия", "Должность", "Дата рождения", "Зарплата"};
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

        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Employee> list = employeeDAO.getAll();
            for (Employee e : list) {
                tableModel.addRow(new Object[]{e.getId(), e.getFirstName(), e.getLastName(), e.getPosition(), e.getBirthDate(), e.getSalary()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchEmployees() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Employee> list = employeeDAO.getAll();
            for (Employee e : list) {
                if (e.getFirstName().toLowerCase().contains(searchText) ||
                        e.getLastName().toLowerCase().contains(searchText) ||
                        (e.getPosition() != null && e.getPosition().toLowerCase().contains(searchText))) {
                    tableModel.addRow(new Object[]{e.getId(), e.getFirstName(), e.getLastName(), e.getPosition(), e.getBirthDate(), e.getSalary()});
                }
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Ничего не найдено");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void addEmployee() {
        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField position = new JTextField();
        JTextField birthDate = new JTextField("ГГГГ-ММ-ДД");
        JTextField salary = new JTextField();

        Object[] fields = {
                "Имя:", firstName, "Фамилия:", lastName,
                "Должность:", position, "Дата рождения:", birthDate, "Зарплата:", salary
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Добавить сотрудника", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Employee e = new Employee();
                e.setFirstName(firstName.getText());
                e.setLastName(lastName.getText());
                e.setPosition(position.getText());
                e.setBirthDate(Date.valueOf(birthDate.getText()));
                e.setSalary(new BigDecimal(salary.getText()));
                employeeDAO.add(e);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
            }
        }
    }

    private void editEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String firstName = (String) tableModel.getValueAt(row, 1);
        String lastName = (String) tableModel.getValueAt(row, 2);
        String position = (String) tableModel.getValueAt(row, 3);
        Date birthDate = (Date) tableModel.getValueAt(row, 4);
        BigDecimal salary = (BigDecimal) tableModel.getValueAt(row, 5);

        JTextField tfFirstName = new JTextField(firstName);
        JTextField tfLastName = new JTextField(lastName);
        JTextField tfPosition = new JTextField(position);
        JTextField tfBirthDate = new JTextField(birthDate.toString());
        JTextField tfSalary = new JTextField(salary.toString());

        Object[] fields = {
                "Имя:", tfFirstName, "Фамилия:", tfLastName,
                "Должность:", tfPosition, "Дата рождения:", tfBirthDate, "Зарплата:", tfSalary
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Изменить сотрудника", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Employee e = new Employee();
                e.setId(id);
                e.setFirstName(tfFirstName.getText());
                e.setLastName(tfLastName.getText());
                e.setPosition(tfPosition.getText());
                e.setBirthDate(Date.valueOf(tfBirthDate.getText()));
                e.setSalary(new BigDecimal(tfSalary.getText()));
                employeeDAO.update(e);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
            }
        }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Удалить сотрудника?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeDAO.delete(id);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }
}