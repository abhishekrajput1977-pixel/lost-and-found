package org.campusguard.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.campusguard.dto.JwtResponse;
import org.campusguard.dto.LoginRequest;
import org.campusguard.dto.RegisterRequest;
import org.campusguard.dto.UserDto;
import org.campusguard.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto registered = authService.registerUser(request);
        return ResponseEntity.ok(registered);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        JwtResponse jwtResponse = authService.authenticateUser(request);

        // Also attach JWT cookie for web sessions
        Cookie jwtCookie = new Cookie("JWT_TOKEN", jwtResponse.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 24 hours
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto currentUser = authService.getCurrentUserDto();
        return ResponseEntity.ok(currentUser);
    }
}
