package dao;

import model.Employee;
import db.DatabaseInitializer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void add(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (first_name, last_name, position, birth_date, salary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getPosition());
            stmt.setDate(4, employee.getBirthDate());
            stmt.setBigDecimal(5, employee.getSalary());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) employee.setId(rs.getInt(1));
        }
    }

    public List<Employee> getAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY id";
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("id"));
                e.setFirstName(rs.getString("first_name"));
                e.setLastName(rs.getString("last_name"));
                e.setPosition(rs.getString("position"));
                e.setBirthDate(rs.getDate("birth_date"));
                e.setSalary(rs.getBigDecimal("salary"));
                list.add(e);
            }
        }
        return list;
    }

    public void update(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET first_name=?, last_name=?, position=?, birth_date=?, salary=? WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getPosition());
            stmt.setDate(4, employee.getBirthDate());
            stmt.setBigDecimal(5, employee.getSalary());
            stmt.setInt(6, employee.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}