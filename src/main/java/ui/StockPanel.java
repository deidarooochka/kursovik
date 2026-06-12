package ui;

import dao.StockDAO;
import dao.BookDAO;
import model.Stock;
import model.Book;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StockPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private StockDAO stockDAO;
    private BookDAO bookDAO;
    private JTextField searchField;

    public StockPanel() {
        stockDAO = new StockDAO();
        bookDAO = new BookDAO();
        setLayout(new BorderLayout());


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchStocks());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Книга", "Количество на складе"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Обновить");

        refreshButton.addActionListener(e -> refreshTable());

        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Stock> stocks = stockDAO.getAll();
            for (Stock s : stocks) {
                tableModel.addRow(new Object[]{s.getId(), s.getBookTitle(), s.getQuantity()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchStocks() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Stock> stocks = stockDAO.getAll();
            for (Stock s : stocks) {
                if (s.getBookTitle() != null && s.getBookTitle().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{s.getId(), s.getBookTitle(), s.getQuantity()});
                }
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Ничего не найдено");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void addToStock() {
        try {
            List<Book> books = bookDAO.getAll();
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Сначала добавьте книги!");
                return;
            }

            JComboBox<Book> bookBox = new JComboBox<>(books.toArray(new Book[0]));
            JTextField quantity = new JTextField();

            Object[] fields = {"Книга:", bookBox, "Количество:", quantity};

            int result = JOptionPane.showConfirmDialog(this, fields, "Добавить на склад", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Book selectedBook = (Book) bookBox.getSelectedItem();
                int qty = Integer.parseInt(quantity.getText());

                int currentQty = stockDAO.getQuantity(selectedBook.getId());
                Stock stock = new Stock(selectedBook.getId(), currentQty + qty);
                stockDAO.addOrUpdate(stock);
                refreshTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }
}