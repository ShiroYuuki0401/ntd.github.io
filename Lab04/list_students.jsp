<%-- 
    Document   : list_students
    Created on : Nov 8, 2025, 2:12:17 PM
    Author     : shiro
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h1 { color: #333; }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-bottom: 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
        }
        th {
            background-color: #007bff;
            color: white;
            padding: 12px;
            text-align: left;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        tr:hover { background-color: #f8f9fa; }
        .action-link {
            color: #007bff;
            text-decoration: none;
            margin-right: 10px;
        }
        .delete-link { color: #dc3545; }
        
        /* Exercise 5.1: Search Form Styling */
        .search-form {
            margin-bottom: 20px;
            padding: 15px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .search-form input[type="text"] {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            width: 300px;
        }
        .search-form button {
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .search-form a {
            padding: 10px 20px;
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        /* Exercise 7.1: Pagination Styling */
        .pagination {
            margin-top: 20px;
        }
        .pagination strong {
            display: inline-block;
            padding: 8px 12px;
            margin: 0 2px;
            border-radius: 5px;
            background-color: #007bff;
            color: white;
            border: 1px solid #007bff;
        }
        .pagination a {
            display: inline-block;
            padding: 8px 12px;
            margin: 0 2px;
            border-radius: 5px;
            background-color: white;
            color: #007bff;
            text-decoration: none;
            border: 1px solid #ddd;
        }
        .pagination a:hover {
            background-color: #f8f9fa;
        }
        
        /* Exercise 7.2c: Responsive Table */
        .table-responsive {
            overflow-x: auto;
        }
        @media (max-width: 768px) {
            table {
                font-size: 12px;
            }
            th, td {
                padding: 5px;
            }
            .search-form input[type="text"] {
                width: 100%;
                box-sizing: border-box;
                margin-bottom: 10px;
            }
            .search-form button, .search-form a {
                width: 100%;
                display: block;
                text-align: center;
                box-sizing: border-box;
            }
            .search-form a { margin-top: 5px; }
        }
    </style>
</head>
<body>
    <h1>📚 Student Management System</h1>
    
    <% if (request.getParameter("message") != null) { %>
        <div class="message success">
            <%= request.getParameter("message") %>
        </div>
    <% } %>
    
    <% if (request.getParameter("error") != null) { %>
        <div class="message error">
            <%= request.getParameter("error") %>
        </div>
    <% } %>
    
    <a href="add_student.jsp" class="btn">➕ Add New Student</a>
    
    <form action="list_students.jsp" method="GET" class="search-form">
        <input type="text" name="keyword" 
               value="<%= (request.getParameter("keyword") != null) ? request.getParameter("keyword") : "" %>" 
               placeholder="Search by name or code...">
        <button type="submit">Search</button>
        <a href="list_students.jsp">Clear</a>
    </form>
    
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Student Code</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Major</th>
                    <th>Created At</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
<%
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    PreparedStatement countPstmt = null;
    ResultSet countRs = null;

    int totalRecords = 0;
    int totalPages = 1;
    int recordsPerPage = 10; // Exercise 7.1 requirement
    
    // Exercise 5.2: Get keyword
    String keyword = request.getParameter("keyword");
    
    // Exercise 7.1: Get current page
    String pageParam = request.getParameter("page");
    int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
    int offset = (currentPage - 1) * recordsPerPage;
    
    String keywordParam = "";
    if (keyword != null && !keyword.trim().isEmpty()) {
        keywordParam = "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
    }

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_management",
            "root",
            "root"
        );
        
        // Exercise 7.1: Get total records (filtered by search)
        String countSql = "SELECT COUNT(*) FROM students";
        if (keyword != null && !keyword.trim().isEmpty()) {
            countSql += " WHERE full_name LIKE ? OR student_code LIKE ?";
        }
        countPstmt = conn.prepareStatement(countSql);
        if (keyword != null && !keyword.trim().isEmpty()) {
            countPstmt.setString(1, "%" + keyword + "%");
            countPstmt.setString(2, "%" + keyword + "%");
        }
        countRs = countPstmt.executeQuery();
        if (countRs.next()) {
            totalRecords = countRs.getInt(1);
        }
        totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        
        // Exercise 5.2 & 7.1: Main query with Search and Pagination
        String sql;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search query
            sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";
        } else {
            // Normal query
            sql = "SELECT * FROM students ORDER BY id DESC LIMIT ? OFFSET ?";
        }
        
        pstmt = conn.prepareStatement(sql);
        
        // Set parameters
        if (keyword != null && !keyword.trim().isEmpty()) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setInt(3, recordsPerPage);
            pstmt.setInt(4, offset);
        } else {
            pstmt.setInt(1, recordsPerPage);
            pstmt.setInt(2, offset);
        }
        
        rs = pstmt.executeQuery();
        
        if (!rs.isBeforeFirst() && totalRecords == 0) {
             out.println("<tr><td colspan='7'>No students found.</td></tr>");
        } else if (!rs.isBeforeFirst() && totalRecords > 0) {
             out.println("<tr><td colspan='7'>No students found for this search/page.</td></tr>");
        }
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String studentCode = rs.getString("student_code");
            String fullName = rs.getString("full_name");
            String email = rs.getString("email");
            String major = rs.getString("major");
            Timestamp createdAt = rs.getTimestamp("created_at");
%>
            <tr>
                <td><%= id %></td>
                <td><%= studentCode %></td>
                <td><%= fullName %></td>
                <td><%= email != null ? email : "N/A" %></td>
                <td><%= major != null ? major : "N/A" %></td>
                <td><%= createdAt %></td>
                <td>
                    <a href="edit_student.jsp?id=<%= id %>" class="action-link">✏️ Edit</a>
                    <a href="delete_student.jsp?id=<%= id %>" 
                       class="action-link delete-link"
                       onclick="return confirm('Are you sure?')">🗑️ Delete</a>
                </td>
            </tr>
<%
        }
    } catch (ClassNotFoundException e) {
        out.println("<tr><td colspan='7'>Error: JDBC Driver not found!</td></tr>");
        e.printStackTrace();
    } catch (SQLException e) {
        out.println("<tr><td colspan='7'>Database Error: " + e.getMessage() + "</td></tr>");
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (countRs != null) countRs.close();
            if (pstmt != null) pstmt.close();
            if (countPstmt != null) countPstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>
            </tbody>
        </table>
    </div>
    
    <div class="pagination">
        <% if (currentPage > 1) { %>
            <a href="list_students.jsp?page=<%= currentPage - 1 %><%= keywordParam %>">Previous</a>
        <% } %>
        
        <% for (int i = 1; i <= totalPages; i++) { %>
            <% if (i == currentPage) { %>
                <strong><%= i %></strong>
            <% } else { %>
                <a href="list_students.jsp?page=<%= i %><%= keywordParam %>"><%= i %></a>
            <% } %>
        <% } %>
        
        <% if (currentPage < totalPages) { %>
            <a href="list_students.jsp?page=<%= currentPage + 1 %><%= keywordParam %>">Next</a>
        <% } %>
    </div>
    
    <script>
    setTimeout(function() {
        var messages = document.querySelectorAll('.message');
        messages.forEach(function(msg) {
            msg.style.display = 'none';
        });
    }, 3000); // 3 seconds
    </script>
</body>
</html>
