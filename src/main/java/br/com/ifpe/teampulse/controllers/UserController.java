package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import br.com.ifpe.teampulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Listar (todos)
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getUserList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<User> allUsers = userRepository.findAll();
        List<User> filteredUsers = filterUsersByPermission(currentUser.getUserType(), allUsers);

        if (filteredUsers.isEmpty()) {
            return buildSuccessResponse("Nenhum usuário encontrado", Map.of("users", List.of()));
        }

        List<Map<String, Object>> userList = filteredUsers.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("userType", user.getUserType());
                    return userMap;
                })
                .collect(Collectors.toList());

        return buildSuccessResponse("Lista de usuários recuperada com sucesso", Map.of("users", userList));
    }

    // Contador (todos)
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<User> allUsers = userRepository.findAll();
        List<User> filteredUsers = filterUsersByPermission(currentUser.getUserType(), allUsers);

        Map<UserType, Long> countByType = filteredUsers.stream()
                .collect(Collectors.groupingBy(User::getUserType, Collectors.counting()));

        Map<String, Object> response = new HashMap<>();

        switch (currentUser.getUserType()) {
            case ADMIN:
                response.put("admin", countByType.getOrDefault(UserType.ADMIN, 0L));
                response.put("gerente", countByType.getOrDefault(UserType.GERENTE, 0L));
                response.put("colaborador", countByType.getOrDefault(UserType.COLABORADOR, 0L));
                response.put("total", filteredUsers.size());
                break;
            case GERENTE:
                response.put("gerente", countByType.getOrDefault(UserType.GERENTE, 0L));
                response.put("colaborador", countByType.getOrDefault(UserType.COLABORADOR, 0L));
                response.put("total", filteredUsers.size());
                break;
            case COLABORADOR:
                response.put("colaborador", countByType.getOrDefault(UserType.COLABORADOR, 0L));
                response.put("total", filteredUsers.size());
                break;
        }

        return buildSuccessResponse("Contagem de usuários recuperada com sucesso", response);
    }

    // Listar (UserType)
    public ResponseEntity<Map<String, Object>> getUsersByType(@PathVariable String userType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        try {
            UserType requestedType = UserType.valueOf(userType.toUpperCase());

            if (!canViewUserType(currentUser.getUserType(), requestedType)) {
                return buildForbiddenResponse("Você não tem permissão para visualizar este tipo de usuário");
            }

            List<User> users = userRepository.findByUserType(requestedType);

            if (users.isEmpty()) {
                return buildSuccessResponse("Nenhum usuário encontrado para o tipo especificado",
                        Map.of("users", List.of()));
            }

            List<Map<String, Object>> userList = users.stream()
                    .map(user -> {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", user.getId());
                        userMap.put("username", user.getUsername());
                        userMap.put("email", user.getEmail());
                        userMap.put("userType", user.getUserType());
                        return userMap;
                    })
                    .collect(Collectors.toList());

            return buildSuccessResponse("Usuários recuperados com sucesso", Map.of("users", userList));

        } catch (IllegalArgumentException e) {
            return buildBadRequestResponse("Tipo de usuário inválido");
        }
    }

    // Editar usuário
    @PutMapping("/edit/{userId}")
    public ResponseEntity<Map<String, Object>> editUser(@PathVariable String userId,
                                                        @RequestBody Map<String, Object> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<User> targetUserOpt = userRepository.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return buildNotFoundResponse("Usuário não encontrado");
        }

        User targetUser = targetUserOpt.get();

        if (!canEditUser(currentUser.getUserType(), targetUser.getUserType(), currentUser.getId(),
                targetUser.getId())) {
            return buildForbiddenResponse("Você não tem permissão para editar este usuário");
        }

        boolean updated = false;
        Map<String, String> validationErrors = new HashMap<>();

        if (updates.containsKey("username")) {
            String newUsername = (String) updates.get("username");
            if (newUsername != null && !newUsername.trim().isEmpty()) {
                if (newUsername.length() < 3 || newUsername.length() > 50) {
                    validationErrors.put("username", "Username deve ter entre 3 e 50 caracteres");
                } else {
                    targetUser.setUsername(newUsername.trim());
                    updated = true;
                }
            }
        }

        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                if (userRepository.existsByEmail(newEmail) && !targetUser.getEmail().equals(newEmail)) {
                    validationErrors.put("email", "Email já está em uso");
                } else {
                    targetUser.setEmail(newEmail.trim());
                    updated = true;
                }
            }
        }

        if (updates.containsKey("password")) {
            String newPassword = (String) updates.get("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (newPassword.length() < 8 || newPassword.length() > 100) {
                    validationErrors.put("password", "Password deve ter entre 8 e 100 caracteres");
                } else {
                    targetUser.setPassword(passwordEncoder.encode(newPassword));
                    updated = true;
                }
            }
        }

        if (updates.containsKey("userType")) {
            String newUserTypeStr = (String) updates.get("userType");
            if (newUserTypeStr != null) {
                try {
                    UserType newUserType = UserType.valueOf(newUserTypeStr.toUpperCase());
                    if (!canChangeUserType(currentUser.getUserType(), targetUser.getUserType(), newUserType)) {
                        validationErrors.put("userType", "Você não tem permissão para alterar para este tipo de usuário");
                    } else {
                        targetUser.setUserType(newUserType);
                        updated = true;
                    }
                } catch (IllegalArgumentException e) {
                    validationErrors.put("userType", "Tipo de usuário inválido");
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            return buildBadRequestResponse("Erros de validação", validationErrors);
        }

        if (!updated) {
            return buildBadRequestResponse("Nenhum campo válido para atualização");
        }

        userRepository.save(targetUser);

        Map<String, Object> response = new HashMap<>();
        response.put("id", targetUser.getId());
        response.put("username", targetUser.getUsername());
        response.put("email", targetUser.getEmail());
        response.put("userType", targetUser.getUserType());

        return buildSuccessResponse("Usuário atualizado com sucesso", response);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<User> targetUserOpt = userRepository.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return buildNotFoundResponse("Usuário não encontrado");
        }

        User targetUser = targetUserOpt.get();

        if (currentUser.getId().equals(targetUser.getId())) {
            return buildBadRequestResponse("Não é possível deletar sua própria conta");
        }

        if (!targetUser.getSquads().isEmpty()) {
            return buildBadRequestResponse("O usuário está em uma ou mais squads. Remova-o das squads antes de deletar.");
        }

        if (!canDeleteUser(currentUser.getUserType(), targetUser.getUserType())) {
            return buildForbiddenResponse("Você não tem permissão para deletar este usuário");
        }

        userRepository.delete(targetUser);

        return buildSuccessResponse("Usuário deletado com sucesso",
                Map.of("deletedUserId", userId));
    }

    // Métodos auxiliares de resposta
    private ResponseEntity<Map<String, Object>> buildSuccessResponse(String message, Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.putAll(data);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> buildBadRequestResponse(String errorMessage) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", errorMessage));
    }

    private ResponseEntity<Map<String, Object>> buildBadRequestResponse(String errorMessage, Map<String, String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", errorMessage);
        response.put("validationErrors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> buildForbiddenResponse(String errorMessage) {
        return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "error", errorMessage));
    }

    private ResponseEntity<Map<String, Object>> buildNotFoundResponse(String errorMessage) {
        return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "error", errorMessage));
    }

    // Permissão (Editar)
    private boolean canEditUser(UserType currentUserType, UserType targetUserType, String currentUserId,
            String targetUserId) {
        // Permite editar a própria conta (exceto userType)
        if (currentUserId.equals(targetUserId)) {
            return true;
        }

        switch (currentUserType) {
            case ADMIN:
                // Admin pode editar GERENTE e COLABORADOR, mas não outros ADMINs
                return targetUserType == UserType.GERENTE || targetUserType == UserType.COLABORADOR;

            default:
                return false;
        }
    }

    // Permissão (Alterar UserType)
    private boolean canChangeUserType(UserType currentUserType, UserType currentTargetType, UserType newTargetType) {
        switch (currentUserType) {
            case ADMIN:
                // Admin pode alterar GERENTE <-> COLABORADOR, mas não pode criar/alterar ADMIN
                return (currentTargetType == UserType.GERENTE || currentTargetType == UserType.COLABORADOR) &&
                        (newTargetType == UserType.GERENTE || newTargetType == UserType.COLABORADOR);
            default:
                return false;
        }
    }

    // Permissão (Deletar)
    private boolean canDeleteUser(UserType currentUserType, UserType targetUserType) {
        switch (currentUserType) {
            case ADMIN:
                // Admin pode deletar GERENTE e COLABORADOR, mas não outros ADMINs
                return targetUserType == UserType.GERENTE || targetUserType == UserType.COLABORADOR;

            case GERENTE:
                // Gerente pode deletar apenas COLABORADOR
                return targetUserType == UserType.COLABORADOR;

            default:
                return false;
        }
    }

    // Permissão (Visualização)
    private boolean canViewUserType(UserType currentUserType, UserType targetUserType) {
        switch (currentUserType) {
            case ADMIN:
                // Admin pode ver todos os tipos de usuário
                return true;

            case GERENTE:
                // Gerente pode ver GERENTE e COLABORADOR
                return targetUserType == UserType.GERENTE || targetUserType == UserType.COLABORADOR;

            case COLABORADOR:
                // Colaborador pode ver apenas COLABORADOR
                return targetUserType == UserType.COLABORADOR;

            default:
                return false;
        }
    }

    // Permissões
    private List<User> filterUsersByPermission(UserType currentUserType, List<User> allUsers) {
        switch (currentUserType) {
            case ADMIN:
                // Admin pode ver todos os tipos de usuário
                return allUsers;

            case GERENTE:
                return allUsers.stream()
                        .filter(user -> user.getUserType() == UserType.GERENTE ||
                                user.getUserType() == UserType.COLABORADOR)
                        .collect(Collectors.toList());

            case COLABORADOR:
                // Colaborador pode ver apenas COLABORADOR
                return allUsers.stream()
                        .filter(user -> user.getUserType() == UserType.GERENTE ||
                                user.getUserType() == UserType.COLABORADOR)
                        .collect(Collectors.toList());

            default:
                return List.of();
        }
    }
}