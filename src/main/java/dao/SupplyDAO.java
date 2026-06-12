package dao;

import model.Supply;
import model.SupplyItem;
import db.DatabaseInitializer;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplyDAO {

    public int addSupply(Supply supply) throws SQLException {
        String sql = "INSERT INTO supplies (supply_date, supplier_id, total_cost) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, supply.getSupplyDate());
            stmt.setInt(2, supply.getSupplierId());
            stmt.setBigDecimal(3, supply.getTotalCost());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public void addSupplyItem(SupplyItem item) throws SQLException {
        String sql = "INSERT INTO supply_items (supply_id, book_id, quantity, cost_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getSupplyId());
            stmt.setInt(2, item.getBookId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getCostPrice());
            stmt.executeUpdate();
        }
    }

    public List<SupplyItem> getAllSupplyItems() throws SQLException {
        List<SupplyItem> list = new ArrayList<>();
        String sql = "SELECT si.*, s.supply_date, s.total_cost, sup.name as supplier_name, b.title as book_title " +
                "FROM supply_items si " +
                "JOIN supplies s ON si.supply_id = s.id " +
                "JOIN suppliers sup ON s.supplier_id = sup.id " +
                "JOIN books b ON si.book_id = b.id " +
                "ORDER BY si.id DESC";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SupplyItem item = new SupplyItem();
                item.setId(rs.getInt("id"));
                item.setSupplyId(rs.getInt("supply_id"));
                item.setBookId(rs.getInt("book_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setCostPrice(rs.getBigDecimal("cost_price"));
                item.setBookTitle(rs.getString("book_title"));
                item.setSupplyDate(rs.getTimestamp("supply_date"));
                item.setSupplierName(rs.getString("supplier_name"));
                item.setTotalCost(rs.getBigDecimal("total_cost"));
                list.add(item);
            }
        }
        return list;
    }

    public void deleteSupplyItem(int id) throws SQLException {
        String sql = "DELETE FROM supply_items WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    public List<Supply> getAllSupplies() throws SQLException {
        List<Supply> list = new ArrayList<>();
        String sql = "SELECT s.*, sup.name as sup_name FROM supplies s JOIN suppliers sup ON s.supplier_id = sup.id ORDER BY s.id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Supply s = new Supply();
                s.setId(rs.getInt("id"));
                s.setSupplyDate(rs.getTimestamp("supply_date"));
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setTotalCost(rs.getBigDecimal("total_cost"));
                s.setSupplierName(rs.getString("sup_name"));
                list.add(s);
            }
        }
        return list;
    }
}