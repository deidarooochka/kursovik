package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private AuthorPanel authorPanel;
    private PublisherPanel publisherPanel;
    private CategoryPanel categoryPanel;
    private BookPanel bookPanel;
    private StockPanel stockPanel;
    private SupplierPanel supplierPanel;
    private CustomerPanel customerPanel;
    private EmployeePanel employeePanel;
    private SalePanel salePanel;
    private SupplyPanel supplyPanel;

    public MainFrame() {
        setTitle("Система учета продаж книжного магазина");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        authorPanel = new AuthorPanel();
        publisherPanel = new PublisherPanel();
        categoryPanel = new CategoryPanel();
        bookPanel = new BookPanel();
        stockPanel = new StockPanel();
        supplierPanel = new SupplierPanel();
        customerPanel = new CustomerPanel();
        employeePanel = new EmployeePanel();
        salePanel = new SalePanel();
        supplyPanel = new SupplyPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Авторы", authorPanel);
        tabbedPane.addTab("Издательства", publisherPanel);
        tabbedPane.addTab("Категории", categoryPanel);
        tabbedPane.addTab("Книги", bookPanel);
        tabbedPane.addTab("Склад", stockPanel);
        tabbedPane.addTab("Поставщики", supplierPanel);
        tabbedPane.addTab("Покупатели", customerPanel);
        tabbedPane.addTab("Сотрудники", employeePanel);
        tabbedPane.addTab("Продажи", salePanel);
        tabbedPane.addTab("Поставки", supplyPanel);

        add(tabbedPane);

        refreshAll();
    }

    public void refreshAll() {
        authorPanel.refreshTable();
        publisherPanel.refreshTable();
        categoryPanel.refreshTable();
        bookPanel.refreshTable();
        stockPanel.refreshTable();
        supplierPanel.refreshTable();
        customerPanel.refreshTable();
        employeePanel.refreshTable();
        salePanel.refreshTable();
        supplyPanel.refreshTable();
    }
}