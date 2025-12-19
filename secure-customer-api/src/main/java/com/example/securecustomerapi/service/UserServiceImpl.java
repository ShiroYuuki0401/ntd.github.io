package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.entity.RefreshToken;
import com.example.securecustomerapi.entity.Role;
import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.exception.DuplicateResourceException;
import com.example.securecustomerapi.exception.ResourceNotFoundException;
import com.example.securecustomerapi.repository.RefreshTokenRepository;
import com.example.securecustomerapi.repository.UserRepository;
import com.example.securecustomerapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create Refresh Token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(java.util.UUID.randomUUID().toString());
        refreshToken.setExpiryDate(java.time.LocalDateTime.now().plusDays(7));

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(refreshToken);

        // Use the constructor with 5 arguments (including refresh token)
        return new LoginResponseDTO(
                token,
                refreshToken.getToken(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }

    // ... register() and getCurrentUser() remain the same ...
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new DuplicateResourceException("Username already exists");
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new DuplicateResourceException("Email already exists");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER);
        user.setIsActive(true);

        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }

    // --- NEW METHODS ---

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(role);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(!user.getIsActive());
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateProfile(String username, UpdateProfileDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public void deleteAccount(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        user.setIsActive(false); // Soft delete
        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO refreshToken(String requestToken) {
        return refreshTokenRepository.findByToken(requestToken)
                .map(refreshToken -> {
                    if (refreshToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                        refreshTokenRepository.delete(refreshToken);
                        throw new RuntimeException("Refresh token was expired. Please make a new signin request");
                    }
                    User user = refreshToken.getUser();
                    String token = tokenProvider.generateTokenFromUsername(user.getUsername());
                    return new LoginResponseDTO(token, requestToken, user.getUsername(), user.getEmail(),
                            user.getRole().name());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    // --- Previous Password Methods ---

    @Override
    public void changePassword(String username, ChangePasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new IllegalArgumentException("Passwords do not match");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
            throw new IllegalArgumentException("Invalid current password");
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(),
                user.getRole().name(), user.getIsActive(), user.getCreatedAt());
    }
}