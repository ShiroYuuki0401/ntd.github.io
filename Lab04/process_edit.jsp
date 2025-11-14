<%-- 
    Document   : process_edit
    Created on : Nov 8, 2025, 2:24:22 PM
    Author     : shiro
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.net.URLEncoder" %>
<%
    String idParam = request.getParameter("id");
    String studentCode = request.getParameter("student_code");
    String fullName = request.getParameter("full_name");
    String email = request.getParameter("email");
    String major = request.getParameter("major");
    String errorMessage = null;

    // Validation
    if (idParam == null || idParam.trim().isEmpty()) {
        response.sendRedirect("list_students.jsp?error=Invalid+ID");
        return;
    }
    if (fullName == null || fullName.trim().isEmpty()) {
        errorMessage = "Missing required fields";
        response.sendRedirect("edit_student.jsp?id=" + idParam + "&error=" + URLEncoder.encode(errorMessage, "UTF-8"));
        return;
    }

    int studentId = 0;
    try {
        studentId = Integer.parseInt(idParam);
    } catch (NumberFormatException e) {
        response.sendRedirect("list_students.jsp?error=Invalid student ID format");
        return;
    }

    // Exercise 6.1: Email Validation
    if (email != null && !email.trim().isEmpty()) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            errorMessage = "Invalid email format.";
            response.sendRedirect("edit_student.jsp?id=" + studentId + "&error=" + URLEncoder.encode(errorMessage, "UTF-8"));
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
        
        // Update student information
        String updateSql = "UPDATE students SET full_name = ?, email = ?, major = ? WHERE id = ?";
        pstmt = conn.prepareStatement(updateSql);
        pstmt.setString(1, fullName.trim());
        pstmt.setString(2, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
        pstmt.setString(3, (major != null && !major.trim().isEmpty()) ? major.trim() : null);
        pstmt.setInt(4, studentId);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            response.sendRedirect("list_students.jsp?message=" + URLEncoder.encode("Student information updated successfully!", "UTF-8"));
        } else {
            errorMessage = "Student not found or no changes made";
            response.sendRedirect("edit_student.jsp?id=" + studentId + "&error=" + URLEncoder.encode(errorMessage, "UTF-8"));
        }
        
    } catch (SQLException e) {
        errorMessage = "Database error: " + e.getErrorCode();
        response.sendRedirect("edit_student.jsp?id=" + studentId + "&error=" + URLEncoder.encode(errorMessage, "UTF-8"));
    } catch (Exception e) {
        errorMessage = "System error occurred: " + e.getMessage();
        response.sendRedirect("edit_student.jsp?id=" + studentId + "&error=" + URLEncoder.encode(errorMessage, "UTF-8"));
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>
