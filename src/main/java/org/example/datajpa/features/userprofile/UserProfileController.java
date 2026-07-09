package org.example.datajpa.features.userprofile;


import lombok.RequiredArgsConstructor;
import org.example.datajpa.features.userprofile.dto.response.UserProfileResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserProfileResponse me(){
        return userProfileService.me();
    }
}
