package dao;

import model.Book;
import db.DatabaseInitializer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void add(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, price, author_id, publisher_id, category_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setBigDecimal(2, book.getPrice());
            stmt.setInt(3, book.getAuthorId());
            stmt.setInt(4, book.getPublisherId());
            stmt.setInt(5, book.getCategoryId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) book.setId(rs.getInt(1));
        }
    }

    public List<Book> getAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.*, a.first_name, a.last_name, p.name as pub_name, c.name as cat_name " +
                "FROM books b " +
                "LEFT JOIN authors a ON b.author_id = a.id " +
                "LEFT JOIN publishers p ON b.publisher_id = p.id " +
                "LEFT JOIN categories c ON b.category_id = c.id " +
                "ORDER BY b.id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getInt("id"));
                b.setTitle(rs.getString("title"));
                b.setPrice(rs.getBigDecimal("price"));
                b.setAuthorId(rs.getInt("author_id"));
                b.setPublisherId(rs.getInt("publisher_id"));
                b.setCategoryId(rs.getInt("category_id"));
                b.setAuthorName(rs.getString("first_name") + " " + rs.getString("last_name"));
                b.setPublisherName(rs.getString("pub_name"));
                b.setCategoryName(rs.getString("cat_name"));
                list.add(b);
            }
        }
        return list;
    }

    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET title=?, price=?, author_id=?, publisher_id=?, category_id=? WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setBigDecimal(2, book.getPrice());
            stmt.setInt(3, book.getAuthorId());
            stmt.setInt(4, book.getPublisherId());
            stmt.setInt(5, book.getCategoryId());
            stmt.setInt(6, book.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}