package dao;

import model.Stock;
import db.DatabaseInitializer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    public void addOrUpdate(Stock stock) throws SQLException {
        String sql = "MERGE INTO stocks (book_id, quantity) KEY(book_id) VALUES (?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stock.getBookId());
            stmt.setInt(2, stock.getQuantity());
            stmt.executeUpdate();
        }
    }

    public List<Stock> getAll() throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.*, b.title FROM stocks s JOIN books b ON s.book_id = b.id ORDER BY s.id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Stock s = new Stock();
                s.setId(rs.getInt("id"));
                s.setBookId(rs.getInt("book_id"));
                s.setQuantity(rs.getInt("quantity"));
                s.setBookTitle(rs.getString("title"));
                list.add(s);
            }
        }
        return list;
    }

    public void updateQuantity(int bookId, int newQuantity) throws SQLException {
        String sql = "UPDATE stocks SET quantity = ? WHERE book_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, bookId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                String insertSql = "INSERT INTO stocks (book_id, quantity) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, bookId);
                    insertStmt.setInt(2, newQuantity);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public int getQuantity(int bookId) throws SQLException {
        String sql = "SELECT quantity FROM stocks WHERE book_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity");
            }
            return 0;
        }
    }
}