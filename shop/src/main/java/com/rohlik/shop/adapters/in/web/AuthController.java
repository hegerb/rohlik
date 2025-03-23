package com.rohlik.shop.adapters.in.web;

import com.rohlik.shop.adapters.out.persistence.JpaUserRepository;
import com.rohlik.shop.application.domain.UserEntity;
import com.rohlik.shop.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JpaUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Uživatel s tímto jménem již existuje"));
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.username());
        userEntity.setPassword(passwordEncoder.encode(request.password()));
        userEntity.setRole("USER");

        userRepository.save(userEntity);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}

record AuthRequest(String username, String password) {}
record RegisterRequest(
    @NotBlank(message = "Uživatelské jméno je povinné")
    String username,
    
    @NotBlank(message = "Heslo je povinné")
    @Size(min = 5, message = "Heslo musí mít alespoň 5 znaků")
    String password
) {}
record AuthResponse(String token) {} 