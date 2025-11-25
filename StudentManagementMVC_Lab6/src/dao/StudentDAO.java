package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // <--- UPDATE PASSWORD

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Check duplicate code (Logic from Lab 4)
    public boolean isStudentCodeExists(String code) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_code = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

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

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET full_name = ?, email = ?, major = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(2, s.getEmail());
            pstmt.setString(3, s.getMajor());
            pstmt.setInt(4, s.getId());
            // Note: Student Code usually isn't updated, but name is
            pstmt.setString(1, s.getFullName());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    // Exercise 5: Search (Name or Code)
    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ? ORDER BY id DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    // Exercise 7: Filter by Major (Using LIKE for text input)
    public List<Student> getStudentsByMajor(String major) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE major LIKE ? ORDER BY id DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + major + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    // Exercise 7: Sort
    public List<Student> getStudentsSorted(String column, String order) {
        List<Student> students = new ArrayList<>();
        if (!isValidColumn(column)) {
            column = "id";
        }
        if (!order.equalsIgnoreCase("ASC")) {
            order = "DESC";
        }
        String sql = "SELECT * FROM students ORDER BY " + column + " " + order;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    // Exercise 8: Pagination
    public int getTotalRecords() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Student> getStudentsPaginated(int offset, int limit) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC LIMIT ? OFFSET ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("student_code"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("major"),
                rs.getTimestamp("created_at")
        );
    }

    private boolean isValidColumn(String col) {
        return col.equals("id") || col.equals("student_code") || col.equals("full_name") || col.equals("email") || col.equals("major");
    }
}
