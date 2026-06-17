package com.phuriphat.inventoryapi.auth;

import com.phuriphat.inventoryapi.auth.dto.AuthResponse;
import com.phuriphat.inventoryapi.auth.dto.LoginRequest;
import com.phuriphat.inventoryapi.auth.dto.ProfileResponse;
import com.phuriphat.inventoryapi.auth.dto.RegisterRequest;
import com.phuriphat.inventoryapi.common.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse response = authService.register(registerRequest);
        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Registered successfully")
                .data(response)
                .build();

        return  ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse response = authService.login(loginRequest);
        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();

        return  ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ProfileResponse response = authService.getProfile(userDetails.getUsername());
        ApiResponse<ProfileResponse> apiResponse = ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
