package dao;

import model.Customer;
import db.DatabaseInitializer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void add(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, phone, birth_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPhone());
            stmt.setDate(4, customer.getBirthDate());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) customer.setId(rs.getInt(1));
        }
    }

    public List<Customer> getAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setPhone(rs.getString("phone"));
                c.setBirthDate(rs.getDate("birth_date"));
                list.add(c);
            }
        }
        return list;
    }

    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name=?, last_name=?, phone=?, birth_date=? WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPhone());
            stmt.setDate(4, customer.getBirthDate());
            stmt.setInt(5, customer.getId());

            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Обновлено строк в customers: " + rowsUpdated);

            if (rowsUpdated == 0) {
                throw new SQLException("Покупатель с ID " + customer.getId() + " не найден");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}