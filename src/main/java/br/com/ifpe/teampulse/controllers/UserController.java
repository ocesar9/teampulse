package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import br.com.ifpe.teampulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Listar (todos)
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getUserList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<User> allUsers = userRepository.findAll();
        // Filtra os usuários baseado nas permissões do usuário atual
        List<User> filteredUsers = filterUsersByPermission(currentUser.getUserType(), allUsers);

        // Converte para formato de resposta
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

        return ResponseEntity.ok(userList);
    }

    // Contador (todos)
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<User> allUsers = userRepository.findAll();
        // Filtra os usuários baseado nas permissões do usuário atual
        List<User> filteredUsers = filterUsersByPermission(currentUser.getUserType(), allUsers);

        // Conta usuários por tipo
        Map<UserType, Long> countByType = filteredUsers.stream()
                .collect(Collectors.groupingBy(User::getUserType, Collectors.counting()));

        // Converte para formato de resposta - APENAS os campos que o usuário pode ver
        Map<String, Object> response = new HashMap<>();

        // Adiciona apenas os campos baseado nas permissões do usuário atual
        switch (currentUser.getUserType()) {
            case ADMIN:
                // Admin pode ver todos os tipos
                response.put("admin", countByType.getOrDefault(UserType.ADMIN, 0L));
                response.put("gerente", countByType.getOrDefault(UserType.GERENTE, 0L));
                response.put("colaborador", countByType.getOrDefault(UserType.COLABORADOR, 0L));
                response.put("total", filteredUsers.size());
                break;

            case GERENTE:
                // Gerente pode ver apenas GERENTE e COLABORADOR
                response.put("gerente", countByType.getOrDefault(UserType.GERENTE, 0L));
                response.put("colaborador", countByType.getOrDefault(UserType.COLABORADOR, 0L));
                break;

            case COLABORADOR:
                // Colaborador pode ver apenas COLABORADOR
                response.put("colaborador", countByType.getOrDefault(UserType.COLABORADOR, 0L));
                break;
        }

        return ResponseEntity.ok(response);
    }


    // Listar (UserType)
    @GetMapping("/by-type/{userType}")
    public ResponseEntity<List<Map<String, Object>>> getUsersByType(@PathVariable String userType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        try {
            UserType requestedType = UserType.valueOf(userType.toUpperCase());

            // Verifica se o usuário atual tem permissão para ver este tipo de usuário
            if (!canViewUserType(currentUser.getUserType(), requestedType)) {
                return ResponseEntity.status(403).build();
            }

            // Busca usuários do tipo específico
            List<User> users = userRepository.findByUserType(requestedType);

            // Converte para formato de resposta (sem senha)
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

            return ResponseEntity.ok(userList);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // Deletar
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<User> targetUserOpt = userRepository.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User targetUser = targetUserOpt.get();

        // Não permite deletar a própria conta
        if (currentUser.getId().equals(targetUser.getId())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Não é possível deletar sua própria conta");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Verifica permissão para deletar
        if (!canDeleteUser(currentUser.getUserType(), targetUser.getUserType())) {
            return ResponseEntity.status(403).build();
        }

        userRepository.delete(targetUser);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuário deletado com sucesso");
        response.put("deletedUserId", userId);

        return ResponseEntity.ok(response);
    }


    // Permissão (Editar)
    private boolean canEditUser(UserType currentUserType, UserType targetUserType, Long currentUserId, Long targetUserId) {
        // Permite editar a própria conta (exceto userType)
        if (currentUserId.equals(targetUserId)) {
            return true;
        }

        switch (currentUserType) {
            case ADMIN:
                // Admin pode editar GERENTE e COLABORADOR, mas não outros ADMINs
                return targetUserType == UserType.GERENTE || targetUserType == UserType.COLABORADOR;

            case GERENTE:
                // Gerente pode editar apenas COLABORADOR
                return targetUserType == UserType.COLABORADOR;

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