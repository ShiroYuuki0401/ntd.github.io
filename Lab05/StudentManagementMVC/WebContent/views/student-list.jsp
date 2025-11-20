<%-- 
    Document   : student-list
    Created on : Nov 20, 2025
    Author     : shiro
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Student List</title>
    <style>
        table { border-collapse: collapse; width: 80%; margin: 20px auto; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        h1 { text-align: center; font-family: sans-serif; }
        .btn-add { display: block; width: 150px; margin: 0 auto; padding: 10px; background: #008CBA; color: white; text-align: center; text-decoration: none; border-radius: 4px;}
    </style>
</head>
<body>

    <h1>🎓 Student Management (MVC)</h1>
    
    <a href="student?action=new" class="btn-add">+ Add New Student</a>
    <br>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Student Code</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Major</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="s" items="${listStudent}">
                <tr>
                    <td>${s.id}</td>
                    <td>${s.studentCode}</td>
                    <td>${s.fullName}</td>
                    <td>${s.email}</td>
                    <td>${s.major}</td>
                    <td>
                        <a href="student?action=edit&id=${s.id}">Edit</a> |
                        <a href="student?action=delete&id=${s.id}" onclick="return confirm('Are you sure you want to delete this student?');">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</body>
</html>