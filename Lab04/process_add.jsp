<%-- 
    Document   : process_add
    Created on : Nov 8, 2025, 2:24:02 PM
    Author     : shiro
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.net.URLEncoder" %>
<%
    String studentCode = request.getParameter("student_code");
    String fullName = request.getParameter("full_name");
    String email = request.getParameter("email");
    String major = request.getParameter("major");
    String redirectUrl = "add_student.jsp";
    String errorMessage = null;

    // Build query string to preserve data on redirect
    String queryParams = "";
    queryParams += "&student_code=" + (studentCode != null ? URLEncoder.encode(studentCode, "UTF-8") : "");
    queryParams += "&full_name=" + (fullName != null ? URLEncoder.encode(fullName, "UTF-8") : "");
    queryParams += "&email=" + (email != null ? URLEncoder.encode(email, "UTF-8") : "");
    queryParams += "&major=" + (major != null ? URLEncoder.encode(major, "UTF-8") : "");

    // Server-side validation
    if (studentCode == null || studentCode.trim().isEmpty() || 
        fullName == null || fullName.trim().isEmpty()) {
        errorMessage = "Required fields (Student Code and Full Name) cannot be empty!";
        redirectUrl += "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + queryParams;
        response.sendRedirect(redirectUrl);
        return;
    }

    // Exercise 6.2: Student Code Pattern Validation
    String codeRegex = "[A-Z]{2}[0-9]{3,}";
    if (!studentCode.matches(codeRegex)) {
        errorMessage = "Invalid Student Code format (e.g., SV001).";
        redirectUrl += "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + queryParams;
        response.sendRedirect(redirectUrl);
        return;
    }
    
    // Exercise 6.1: Email Validation
    if (email != null && !email.trim().isEmpty()) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            errorMessage = "Invalid email format.";
            redirectUrl += "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + queryParams;
            response.sendRedirect(redirectUrl);
            return;
        }
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
            redirectUrl += "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + queryParams;
            response.sendRedirect(redirectUrl);
            rs.close(); // Close ResultSet
            return;
        }
        rs.close(); // Close ResultSet
        
        // Insert new student
        String insertSql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        pstmt.close(); // Close previous PreparedStatement
        pstmt = conn.prepareStatement(insertSql);
        pstmt.setString(1, studentCode.trim());
        pstmt.setString(2, fullName.trim());
        pstmt.setString(3, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
        pstmt.setString(4, (major != null && !major.trim().isEmpty()) ? major.trim() : null);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            response.sendRedirect("list_students.jsp?message=" + URLEncoder.encode("New student added successfully!", "UTF-8"));
        } else {
            errorMessage = "Failed to add student. Please try again.";
            redirectUrl += "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + queryParams;
            response.sendRedirect(redirectUrl);
        }
        
    } catch (SQLException e) {
        String dbError = "Database error occurred";
        if (e.getErrorCode() == 1062) {
            dbError = "Student code already exists!";
        }
        redirectUrl += "?error=" + URLEncoder.encode(dbError, "UTF-8") + queryParams;
        response.sendRedirect(redirectUrl);
    } catch (Exception e) {
        errorMessage = "System error occurred: " + e.getMessage();
        redirectUrl += "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + queryParams;
        response.sendRedirect(redirectUrl);
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>
