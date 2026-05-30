package com.phuriphat.inventoryapi.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User userMock;

    @BeforeEach
    void setUp() {
        userMock = User.builder()
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when user exists")
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // GIVEN
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userMock));

        // WHEN
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // THEN
        assertEquals(userMock.getEmail(), userDetails.getUsername());

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_whenUserNotFound_shouldThrowException() {
        // GIVEN
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // WHEN
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@example.com"));

        // THEN
        assertEquals("User not found: unknown@example.com", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }
}