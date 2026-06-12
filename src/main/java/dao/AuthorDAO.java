package dao;

import model.Author;
import db.DatabaseInitializer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    public void add(Author author) throws SQLException {
        String sql = "INSERT INTO authors (first_name, last_name) VALUES (?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) author.setId(rs.getInt(1));
        }
    }

    public List<Author> getAll() throws SQLException {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Author a = new Author();
                a.setId(rs.getInt("id"));
                a.setFirstName(rs.getString("first_name"));
                a.setLastName(rs.getString("last_name"));
                list.add(a);
            }
        }
        return list;
    }

    public void update(Author author) throws SQLException {
        String sql = "UPDATE authors SET first_name=?, last_name=? WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setInt(3, author.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM authors WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}