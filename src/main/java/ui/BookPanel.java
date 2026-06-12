package ui;

import dao.BookDAO;
import dao.AuthorDAO;
import dao.PublisherDAO;
import dao.CategoryDAO;
import model.Book;
import model.Author;
import model.Publisher;
import model.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class BookPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private PublisherDAO publisherDAO;
    private CategoryDAO categoryDAO;
    private JTextField searchField;

    public BookPanel() {
        bookDAO = new BookDAO();
        authorDAO = new AuthorDAO();
        publisherDAO = new PublisherDAO();
        categoryDAO = new CategoryDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchBooks());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Название", "Цена", "Автор", "Издательство", "Категория"};
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

        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Book> books = bookDAO.getAll();
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                        b.getId(), b.getTitle(), b.getPrice(),
                        b.getAuthorName(), b.getPublisherName(), b.getCategoryName()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchBooks() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Book> books = bookDAO.getAll();
            for (Book b : books) {
                if (b.getTitle().toLowerCase().contains(searchText) ||
                        (b.getAuthorName() != null && b.getAuthorName().toLowerCase().contains(searchText))) {
                    tableModel.addRow(new Object[]{
                            b.getId(), b.getTitle(), b.getPrice(),
                            b.getAuthorName(), b.getPublisherName(), b.getCategoryName()
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

    private void addBook() {
        try {
            List<Author> authors = authorDAO.getAll();
            List<Publisher> publishers = publisherDAO.getAll();
            List<Category> categories = categoryDAO.getAll();

            if (authors.isEmpty() || publishers.isEmpty() || categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Сначала добавьте авторов, издательства и категории!");
                return;
            }

            JComboBox<Author> authorBox = new JComboBox<>(authors.toArray(new Author[0]));
            JComboBox<Publisher> publisherBox = new JComboBox<>(publishers.toArray(new Publisher[0]));
            JComboBox<Category> categoryBox = new JComboBox<>(categories.toArray(new Category[0]));

            JTextField title = new JTextField();
            JTextField price = new JTextField();

            Object[] fields = {
                    "Название:", title, "Цена:", price,
                    "Автор:", authorBox, "Издательство:", publisherBox, "Категория:", categoryBox
            };

            int result = JOptionPane.showConfirmDialog(this, fields, "Добавить книгу", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Book book = new Book();
                book.setTitle(title.getText());
                book.setPrice(new BigDecimal(price.getText()));
                book.setAuthorId(((Author) authorBox.getSelectedItem()).getId());
                book.setPublisherId(((Publisher) publisherBox.getSelectedItem()).getId());
                book.setCategoryId(((Category) categoryBox.getSelectedItem()).getId());

                bookDAO.add(book);
                refreshTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void editBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите книгу");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Book book = bookDAO.getAll().stream().filter(b -> b.getId() == id).findFirst().orElse(null);
            if (book == null) return;

            List<Author> authors = authorDAO.getAll();
            List<Publisher> publishers = publisherDAO.getAll();
            List<Category> categories = categoryDAO.getAll();

            JComboBox<Author> authorBox = new JComboBox<>(authors.toArray(new Author[0]));
            JComboBox<Publisher> publisherBox = new JComboBox<>(publishers.toArray(new Publisher[0]));
            JComboBox<Category> categoryBox = new JComboBox<>(categories.toArray(new Category[0]));

            for (int i = 0; i < authors.size(); i++) {
                if (authors.get(i).getId() == book.getAuthorId()) authorBox.setSelectedIndex(i);
            }
            for (int i = 0; i < publishers.size(); i++) {
                if (publishers.get(i).getId() == book.getPublisherId()) publisherBox.setSelectedIndex(i);
            }
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == book.getCategoryId()) categoryBox.setSelectedIndex(i);
            }

            JTextField title = new JTextField(book.getTitle());
            JTextField price = new JTextField(book.getPrice().toString());

            Object[] fields = {
                    "Название:", title, "Цена:", price,
                    "Автор:", authorBox, "Издательство:", publisherBox, "Категория:", categoryBox
            };

            int result = JOptionPane.showConfirmDialog(this, fields, "Изменить книгу", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                book.setTitle(title.getText());
                book.setPrice(new BigDecimal(price.getText()));
                book.setAuthorId(((Author) authorBox.getSelectedItem()).getId());
                book.setPublisherId(((Publisher) publisherBox.getSelectedItem()).getId());
                book.setCategoryId(((Category) categoryBox.getSelectedItem()).getId());

                bookDAO.update(book);
                refreshTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите книгу");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Удалить книгу?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookDAO.delete(id);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }
}