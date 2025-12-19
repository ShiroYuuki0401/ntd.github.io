package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.ChangePasswordDTO;
import com.example.securecustomerapi.dto.UpdateProfileDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;
import com.example.securecustomerapi.service.UserService;
import jakarta.validation.Valid;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // You need to add updateProfile method to UserService
        return ResponseEntity.ok(userService.updateProfile(username, dto));
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestParam String password) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteAccount(username, password);
        return ResponseEntity.ok(new java.util.HashMap<String, String>() {
            {
                put("message", "Account deactivated successfully");
            }
        });
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userService.changePassword(username, dto);
            return ResponseEntity.ok(new HashMap<String, String>() {
                {
                    put("message", "Password changed successfully");
                }
            });
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {
                {
                    put("message", e.getMessage());
                }
            });
        }
    }
}