package dao;

import model.Publisher;
import db.DatabaseInitializer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublisherDAO {

    public void add(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO publishers (name, phone, address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getPhone());
            stmt.setString(3, publisher.getAddress());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) publisher.setId(rs.getInt(1));
        }
    }

    public List<Publisher> getAll() throws SQLException {
        List<Publisher> list = new ArrayList<>();
        String sql = "SELECT * FROM publishers ORDER BY id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Publisher p = new Publisher();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPhone(rs.getString("phone"));
                p.setAddress(rs.getString("address"));
                list.add(p);
            }
        }
        return list;
    }

    public void update(Publisher publisher) throws SQLException {
        String sql = "UPDATE publishers SET name=?, phone=?, address=? WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getPhone());
            stmt.setString(3, publisher.getAddress());
            stmt.setInt(4, publisher.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM publishers WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}