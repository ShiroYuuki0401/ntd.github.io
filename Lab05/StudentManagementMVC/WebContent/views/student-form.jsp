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
        <title>Student Form</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background: #f5f5f5;
            }
            .container {
                width: 50%;
                margin: 50px auto;
                padding: 30px;
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            input {
                width: 100%;
                padding: 10px;
                margin: 5px 0;
                border: 1px solid #ddd;
                border-radius: 5px;
                box-sizing: border-box;
            }
            .error {
                color: #721c24;
                background: #f8d7da;
                padding: 5px;
                border-radius: 3px;
                font-size: 0.9em;
                display: block;
                margin-bottom: 10px;
            }
            button {
                background: #28a745;
                color: white;
                padding: 12px 30px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                margin-top: 10px;
            }
            a {
                display: inline-block;
                margin-left: 10px;
                color: #6c757d;
                text-decoration: none;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <h2>
                <c:if test="${student != null && student.id > 0}">✏️ Edit Student</c:if>
                <c:if test="${student == null || student.id == 0}">➕ Add New Student</c:if>
                </h2>

                <form action="student" method="post">
                    <input type="hidden" name="action" value="${student.id > 0 ? 'update' : 'insert'}" />
                <c:if test="${student.id > 0}"><input type="hidden" name="id" value="${student.id}" /></c:if>

                    <label>Student Code <span style="color:red">*</span></label>
                    <input type="text" name="studentCode" value="${student.studentCode}" placeholder="e.g., SV001" ${student.id > 0 ? 'readonly' : ''} />
                <c:if test="${not empty errorCode}"><span class="error">⚠️ ${errorCode}</span></c:if>

                    <label>Full Name <span style="color:red">*</span></label>
                    <input type="text" name="fullName" value="${student.fullName}" placeholder="Full Name" />
                <c:if test="${not empty errorName}"><span class="error">⚠️ ${errorName}</span></c:if>

                    <label>Email</label>
                    <input type="text" name="email" value="${student.email}" placeholder="email@example.com" />
                <c:if test="${not empty errorEmail}"><span class="error">⚠️ ${errorEmail}</span></c:if>

                    <label>Major</label>
                    <input type="text" name="major" value="${student.major}" placeholder="e.g., Computer Science" />

                <button type="submit">Save Student</button>
                <a href="student?action=list">Cancel</a>
            </form>
        </div>

    </body>
</html>
