package ui;

import dao.SupplyDAO;
import dao.SupplierDAO;
import dao.BookDAO;
import dao.StockDAO;
import model.Supply;
import model.SupplyItem;
import model.Supplier;
import model.Book;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplyPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private SupplyDAO supplyDAO;
    private SupplierDAO supplierDAO;
    private BookDAO bookDAO;
    private StockDAO stockDAO;
    private JTextField searchField;

    public SupplyPanel() {
        supplyDAO = new SupplyDAO();
        supplierDAO = new SupplierDAO();
        bookDAO = new BookDAO();
        stockDAO = new StockDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchSupplies());
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Дата", "Поставщик", "Книга", "Количество", "Цена закупки", "Сумма поставки"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Оформить новую поставку");
        JButton deleteButton = new JButton("Удалить");

        addButton.addActionListener(e -> showSupplyDialog());
        deleteButton.addActionListener(e -> deleteSupply());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<SupplyItem> items = supplyDAO.getAllSupplyItems();
            for (SupplyItem item : items) {
                tableModel.addRow(new Object[]{
                        item.getId(),
                        item.getSupplyDate(),
                        item.getSupplierName(),
                        item.getBookTitle(),
                        item.getQuantity(),
                        item.getCostPrice(),
                        item.getTotalCost()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void searchSupplies() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<SupplyItem> items = supplyDAO.getAllSupplyItems();
            for (SupplyItem item : items) {
                if ((item.getSupplierName() != null && item.getSupplierName().toLowerCase().contains(searchText)) ||
                        (item.getBookTitle() != null && item.getBookTitle().toLowerCase().contains(searchText))) {
                    tableModel.addRow(new Object[]{
                            item.getId(),
                            item.getSupplyDate(),
                            item.getSupplierName(),
                            item.getBookTitle(),
                            item.getQuantity(),
                            item.getCostPrice(),
                            item.getTotalCost()
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

    private class CartItem {
        private int bookId;
        private String bookTitle;
        private int quantity;
        private BigDecimal costPrice;

        public CartItem(int bookId, String bookTitle, int quantity, BigDecimal costPrice) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.quantity = quantity;
            this.costPrice = costPrice;
        }

        public int getBookId() { return bookId; }
        public String getBookTitle() { return bookTitle; }
        public int getQuantity() { return quantity; }
        public BigDecimal getCostPrice() { return costPrice; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getTotal() { return costPrice.multiply(BigDecimal.valueOf(quantity)); }
    }

    private void showSupplyDialog() {
        try {
            List<Supplier> suppliers = supplierDAO.getAll();
            List<Book> books = bookDAO.getAll();

            if (suppliers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Сначала добавьте поставщиков!");
                return;
            }
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Сначала добавьте книги!");
                return;
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавление поставки", true);
            dialog.setSize(700, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            topPanel.add(new JLabel("Поставщик:"));
            JComboBox<Supplier> supplierBox = new JComboBox<>(suppliers.toArray(new Supplier[0]));
            supplierBox.setPreferredSize(new Dimension(200, 25));
            topPanel.add(supplierBox);
            dialog.add(topPanel, BorderLayout.NORTH);

            String[] cartColumns = {"ID", "Книга", "Количество", "Цена закупки", "Сумма"};
            DefaultTableModel cartModel = new DefaultTableModel(cartColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable cartTable = new JTable(cartModel);
            JScrollPane cartScroll = new JScrollPane(cartTable);
            cartScroll.setBorder(BorderFactory.createTitledBorder("Товары в поставке"));

            JPanel addPanel = new JPanel();
            addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
            addPanel.setBorder(BorderFactory.createTitledBorder("Добавить товар"));

            JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JComboBox<Book> bookBox = new JComboBox<>(books.toArray(new Book[0]));
            JTextField quantityField = new JTextField(5);
            JTextField costPriceField = new JTextField(8);
            JButton addButton = new JButton("Добавить в поставку");

            fieldsPanel.add(new JLabel("Книга:"));
            fieldsPanel.add(bookBox);
            fieldsPanel.add(new JLabel("Кол-во:"));
            fieldsPanel.add(quantityField);
            fieldsPanel.add(new JLabel("Цена закупки:"));
            fieldsPanel.add(costPriceField);

            JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel2.add(addButton);

            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JLabel totalLabel = new JLabel("Итого по поставке: 0.00 руб.");
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
            totalPanel.add(totalLabel);

            addPanel.add(fieldsPanel);
            addPanel.add(buttonPanel2);
            addPanel.add(totalPanel);

            List<CartItem> cart = new ArrayList<>();

            Runnable updateTotal = () -> {
                BigDecimal total = BigDecimal.ZERO;
                for (CartItem item : cart) {
                    total = total.add(item.getTotal());
                }
                totalLabel.setText(String.format("Итого по поставке: %.2f руб.", total));
            };

            Runnable updateCartTable = () -> {
                cartModel.setRowCount(0);
                for (CartItem item : cart) {
                    cartModel.addRow(new Object[]{
                            item.getBookId(),
                            item.getBookTitle(),
                            item.getQuantity(),
                            item.getCostPrice(),
                            item.getTotal()
                    });
                }
            };

            addButton.addActionListener(e -> {
                try {
                    Book selectedBook = (Book) bookBox.getSelectedItem();
                    int qty = Integer.parseInt(quantityField.getText());
                    BigDecimal costPrice = new BigDecimal(costPriceField.getText());

                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Количество должно быть больше 0");
                        return;
                    }
                    if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Цена должна быть больше 0");
                        return;
                    }

                    for (CartItem item : cart) {
                        if (item.getBookId() == selectedBook.getId()) {
                            item.setQuantity(item.getQuantity() + qty);
                            updateCartTable.run();
                            updateTotal.run();
                            quantityField.setText("");
                            costPriceField.setText("");
                            return;
                        }
                    }

                    cart.add(new CartItem(selectedBook.getId(), selectedBook.getTitle(), qty, costPrice));
                    updateCartTable.run();
                    updateTotal.run();
                    quantityField.setText("");
                    costPriceField.setText("");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Введите корректные числа");
                }
            });

            JPanel dialogButtonPanel = new JPanel();
            JButton removeButton = new JButton("Удалить выбранное");
            JButton completeButton = new JButton("Оформить поставку");
            JButton cancelButton = new JButton("Отмена");

            removeButton.addActionListener(e -> {
                int row = cartTable.getSelectedRow();
                if (row != -1) {
                    cart.remove(row);
                    updateCartTable.run();
                    updateTotal.run();
                }
            });

            completeButton.addActionListener(e -> {
                if (cart.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Добавьте товары в поставку!");
                    return;
                }

                try {
                    Supplier selectedSupplier = (Supplier) supplierBox.getSelectedItem();

                    BigDecimal totalCost = BigDecimal.ZERO;
                    for (CartItem item : cart) {
                        totalCost = totalCost.add(item.getTotal());
                    }

                    Supply supply = new Supply();
                    supply.setSupplyDate(new Timestamp(System.currentTimeMillis()));
                    supply.setSupplierId(selectedSupplier.getId());
                    supply.setTotalCost(totalCost);

                    int supplyId = supplyDAO.addSupply(supply);

                    for (CartItem item : cart) {
                        SupplyItem supplyItem = new SupplyItem(supplyId, item.getBookId(), item.getQuantity(), item.getCostPrice());
                        supplyDAO.addSupplyItem(supplyItem);

                        int currentStock = stockDAO.getQuantity(item.getBookId());
                        stockDAO.updateQuantity(item.getBookId(), currentStock + item.getQuantity());
                    }

                    dialog.dispose();
                    refreshTable();

                    Component parent = SupplyPanel.this;
                    while (!(parent instanceof MainFrame) && parent != null) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof MainFrame) {
                        ((MainFrame) parent).refreshAll();
                    }

                    JOptionPane.showMessageDialog(SupplyPanel.this, "Поставка добавлена!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialogButtonPanel.add(removeButton);
            dialogButtonPanel.add(completeButton);
            dialogButtonPanel.add(cancelButton);

            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.add(addPanel, BorderLayout.NORTH);
            centerPanel.add(cartScroll, BorderLayout.CENTER);

            dialog.add(topPanel, BorderLayout.NORTH);
            dialog.add(centerPanel, BorderLayout.CENTER);
            dialog.add(dialogButtonPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSupply() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Удалить поставку?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int quantity = (int) tableModel.getValueAt(row, 4);
                String bookTitle = (String) tableModel.getValueAt(row, 3);

                List<Book> books = bookDAO.getAll();
                for (Book book : books) {
                    if (book.getTitle().equals(bookTitle)) {
                        int currentStock = stockDAO.getQuantity(book.getId());
                        stockDAO.updateQuantity(book.getId(), currentStock - quantity);
                        break;
                    }
                }

                supplyDAO.deleteSupplyItem(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Поставка удалена");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }
}