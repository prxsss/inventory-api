package com.phuriphat.inventoryapi.auth;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User userMock;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Provide a valid 256-bit Base64-encoded secret key for HS256
        String secret = "4qhq8lr/2rZk+c0c7E+Mqw9k2eLq2QXZyK1z7n5V4Xw=";
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "expirationsMs", 1000 * 60 * 60L); // 1 hour

        userMock = User.builder()
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("generateToken should return non-null token when user details are provided")
    void generateToken_withUserDetails_shouldReturnToken() {
        // WHEN
        String token = jwtService.generateToken(userMock);

        // THEN
        assertNotNull(token);
    }

    @Test
    @DisplayName("extractUsername should return correct username when valid token is provided")
    void extractUsername_withValidToken_shouldReturnUsername() {
        // GIVEN
        String token = jwtService.generateToken(userMock);

        // WHEN
        String username = jwtService.extractUsername(token);

        // THEN
        assertEquals(userMock.getEmail(), username);
    }

    @Test
    @DisplayName("isTokenValid should return true when token belongs to user and is not expired")
    void isTokenValid_withValidTokenAndUser_shouldReturnTrue() {
        // GIVEN
        String token = jwtService.generateToken(userMock);

        // WHEN
        boolean isValid = jwtService.isTokenValid(token, userMock);

        // THEN
        assertTrue(isValid);
    }

    @Test
    @DisplayName("isTokenValid should return false when token belongs to different user")
    void isTokenValid_withDifferentUser_shouldReturnFalse() {
        // GIVEN
        String token = jwtService.generateToken(userMock);

        User differentUser = User.builder()
                .email("different@example.com")
                .password("password123")
                .build();

        // WHEN
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // THEN
        assertFalse(isValid);
    }

    @Test
    @DisplayName("isTokenValid should throw ExpiredJwtException when token is expired")
    void isTokenExpired_withExpiredToken_shouldThrowExpiredJwtException() throws InterruptedException {
        // GIVEN
        ReflectionTestUtils.setField(jwtService, "expirationsMs", 1L);
        String token = jwtService.generateToken(userMock);

        // Sleep briefly to ensure the token expires
        Thread.sleep(10);

        // WHEN & THEN
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userMock));
    }
}