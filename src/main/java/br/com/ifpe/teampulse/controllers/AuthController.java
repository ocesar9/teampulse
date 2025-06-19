package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import br.com.ifpe.teampulse.dto.LoginRequestDTO;
import br.com.ifpe.teampulse.dto.RegisterRequestDTO;
import br.com.ifpe.teampulse.dto.ResponseDTO;
import br.com.ifpe.teampulse.infra.security.TokenService;
import br.com.ifpe.teampulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // Login (todos)
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        User user = this.repository.findByEmail(body.email()).orElseThrow(
                () -> new RuntimeException("Email não encontrado"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getUsername(), user.getEmail(), token, user.getUserType()));
        }
        return ResponseEntity.badRequest().build();
    }

    // Registro (ADMIN)
    @PostMapping("/register/admin")
    public ResponseEntity registerAdmin(@RequestBody RegisterRequestDTO body) {
        // Endpoint específico para criação de ADMIN (sem autenticação necessária)
        Optional<User> existingUser = this.repository.findByEmail(body.email());

        if (!existingUser.isPresent()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setUsername(body.username());
            newUser.setUserType(UserType.ADMIN);

            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity
                    .ok(new ResponseDTO(newUser.getUsername(), newUser.getEmail(), token, newUser.getUserType()));
        }

        return ResponseEntity.badRequest().body("Usuário já existe com este email.");
    }

    // Registro (GERENTE, COLABORADOR)
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        UserType requestedUserType = body.userType() != null ? body.userType() : UserType.COLABORADOR;

        // Não permite criar ADMIN através deste endpoint
        if (requestedUserType == UserType.ADMIN) {
            return ResponseEntity.status(403).body("Endpoint /auth/register/admin para criar administradores.");
        }

        // Para GERENTE e COLABORADOR, verifica autenticação e permissões
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        User currentUser = (User) authentication.getPrincipal();

        // Verifica as permissões baseadas no tipo de usuário atual
        if (!hasPermissionToRegister(currentUser.getUserType(), requestedUserType)) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        Optional<User> existingUser = this.repository.findByEmail(body.email());

        if (!existingUser.isPresent()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setUsername(body.username());
            newUser.setUserType(requestedUserType);

            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity
                    .ok(new ResponseDTO(newUser.getUsername(), newUser.getEmail(), token, newUser.getUserType()));
        }

        return ResponseEntity.badRequest().body("Usuário já existe com este email.");
    }

    // Permissões
    private boolean hasPermissionToRegister(UserType currentUserType, UserType targetUserType) {
        switch (currentUserType) {
            case ADMIN:
                // Admin pode registrar GERENTE e COLABORADOR
                return targetUserType == UserType.GERENTE || targetUserType == UserType.COLABORADOR;
            case GERENTE:
                // Gerente pode registrar apenas COLABORADOR
                return targetUserType == UserType.COLABORADOR;
            case COLABORADOR:
                // Colaborador não pode registrar ninguém
                return false;
            default:
                return false;
        }
    }
}
