package cl.duoc.ms_auth.controller;

import cl.duoc.ms_auth.model.Usuario;
import cl.duoc.ms_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.username(), request.password());
            return ResponseEntity.ok(Map.of("accessToken", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        try {
            Usuario creado = authService.registrar(request.username(), request.password(), request.rol());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "Usuario registrado correctamente.",
                        "username", creado.getUsername(),
                        "rol", creado.getRol()
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    record LoginRequest(String username, String password) {}
    record RegisterRequest(String username, String password, String rol) {}
}
