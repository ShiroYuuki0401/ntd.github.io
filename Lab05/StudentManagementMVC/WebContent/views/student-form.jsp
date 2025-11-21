<%-- 
    Document   : student-form
    Created on : Nov 20, 2025
    Author     : shiro
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>
            <c:if test="${student != null}">Edit</c:if><c:if test="${student == null}">Add New</c:if> Student
        </title>
            <style>
                .container {
                    width: 50%;
                    margin: 50px auto;
                    padding: 20px;
                    border: 1px solid #ccc;
                    border-radius: 5px;
                }
                input, select {
                    width: 100%;
                    padding: 10px;
                    margin: 5px 0 20px 0;
                    display: inline-block;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    box-sizing: border-box;
                }
                button {
                    width: 100%;
                    background-color: #4CAF50;
                    color: white;
                    padding: 14px 20px;
                    margin: 8px 0;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                }
                button:hover {
                    background-color: #45a049;
                }
            </style>
        </head>
        <body>

            <div class="container">
                <h2>
                <c:if test="${student != null}">✏️ Edit Student</c:if>
                <c:if test="${student == null}">➕ Add New Student</c:if>
                </h2>

                <form action="student" method="post">

                    <input type="hidden" name="action" value="${student != null ? 'update' : 'insert'}" />

                <c:if test="${student != null}">
                    <input type="hidden" name="id" value="${student.id}" />
                </c:if>

                <label>Student Code:</label>
                <input type="text" name="studentCode" value="${student.studentCode}" required placeholder="e.g., SV001" />

                <label>Full Name:</label>
                <input type="text" name="fullName" value="${student.fullName}" required placeholder="John Doe" />

                <label>Email:</label>
                <input type="email" name="email" value="${student.email}" required placeholder="email@example.com" />

                <label>Major:</label> 
                <input type="text" id="major" name="major" 
                       placeholder="e.g., Computer Science"
                       value="<%= (request.getParameter("major") != null) ? request.getParameter("major") : ""%>">

                <button type="submit">Save Student</button>
                <br><br>
                <a href="student?action=list" style="text-decoration: none; text-align: center; display: block;">Back to List</a>
            </form>
        </div>

    </body>
</html>
