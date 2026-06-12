package dao;

import model.Sale;
import model.SaleItem;
import db.DatabaseInitializer;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class SaleDAO {

    public int addSale(Sale sale) throws SQLException {
        String sql = "INSERT INTO sales (sale_date, customer_id, employee_id, total_amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, sale.getSaleDate());
            stmt.setInt(2, sale.getCustomerId());
            stmt.setInt(3, sale.getEmployeeId());
            stmt.setBigDecimal(4, sale.getTotalAmount());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public void addSaleItem(SaleItem item) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, book_id, quantity, price_at_sale) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getSaleId());
            stmt.setInt(2, item.getBookId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getPriceAtSale());
            stmt.executeUpdate();
        }
    }

    public List<Sale> getAllSales() throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, c.first_name || ' ' || c.last_name as cust_name, " +
                "e.first_name || ' ' || e.last_name as emp_name " +
                "FROM sales s " +
                "LEFT JOIN customers c ON s.customer_id = c.id " +
                "LEFT JOIN employees e ON s.employee_id = e.id " +
                "ORDER BY s.id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getInt("id"));
                s.setSaleDate(rs.getTimestamp("sale_date"));
                s.setCustomerId(rs.getInt("customer_id"));
                s.setEmployeeId(rs.getInt("employee_id"));
                s.setTotalAmount(rs.getBigDecimal("total_amount"));
                s.setCustomerName(rs.getString("cust_name"));
                s.setEmployeeName(rs.getString("emp_name"));
                list.add(s);
            }
        }
        return list;
    }

    public BigDecimal getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM sales";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal("total");
        }
        return BigDecimal.ZERO;
    }

    public List<SaleItem> getSaleItems(int saleId) throws SQLException {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT si.*, b.title as book_title FROM sale_items si " +
                "LEFT JOIN books b ON si.book_id = b.id " +
                "WHERE si.sale_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SaleItem item = new SaleItem();
                item.setId(rs.getInt("id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setBookId(rs.getInt("book_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceAtSale(rs.getBigDecimal("price_at_sale"));
                item.setBookTitle(rs.getString("book_title"));
                list.add(item);
            }
        }
        return list;
    }

    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, c.first_name || ' ' || c.last_name as cust_name, " +
                "e.first_name || ' ' || e.last_name as emp_name " +
                "FROM sales s " +
                "LEFT JOIN customers c ON s.customer_id = c.id " +
                "LEFT JOIN employees e ON s.employee_id = e.id " +
                "WHERE s.sale_date >= ? AND s.sale_date < ? " +
                "ORDER BY s.id DESC";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getInt("id"));
                s.setSaleDate(rs.getTimestamp("sale_date"));
                s.setCustomerId(rs.getInt("customer_id"));
                s.setEmployeeId(rs.getInt("employee_id"));
                s.setTotalAmount(rs.getBigDecimal("total_amount"));
                s.setCustomerName(rs.getString("cust_name"));
                s.setEmployeeName(rs.getString("emp_name"));
                list.add(s);
            }
        }
        return list;
    }
}