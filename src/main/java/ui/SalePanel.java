package ui;

import dao.SaleDAO;
import dao.StockDAO;
import dao.BookDAO;
import dao.CustomerDAO;
import dao.EmployeeDAO;
import model.Sale;
import model.SaleItem;
import model.Book;
import model.Customer;
import model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTable detailsTable;
    private DefaultTableModel detailsTableModel;
    private SaleDAO saleDAO;
    private StockDAO stockDAO;
    private BookDAO bookDAO;
    private CustomerDAO customerDAO;
    private EmployeeDAO employeeDAO;
    private JLabel revenueLabel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;

    public SalePanel() {
        saleDAO = new SaleDAO();
        stockDAO = new StockDAO();
        bookDAO = new BookDAO();
        customerDAO = new CustomerDAO();
        employeeDAO = new EmployeeDAO();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        JButton resetButton = new JButton("Сброс");

        searchButton.addActionListener(e -> searchSales());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        JPanel gluePanel = new JPanel();
        gluePanel.setLayout(new BoxLayout(gluePanel, BoxLayout.X_AXIS));
        gluePanel.add(Box.createHorizontalGlue());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        filterPanel.add(new JLabel("Фильтр:"));

        String[] filters = {"Все продажи", "Сегодня", "Вчера", "Эта неделя", "Этот месяц", "Этот год"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setPreferredSize(new Dimension(120, 25));
        filterCombo.addActionListener(e -> refreshTable());
        filterPanel.add(filterCombo);

        topPanel.add(searchPanel);
        topPanel.add(gluePanel);
        topPanel.add(filterPanel);

        revenueLabel = new JLabel("Общая выручка: 0.00 руб.");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        revenueLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel revenuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        revenuePanel.add(revenueLabel);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(revenuePanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        String[] columns = {"ID", "Дата", "Покупатель", "Сотрудник", "Сумма"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSaleDetails();
            }
        });
        JScrollPane topScroll = new JScrollPane(table);
        topScroll.setBorder(BorderFactory.createTitledBorder("Список продаж"));

        String[] detailsColumns = {"ID книги", "Название", "Количество", "Цена", "Сумма"};
        detailsTableModel = new DefaultTableModel(detailsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailsTable = new JTable(detailsTableModel);
        JScrollPane bottomScroll = new JScrollPane(detailsTable);
        bottomScroll.setBorder(BorderFactory.createTitledBorder("Товары в выбранной продаже"));

        splitPane.setTopComponent(topScroll);
        splitPane.setBottomComponent(bottomScroll);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton sellButton = new JButton("Оформить продажу");
        JButton refreshButton = new JButton("Обновить");

        sellButton.addActionListener(e -> showSaleDialog());
        refreshButton.addActionListener(e -> {
            refreshTable();
            detailsTableModel.setRowCount(0);
        });

        buttonPanel.add(sellButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private LocalDateTime getFilterStartDate() {
        String filter = (String) filterCombo.getSelectedItem();
        LocalDate now = LocalDate.now();

        switch (filter) {
            case "Сегодня":
                return now.atStartOfDay();
            case "Вчера":
                return now.minusDays(1).atStartOfDay();
            case "Эта неделя":
                return now.minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay();
            case "Этот месяц":
                return now.withDayOfMonth(1).atStartOfDay();
            case "Этот год":
                return now.withDayOfYear(1).atStartOfDay();
            default:
                return null;
        }
    }

    private LocalDateTime getFilterEndDate() {
        String filter = (String) filterCombo.getSelectedItem();
        LocalDate now = LocalDate.now();

        switch (filter) {
            case "Сегодня":
                return now.plusDays(1).atStartOfDay();
            case "Вчера":
                return now.atStartOfDay();
            case "Эта неделя":
                return now.plusDays(7 - now.getDayOfWeek().getValue()).atStartOfDay();
            case "Этот месяц":
                return now.withDayOfMonth(now.lengthOfMonth()).plusDays(1).atStartOfDay();
            case "Этот год":
                return now.withDayOfYear(now.lengthOfYear()).plusDays(1).atStartOfDay();
            default:
                return null;
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Sale> sales;
            LocalDateTime startDate = getFilterStartDate();
            LocalDateTime endDate = getFilterEndDate();

            if (startDate != null && endDate != null) {
                sales = saleDAO.getSalesByDateRange(startDate, endDate);
            } else {
                sales = saleDAO.getAllSales();
            }

            for (Sale s : sales) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getSaleDate(),
                        s.getCustomerName() != null ? s.getCustomerName() : "Гость",
                        s.getEmployeeName(),
                        s.getTotalAmount()
                });
            }
            updateRevenue(sales);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    private void updateRevenue(List<Sale> sales) {
        BigDecimal total = BigDecimal.ZERO;
        for (Sale s : sales) {
            if (s.getTotalAmount() != null) {
                total = total.add(s.getTotalAmount());
            }
        }
        revenueLabel.setText(String.format("Общая выручка: %.2f руб.", total));
    }

    private void loadSaleDetails() {
        detailsTableModel.setRowCount(0);
        int row = table.getSelectedRow();
        if (row == -1) return;

        int saleId = (int) tableModel.getValueAt(row, 0);

        try {
            List<SaleItem> items = saleDAO.getSaleItems(saleId);
            for (SaleItem item : items) {
                detailsTableModel.addRow(new Object[]{
                        item.getBookId(),
                        item.getBookTitle() != null ? item.getBookTitle() : "Книга ID=" + item.getBookId(),
                        item.getQuantity(),
                        item.getPriceAtSale(),
                        item.getPriceAtSale().multiply(BigDecimal.valueOf(item.getQuantity()))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки товаров: " + e.getMessage());
        }
    }

    private void searchSales() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Sale> sales;
            LocalDateTime startDate = getFilterStartDate();
            LocalDateTime endDate = getFilterEndDate();

            if (startDate != null && endDate != null) {
                sales = saleDAO.getSalesByDateRange(startDate, endDate);
            } else {
                sales = saleDAO.getAllSales();
            }

            for (Sale s : sales) {
                String customerName = s.getCustomerName() != null ? s.getCustomerName() : "Гость";
                if (customerName.toLowerCase().contains(searchText) ||
                        (s.getEmployeeName() != null && s.getEmployeeName().toLowerCase().contains(searchText))) {
                    tableModel.addRow(new Object[]{
                            s.getId(), s.getSaleDate(), customerName, s.getEmployeeName(), s.getTotalAmount()
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
        private String title;
        private BigDecimal price;
        private int quantity;

        public CartItem(int bookId, String title, BigDecimal price, int quantity) {
            this.bookId = bookId;
            this.title = title;
            this.price = price;
            this.quantity = quantity;
        }

        public int getBookId() { return bookId; }
        public String getTitle() { return title; }
        public BigDecimal getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getTotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
    }

    private void showSaleDialog() {
        try {
            List<Book> books = bookDAO.getAll();
            List<Customer> customers = customerDAO.getAll();
            List<Employee> allEmployees = employeeDAO.getAll();
            List<Employee> employees = new java.util.ArrayList<>();

            for (Employee e : allEmployees) {
                String position = e.getPosition().toLowerCase();
                if (position.equals("продавец") || position.equals("менеджер") || position.equals("старший продавец")) {
                    employees.add(e);
                }
            }

            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет продавцов! Добавьте сотрудника с должностью 'Продавец' или 'Менеджер'.");
                return;
            }

            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет книг для продажи!");
                return;
            }
            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет сотрудников! Добавьте сотрудника.");
                return;
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Оформление продажи", true);
            dialog.setSize(750, 550);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new GridBagLayout());
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            Customer guestCustomer = null;
            for (Customer c : customers) {
                if (c.getId() == 2 || "Гость".equals(c.getFirstName())) {
                    guestCustomer = c;
                    break;
                }
            }

            java.util.List<Customer> customerList = new java.util.ArrayList<>(customers);
            if (guestCustomer != null) {
                customerList.remove(guestCustomer);
                customerList.add(0, guestCustomer);
            }

            JComboBox<Customer> customerBox = new JComboBox<>(customerList.toArray(new Customer[0]));
            JComboBox<Employee> employeeBox = new JComboBox<>(employees.toArray(new Employee[0]));

            JButton newCustomerButton = new JButton("Новый покупатель");
            newCustomerButton.addActionListener(e -> {
                dialog.dispose();
                showAddCustomerDialog();
                SwingUtilities.invokeLater(() -> showSaleDialog());
            });

            gbc.gridx = 0; gbc.gridy = 0;
            topPanel.add(new JLabel("Покупатель:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            topPanel.add(customerBox, gbc);
            gbc.gridx = 2;
            gbc.weightx = 0;
            topPanel.add(newCustomerButton, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            gbc.weightx = 0;
            topPanel.add(new JLabel("Сотрудник:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            topPanel.add(employeeBox, gbc);

            String[] cartColumns = {"ID", "Книга", "Цена", "Количество", "Сумма"};
            DefaultTableModel cartModel = new DefaultTableModel(cartColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable cartTable = new JTable(cartModel);
            JScrollPane cartScroll = new JScrollPane(cartTable);
            cartScroll.setBorder(BorderFactory.createTitledBorder("Корзина"));

            JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            addPanel.setBorder(BorderFactory.createTitledBorder("Добавить товар"));

            JComboBox<Book> bookBox = new JComboBox<>(books.toArray(new Book[0]));
            JTextField quantityField = new JTextField(5);
            JButton addButton = new JButton("Добавить в корзину");
            JLabel totalLabel = new JLabel("Итого: 0.00 руб.");
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

            addPanel.add(new JLabel("Книга:"));
            addPanel.add(bookBox);
            addPanel.add(new JLabel("Кол-во:"));
            addPanel.add(quantityField);
            addPanel.add(addButton);
            addPanel.add(totalLabel);

            java.util.List<CartItem> cart = new java.util.ArrayList<>();

            Runnable updateTotal = () -> {
                BigDecimal total = BigDecimal.ZERO;
                for (CartItem item : cart) {
                    total = total.add(item.getTotal());
                }
                totalLabel.setText(String.format("Итого: %.2f руб.", total));
            };

            Runnable updateCartTable = () -> {
                cartModel.setRowCount(0);
                for (CartItem item : cart) {
                    cartModel.addRow(new Object[]{
                            item.getBookId(),
                            item.getTitle(),
                            item.getPrice(),
                            item.getQuantity(),
                            item.getTotal()
                    });
                }
            };

            addButton.addActionListener(e -> {
                try {
                    Book selectedBook = (Book) bookBox.getSelectedItem();
                    int qty = Integer.parseInt(quantityField.getText());
                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Количество должно быть больше 0");
                        return;
                    }

                    int currentStock = stockDAO.getQuantity(selectedBook.getId());
                    if (qty > currentStock) {
                        JOptionPane.showMessageDialog(dialog, "Недостаточно книг! Доступно: " + currentStock);
                        return;
                    }

                    for (CartItem item : cart) {
                        if (item.getBookId() == selectedBook.getId()) {
                            item.setQuantity(item.getQuantity() + qty);
                            updateCartTable.run();
                            updateTotal.run();
                            quantityField.setText("");
                            return;
                        }
                    }

                    cart.add(new CartItem(selectedBook.getId(), selectedBook.getTitle(), selectedBook.getPrice(), qty));
                    updateCartTable.run();
                    updateTotal.run();
                    quantityField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Введите корректное количество");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage());
                }
            });

            JPanel dialogButtonPanel = new JPanel();
            JButton removeButton = new JButton("Удалить выбранное");
            JButton completeButton = new JButton("Оформить продажу");
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
                    JOptionPane.showMessageDialog(dialog, "Добавьте товары в корзину!");
                    return;
                }

                try {
                    Customer selectedCustomer = (Customer) customerBox.getSelectedItem();
                    Employee selectedEmployee = (Employee) employeeBox.getSelectedItem();

                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for (CartItem item : cart) {
                        totalAmount = totalAmount.add(item.getTotal());
                    }

                    Sale sale = new Sale();
                    sale.setSaleDate(new Timestamp(System.currentTimeMillis()));

                    sale.setCustomerId(selectedCustomer.getId());
                    sale.setEmployeeId(selectedEmployee.getId());
                    sale.setTotalAmount(totalAmount);

                    int saleId = saleDAO.addSale(sale);

                    for (CartItem item : cart) {
                        SaleItem saleItem = new SaleItem();
                        saleItem.setSaleId(saleId);
                        saleItem.setBookId(item.getBookId());
                        saleItem.setQuantity(item.getQuantity());
                        saleItem.setPriceAtSale(item.getPrice());
                        saleDAO.addSaleItem(saleItem);

                        int currentStock = stockDAO.getQuantity(item.getBookId());
                        stockDAO.updateQuantity(item.getBookId(), currentStock - item.getQuantity());
                    }

                    dialog.dispose();
                    refreshTable();
                    JOptionPane.showMessageDialog(this, String.format("Продажа оформлена на сумму %.2f руб.", totalAmount));

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

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавление покупателя", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JTextField birthDateField = new JTextField(15);
        birthDateField.setText("ГГГГ-ММ-ДД");

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Имя:"), gbc);
        gbc.gridx = 1;
        dialog.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Фамилия:"), gbc);
        gbc.gridx = 1;
        dialog.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Дата рождения:"), gbc);
        gbc.gridx = 1;
        dialog.add(birthDateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String phone = phoneField.getText().trim();

                if (firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Имя и фамилия обязательны!");
                    return;
                }

                Customer customer = new Customer();
                customer.setFirstName(firstName);
                customer.setLastName(lastName);
                customer.setPhone(phone.isEmpty() ? null : phone);

                String birthDateStr = birthDateField.getText().trim();
                if (!birthDateStr.isEmpty() && !birthDateStr.equals("ГГГГ-ММ-ДД")) {
                    customer.setBirthDate(java.sql.Date.valueOf(birthDateStr));
                }

                customerDAO.add(customer);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Покупатель добавлен!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }
}