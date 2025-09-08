package io.github.srdejo.technical.test.controllers;

import io.github.srdejo.technical.test.dtos.request.LoginRequest;
import io.github.srdejo.technical.test.dtos.request.SignUpRequest;
import io.github.srdejo.technical.test.dtos.response.UserResponse;
import io.github.srdejo.technical.test.service.LoginService;
import io.github.srdejo.technical.test.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final SignUpService signUpService;
    private final LoginService loginService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        UserResponse response = signUpService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponse> loginWithToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado o inválido");
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok(loginService.loginWithToken(token));
    }
}
