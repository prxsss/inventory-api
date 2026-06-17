package com.phuriphat.inventoryapi.auth.dto;

import com.phuriphat.inventoryapi.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long id;

    private String name;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}
