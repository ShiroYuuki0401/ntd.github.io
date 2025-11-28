<%-- 
    Document   : change-password
    Created on : Nov 28, 2025, 4:48:25 PM
    Author     : shiro
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Change Password</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: #f5f5f5;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }
            .card {
                background: white;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                width: 350px;
            }
            h2 {
                text-align: center;
                color: #333;
                margin-top: 0;
            }
            input {
                width: 100%;
                padding: 10px;
                margin: 8px 0;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
            }
            button {
                width: 100%;
                padding: 10px;
                background: #3498db;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                margin-top: 10px;
            }
            button:hover {
                background: #2980b9;
            }
            .alert {
                color: #721c24;
                background: #f8d7da;
                padding: 10px;
                border-radius: 4px;
                margin-bottom: 10px;
                font-size: 14px;
                text-align: center;
            }
            .links {
                text-align: center;
                margin-top: 15px;
            }
            a {
                color: #666;
                text-decoration: none;
                font-size: 14px;
            }
            a:hover {
                color: #333;
            }
        </style>
    </head>
    <body>
        <div class="card">
            <h2>🔄 Change Password</h2>

            <c:if test="${not empty error}">
                <div class="alert">${error}</div>
            </c:if>

            <form action="change-password" method="post">
                <label>Current Password</label>
                <input type="password" name="oldPassword" required>

                <label>New Password</label>
                <input type="password" name="newPassword" required>

                <label>Confirm New Password</label>
                <input type="password" name="confirmPassword" required>

                <button type="submit">Update Password</button>
            </form>

            <div class="links">
                <a href="dashboard">Back to Dashboard</a>
            </div>
        </div>
    </body>
</html>
