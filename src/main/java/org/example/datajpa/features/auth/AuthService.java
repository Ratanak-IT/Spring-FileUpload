package org.example.datajpa.features.auth;

import jakarta.validation.Valid;
import org.example.datajpa.features.auth.dto.RegisterRequest;
import org.example.datajpa.features.auth.dto.RegisterResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);
}
