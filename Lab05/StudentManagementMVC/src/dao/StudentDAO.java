/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.studentmanagementmvc.dao;

/**
 *
 * @author shiro
 */
import com.mycompany.studentmanagementmvc.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // <--- EDIT YOUR PASSWORD HERE

    // Get database connection
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // 1. Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student s = new Student(
                        rs.getInt("id"),
                        rs.getString("student_code"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("major"),
                        rs.getTimestamp("created_at")
                );
                students.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    // 2. Add new student
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getStudentCode());
            pstmt.setString(2, s.getFullName());
            pstmt.setString(3, s.getEmail());
            pstmt.setString(4, s.getMajor());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Get student by ID (Used for populating edit form)
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("student_code"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("major"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 4. Update student
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET student_code = ?, full_name = ?, email = ?, major = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getStudentCode());
            pstmt.setString(2, s.getFullName());
            pstmt.setString(3, s.getEmail());
            pstmt.setString(4, s.getMajor());
            pstmt.setInt(5, s.getId()); // Important: set ID to identify which record to update

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Delete student
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}