<%-- 
    Document   : process_add
    Created on : Nov 8, 2025, 2:24:02 PM
    Author     : shiro
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    String studentCode = request.getParameter("student_code");
    String fullName = request.getParameter("full_name");
    String email = request.getParameter("email");
    String major = request.getParameter("major");
    String redirectUrl = "add_student.jsp";
    String errorMessage = null;

    // Server-side validation
    if (studentCode == null || studentCode.trim().isEmpty() || 
        fullName == null || fullName.trim().isEmpty()) {
        errorMessage = "Required fields (Student Code and Full Name) cannot be empty!";
        redirectUrl += "?error=" + errorMessage.replace(" ", "%20");
        if (studentCode != null) redirectUrl += "&student_code=" + studentCode;
        if (fullName != null) redirectUrl += "&full_name=" + fullName;
        if (email != null) redirectUrl += "&email=" + email;
        if (major != null) redirectUrl += "&major=" + major;
        response.sendRedirect(redirectUrl);
        return;
    }

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_management",
            "root",
            "root"
        );
        
        // Check for duplicate student code
        String checkSql = "SELECT COUNT(*) AS count FROM students WHERE student_code = ?";
        pstmt = conn.prepareStatement(checkSql);
        pstmt.setString(1, studentCode.trim());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        
        if (rs.getInt("count") > 0) {
            errorMessage = "Student code already exists! Please use a different code.";
            redirectUrl += "?error=" + errorMessage.replace(" ", "%20") +
                          "&student_code=" + studentCode +
                          "&full_name=" + fullName +
                          "&email=" + (email != null ? email : "") +
                          "&major=" + (major != null ? major : "");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Insert new student
        String insertSql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        pstmt = conn.prepareStatement(insertSql);
        pstmt.setString(1, studentCode.trim());
        pstmt.setString(2, fullName.trim());
        pstmt.setString(3, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
        pstmt.setString(4, (major != null && !major.trim().isEmpty()) ? major.trim() : null);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            response.sendRedirect("list_students.jsp?message=New student added successfully!");
        } else {
            response.sendRedirect("add_student.jsp?error=Failed to add student. Please try again.");
        }
        
    } catch (SQLException e) {
        String dbError = "Database error occurred";
        if (e.getErrorCode() == 1062) {
            dbError = "Student code already exists!";
        }
        redirectUrl += "?error=" + dbError.replace(" ", "%20") +
                      "&student_code=" + studentCode +
                      "&full_name=" + fullName +
                      "&email=" + (email != null ? email : "") +
                      "&major=" + (major != null ? major : "");
        response.sendRedirect(redirectUrl);
    } catch (Exception e) {
        response.sendRedirect("add_student.jsp?error=System error occurred");
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>