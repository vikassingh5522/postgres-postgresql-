package com.arc.dao;

import java.sql.*;
import java.util.*;

import com.arc.model.Student;

public class StudentDAO {
    private final String JDBC_URL = "jdbc:postgresql://localhost:5432/studentdb";
    private final String USER = "postgres";
    private final String PASSWORD = "Vikas@9156";

    public StudentDAO() {
        createTableIfNotExists();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS student (" +
                     "id SERIAL PRIMARY KEY, " +
                     "name VARCHAR(100) NOT NULL, " +
                     "email VARCHAR(100) NOT NULL)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Student table created or already exists");
        } catch (SQLException e) {
            System.err.println("Error creating student table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY id";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Student(rs.getInt("id"), 
                                   rs.getString("name"), 
                                   rs.getString("email")));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public void insert(Student student) {
        String sql = "INSERT INTO student(name, email) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Student student) {
        String sql = "UPDATE student SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setInt(3, student.getId());
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                System.err.println("No student found with ID: " + student.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                System.err.println("No student found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Student get(int id) {
        Student student = null;
        String sql = "SELECT * FROM student WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                student = new Student(rs.getInt("id"), 
                                     rs.getString("name"), 
                                     rs.getString("email"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting student with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return student;
    }
}