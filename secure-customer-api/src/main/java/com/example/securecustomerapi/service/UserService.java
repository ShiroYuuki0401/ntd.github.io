package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.entity.Role;
import java.util.List;

public interface UserService {

    LoginResponseDTO login(LoginRequestDTO loginRequest);

    UserResponseDTO register(RegisterRequestDTO registerRequest);

    UserResponseDTO getCurrentUser(String username);

    // Password & Auth
    void changePassword(String username, ChangePasswordDTO dto);

    String forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    LoginResponseDTO refreshToken(String requestToken); // Moved logic from Controller to Service

    // Admin & Profile methods
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long userId, Role role);

    UserResponseDTO toggleUserStatus(Long userId);

    UserResponseDTO updateProfile(String username, UpdateProfileDTO dto);

    void deleteAccount(String username, String password);
}