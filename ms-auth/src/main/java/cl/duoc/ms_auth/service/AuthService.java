package cl.duoc.ms_auth.service;

import cl.duoc.ms_auth.model.Usuario;
import cl.duoc.ms_auth.repository.UsuarioRepository;
import cl.duoc.ms_auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Usuario registrar(String username, String password, String rol) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El usuario '" + username + "' ya existe.");
        }
        String rolFinal = (rol != null && !rol.isBlank()) ? rol : "ROLE_USER";
        Usuario nuevo = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .rol(rolFinal)
                .build();
        return usuarioRepository.save(nuevo);
    }

    public String login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas."));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        return jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());
    }
}
