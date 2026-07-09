package org.example.datajpa.features.userprofile.dto.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        String userId,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String avatar,
        String gender,
        String biography
) {
}
