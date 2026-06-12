package ui;

import dao.AuthorDAO;
import model.Author;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AuthorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AuthorDAO authorDAO;
    private JTextField searchField;

    public AuthorPanel() {
        authorDAO = new AuthorDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchAuthors());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Имя", "Фамилия"};
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

        addButton.addActionListener(e -> addAuthor());
        editButton.addActionListener(e -> editAuthor());
        deleteButton.addActionListener(e -> deleteAuthor());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Author> authors = authorDAO.getAll();
            for (Author a : authors) {
                tableModel.addRow(new Object[]{a.getId(), a.getFirstName(), a.getLastName()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchAuthors() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Author> authors = authorDAO.getAll();
            for (Author a : authors) {
                if (a.getFirstName().toLowerCase().contains(searchText) ||
                        a.getLastName().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{a.getId(), a.getFirstName(), a.getLastName()});
                }
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Ничего не найдено");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void addAuthor() {
        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        Object[] fields = {"Имя:", firstName, "Фамилия:", lastName};

        int result = JOptionPane.showConfirmDialog(this, fields, "Добавить автора", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Author author = new Author(firstName.getText(), lastName.getText());
                authorDAO.add(author);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }

    private void editAuthor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String firstName = (String) tableModel.getValueAt(row, 1);
        String lastName = (String) tableModel.getValueAt(row, 2);

        JTextField tfFirstName = new JTextField(firstName);
        JTextField tfLastName = new JTextField(lastName);
        Object[] fields = {"Имя:", tfFirstName, "Фамилия:", tfLastName};

        int result = JOptionPane.showConfirmDialog(this, fields, "Изменить автора", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Author author = new Author(tfFirstName.getText(), tfLastName.getText());
                author.setId(id);
                authorDAO.update(author);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }

    private void deleteAuthor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Удалить?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                authorDAO.delete(id);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }
}