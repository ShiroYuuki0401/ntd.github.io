/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.studentmanagementmvc.controller;

/**
 *
 * @author shiro
 */
import com.mycompany.studentmanagementmvc.dao.StudentDAO;
import com.mycompany.studentmanagementmvc.model.Student;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// URL Mapping: This Servlet runs when accessing /student
@WebServlet("/student")
public class StudentController extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
                searchStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            case "sort":
                sortStudents(request, response);
                break;
            case "list":
            default:
                listStudents(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }

    // --- EXERCISE 6: VALIDATION ---
    private boolean validateStudent(HttpServletRequest request, Student student) {
        boolean isValid = true;

        // Regex match Lab 4: 2 letters + 3+ digits
        String codeRegex = "[A-Z]{2}[0-9]{3,}";
        if (student.getStudentCode() == null || !student.getStudentCode().matches(codeRegex)) {
            request.setAttribute("errorCode", "Format: 2 uppercase letters + 3+ digits (e.g., SV001)");
            isValid = false;
        }

        if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
            request.setAttribute("errorName", "Full Name is required");
            isValid = false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (student.getEmail() != null && !student.getEmail().isEmpty() && !student.getEmail().matches(emailRegex)) {
            request.setAttribute("errorEmail", "Invalid email format");
            isValid = false;
        }
        return isValid;
    }

    private void insertStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("studentCode");
        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        Student newStudent = new Student(code, name, email, major);

        // 1. Basic Validation
        if (!validateStudent(request, newStudent)) {
            request.setAttribute("student", newStudent);
            RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (studentDAO.isStudentCodeExists(code)) {
            request.setAttribute("errorCode", "Student code already exists!");
            request.setAttribute("student", newStudent);
            RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 3. Save
        studentDAO.addStudent(newStudent);
        response.sendRedirect("student?action=list&message=Student added successfully");
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String code = request.getParameter("studentCode");
        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        Student student = new Student(id, code, name, email, major, null);

        if (!validateStudent(request, student)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        studentDAO.updateStudent(student);
        response.sendRedirect("student?action=list&message=Student updated successfully");
    }

    // --- EXERCISE 5: SEARCH ---
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Student> list = studentDAO.searchStudents(keyword);
        request.setAttribute("listStudent", list);
        request.setAttribute("keyword", keyword);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // --- EXERCISE 7: FILTER (TEXT INPUT) ---
    private void filterStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String major = request.getParameter("major");
        List<Student> list = (major == null || major.isEmpty())
                ? studentDAO.getAllStudents() : studentDAO.getStudentsByMajor(major);
        request.setAttribute("listStudent", list);
        request.setAttribute("selectedMajor", major);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    private void sortStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String col = request.getParameter("col");
        String order = request.getParameter("order");
        if (order == null) {
            order = "ASC";
        }
        List<Student> list = studentDAO.getStudentsSorted(col, order);
        request.setAttribute("listStudent", list);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        int recordsPerPage = 5;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        int offset = (page - 1) * recordsPerPage;
        List<Student> list = studentDAO.getStudentsPaginated(offset, recordsPerPage);
        int noOfRecords = studentDAO.getTotalRecords();
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

        request.setAttribute("listStudent", list);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        request.setAttribute("student", existingStudent);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        studentDAO.deleteStudent(id);
        response.sendRedirect("student?action=list&message=Student deleted");
    }
}
