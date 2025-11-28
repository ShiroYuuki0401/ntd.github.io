<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Student Management</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
                background-color: #f5f5f5;
            }
            table {
                border-collapse: collapse;
                width: 100%;
                margin-top: 20px;
                background: white;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 12px;
                text-align: left;
            }
            th {
                background-color: #007bff;
                color: white;
            }
            th a {
                color: white;
                text-decoration: none;
            }
            .toolbar {
                display: flex;
                justify-content: space-between;
                background: white;
                padding: 15px;
                border-radius: 5px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }
            .btn {
                padding: 10px 20px;
                background: #28a745;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                border: none;
                cursor: pointer;
            }
            .btn-danger {
                background: #dc3545;
            }
            .pagination {
                margin-top: 20px;
                text-align: center;
            }
            .pagination a {
                display: inline-block;
                padding: 8px 12px;
                margin: 0 2px;
                border: 1px solid #ddd;
                background: white;
                text-decoration: none;
                color: #007bff;
                border-radius: 5px;
            }
            .pagination a.active {
                background-color: #007bff;
                color: white;
                border-color: #007bff;
            }
            .message {
                padding: 10px;
                margin-bottom: 10px;
                border-radius: 5px;
            }
            .success {
                background-color: #d4edda;
                color: #155724;
            }

            @media (max-width: 768px) {
                .toolbar {
                    flex-direction: column;
                    gap: 10px;
                }
                input[type="text"] {
                    width: 100%;
                    box-sizing: border-box;
                }
            }
        </style>
    </head>
    <body>

        <h1 style="text-align: center; color: #333;">📚 Student Management System</h1>

        <c:if test="${not empty param.message}">
            <div class="message success">${param.message}</div>
        </c:if>

        <div class="toolbar">
            <form action="student" method="get" style="display: flex; gap: 10px;">
                <input type="hidden" name="action" value="search">
                <input type="text" name="keyword" placeholder="Search by name/code..." value="${keyword}" style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                <button type="submit" class="btn" style="background: #007bff;">Search</button>
                <c:if test="${not empty keyword}"><a href="student?action=list" class="btn btn-danger">Clear</a></c:if>
                </form>

                <form action="student" method="get" style="display: flex; gap: 10px;">
                    <input type="hidden" name="action" value="filter">
                    <input type="text" name="major" placeholder="Filter by Major..." value="${selectedMajor}" style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                <button type="submit" class="btn" style="background: #6c757d;">Filter</button>
                <c:if test="${not empty selectedMajor}"><a href="student?action=list" class="btn btn-danger">Clear</a></c:if>
                </form>
            <c:if test="${sessionScope.role == 'admin'}">
                <a href="student?action=new" class="btn">➕ Add New</a>
            </c:if>
        </div>

        <div style="overflow-x: auto;">
            <table>
                <thead>
                    <tr>
                        <th><a href="student?action=sort&col=id&order=ASC">ID 🔼</a></th>
                        <th><a href="student?action=sort&col=student_code&order=ASC">Code 🔼</a></th>
                        <th><a href="student?action=sort&col=full_name&order=ASC">Full Name 🔼</a></th>
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
                                <c:if test="${sessionScope.role == 'admin'}">
                                    <a href="student?action=edit&id=${s.id}" style="color: #007bff; margin-right: 10px;">✏️ Edit</a>
                                    <a href="student?action=delete&id=${s.id}" style="color: #dc3545;" onclick="return confirm('Are you sure?');">🗑️ Delete</a>
                                </c:if>
                                <c:if test="${sessionScope.role != 'admin'}">
                                    <span style="color: #999; font-style: italic;">View Only</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${noOfPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}"><a href="student?action=list&page=${currentPage - 1}">« Previous</a></c:if>
                <c:forEach begin="1" end="${noOfPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}"><a class="active">${i}</a></c:when>
                        <c:otherwise><a href="student?action=list&page=${i}">${i}</a></c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:if test="${currentPage < noOfPages}"><a href="student?action=list&page=${currentPage + 1}">Next »</a></c:if>
                </div>
        </c:if>

        <script>
            setTimeout(function () {
                var msg = document.querySelector('.message');
                if (msg)
                    msg.style.display = 'none';
            }, 3000);
        </script>
    </body>
</html>
