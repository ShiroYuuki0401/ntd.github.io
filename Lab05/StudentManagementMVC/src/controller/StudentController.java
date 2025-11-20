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
        // Initialize DAO when Servlet starts
        studentDAO = new StudentDAO();
    }

    // ---------------- HANDLE GET REQUEST (Page Navigation) ----------------
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
            default:
                listStudents(request, response);
                break;
        }
    }

    // 1. Show new form (Empty form)
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    // 2. Show edit form (Form with existing data)
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id")); // Get ID from URL
        Student existingStudent = studentDAO.getStudentById(id); // Find student in DB

        request.setAttribute("student", existingStudent); // Send to JSP to populate input fields
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    // 3. Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        studentDAO.deleteStudent(id);
        response.sendRedirect("student?action=list"); // Reload list after deletion
    }

    // Method to display list
    private void listStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Student> listStudents = studentDAO.getAllStudents();
        request.setAttribute("listStudent", listStudents);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // ---------------- HANDLE POST REQUEST (Form Submission) ----------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Required to handle UTF-8 characters
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

    // 4. Insert into DB
    private void insertStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String code = request.getParameter("studentCode");
        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");

        Student newStudent = new Student(code, name, email, major);
        studentDAO.addStudent(newStudent);

        // Use sendRedirect to avoid duplicate form submission on F5
        response.sendRedirect("student?action=list");
    }

    // 5. Update in DB
    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id")); // Get ID from hidden input
        String code = request.getParameter("studentCode");
        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");

        // Create student object with updated info
        Student book = new Student(id, code, name, email, major, null);
        studentDAO.updateStudent(book);

        response.sendRedirect("student?action=list");
    }
}