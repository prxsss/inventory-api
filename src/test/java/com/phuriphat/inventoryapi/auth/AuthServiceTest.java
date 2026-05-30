package com.phuriphat.inventoryapi.auth;

import com.phuriphat.inventoryapi.auth.dto.AuthResponse;
import com.phuriphat.inventoryapi.auth.dto.LoginRequest;
import com.phuriphat.inventoryapi.auth.dto.RegisterRequest;
import com.phuriphat.inventoryapi.exception.DuplicateEmailException;
import com.phuriphat.inventoryapi.exception.InvalidCredentialsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register should return auth response when email is unique")
    void register_withValidRequest_shouldReturnAuthResponse() {
        // GIVEN
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");
        User savedUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        // WHEN
        AuthResponse response = authService.register(request);

        // THEN
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        assertEquals("John Doe", userCaptor.getValue().getName());
        assertEquals("john@example.com", userCaptor.getValue().getEmail());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
        assertEquals(Role.USER, userCaptor.getValue().getRole());

        verify(passwordEncoder).encode(request.getPassword());
        verify(jwtService).generateToken(userCaptor.getValue());
    }

    @Test
    @DisplayName("register should throw DuplicateEmailException when email already exists")
    void register_withDuplicateEmail_shouldThrowDuplicateEmailException() {
        // GIVEN
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(User.builder().build()));

        // WHEN
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                () -> authService.register(request));

        // THEN
        assertEquals("Email already exists", exception.getMessage());

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("login should return auth response when credentials are valid")
    void login_withValidRequest_shouldReturnAuthResponse() {
        // GIVEN
        LoginRequest request = new LoginRequest("john@example.com", "password123");
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // WHEN
        AuthResponse response = authService.login(request);

        // THEN
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.getEmail());
        verify(jwtService).generateToken(user);
    }

    @Test
    @DisplayName("login should throw InvalidCredentialsException when user does not exist")
    void login_withNonExistentUser_shouldThrowInvalidCredentialsException() {
        // GIVEN
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // WHEN
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));

        // THEN
        assertEquals("Invalid email or password", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.getEmail());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
