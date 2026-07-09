package org.example.datajpa.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegisterResponse(
        String userId,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone
) {
}
