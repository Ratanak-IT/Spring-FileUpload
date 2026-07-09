package org.example.datajpa.features.auth;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datajpa.features.auth.dto.RegisterRequest;
import org.example.datajpa.features.auth.dto.RegisterResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest registerRequest){
        return authService.register(registerRequest);
    }
}
