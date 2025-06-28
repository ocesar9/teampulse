package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import br.com.ifpe.teampulse.dto.LoginRequestDTO;
import br.com.ifpe.teampulse.dto.RegisterRequestDTO;
import br.com.ifpe.teampulse.dto.ResponseDTO;
import br.com.ifpe.teampulse.infra.security.TokenService;
import br.com.ifpe.teampulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        // Validação de campos obrigatórios
        if (body.email() == null || body.email().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email é obrigatório");
        }
        if (body.password() == null || body.password().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password é obrigatório");
        }

        Optional<User> userOptional = this.repository.findByEmail(body.email());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }

        User user = userOptional.get();

        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity
                    .ok(new ResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getUserType(), token));
        }
        return ResponseEntity.badRequest().body("Credenciais inválidas");
    }

    // Registro (ADMIN)
    @PostMapping("/register/admin")
    public ResponseEntity registerAdmin(@RequestBody RegisterRequestDTO body) {
        // Validação de restrições
        String validationError = validateUserData(body, UserType.ADMIN);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        // Verifica se usuário já existe por email
        if (this.repository.findByEmail(body.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já está em uso");
        }

        try {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setUsername(body.username());
            newUser.setUserType(UserType.ADMIN);

            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity
                    .ok(new ResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getEmail(), newUser.getUserType(), token));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Violação de restrição de dados: " + extractConstraintError(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }

    // Registro (GERENTE, COLABORADOR)
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        UserType requestedUserType = body.userType() != null ? body.userType() : UserType.COLABORADOR;

        // Não permite criar ADMIN através deste endpoint
        if (requestedUserType == UserType.ADMIN) {
            return ResponseEntity.status(403).body("Use endpoint /auth/register/admin para criar administradores");
        }

        // Validação de restrições
        String validationError = validateUserData(body, requestedUserType);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        // Para GERENTE e COLABORADOR, verifica autenticação e permissões
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(403).body("Acesso negado: autenticação necessária");
        }

        User currentUser = (User) authentication.getPrincipal();

        // Verifica as permissões baseadas no tipo de usuário atual
        if (!hasPermissionToRegister(currentUser.getUserType(), requestedUserType)) {
            return ResponseEntity.status(403).body("Acesso negado: permissão insuficiente");
        }

        // Verifica se usuário já existe por email
        if (this.repository.findByEmail(body.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já está em uso");
        }

        try {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setUsername(body.username());
            newUser.setUserType(requestedUserType);

            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity
                    .ok(new ResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getEmail(), newUser.getUserType(), token));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Violação de restrição de dados: " + extractConstraintError(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }

    // Validação de dados do usuário baseada nas restrições da entidade
    private String validateUserData(RegisterRequestDTO body, UserType userType) {
        // Validação de campos obrigatórios (nullable = false)
        if (body.username() == null || body.username().trim().isEmpty()) {
            return "Username é obrigatório";
        }
        if (body.email() == null || body.email().trim().isEmpty()) {
            return "Email é obrigatório";
        }
        if (body.password() == null || body.password().trim().isEmpty()) {
            return "Password é obrigatório";
        }

        // Validação de tamanhos (length constraints)
        if (body.username().length() > 50) {
            return "Username deve ter no máximo 50 caracteres";
        }
        if (body.username().length() < 3) {
            return "Username deve ter no mínimo 3 caracteres";
        }
        if (body.email().length() > 100) {
            return "Email deve ter no máximo 100 caracteres";
        }
        if (body.password().length() > 100) {
            return "Password deve ter no máximo 100 caracteres";
        }
        if (body.password().length() < 8) {
            return "Password deve ter no mínimo 8 caracteres";
        }

        // Validação de formato de email
        if (!isValidEmail(body.email())) {
            return "Email deve ter formato válido";
        }

        return null; // Sem erros
    }

    // Validação simples de email
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    // Extrai erro específico da constraint violada
    private String extractConstraintError(DataIntegrityViolationException e) {
        String message = e.getMessage().toLowerCase();
        if (message.contains("uk_users_email") || message.contains("email")) {
            return "Email já está em uso";
        }
        return "dados duplicados ou inválidos";
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
