/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author shiro
 */
import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user"); // Lấy user từ session

        // 1. Lấy dữ liệu từ form
        String oldPass = request.getParameter("oldPassword");
        String newPass = request.getParameter("newPassword");
        String confirmPass = request.getParameter("confirmPassword");

        // 2. Validate cơ bản
        if (newPass == null || !newPass.equals(confirmPass)) {
            request.setAttribute("error", "New passwords do not match!");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
            return;
        }

        // 3. Kiểm tra mật khẩu cũ
        if (!userDAO.verifyPassword(user.getId(), oldPass)) {
            request.setAttribute("error", "Current password is incorrect!");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
            return;
        }

        // 4. Cập nhật mật khẩu mới
        if (userDAO.changePassword(user.getId(), newPass)) {
            // Logout user sau khi đổi pass để bắt đăng nhập lại (Optional but recommended)
            session.invalidate();
            response.sendRedirect("login?message=Password changed successfully. Please login again.");
        } else {
            request.setAttribute("error", "System error. Please try again.");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
        }
    }
}
