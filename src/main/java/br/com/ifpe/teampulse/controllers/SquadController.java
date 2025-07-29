package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.*;
import br.com.ifpe.teampulse.dto.SquadRequest;
import br.com.ifpe.teampulse.repository.SquadRepository;
import br.com.ifpe.teampulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/squads")
@RequiredArgsConstructor
public class SquadController {
    private final SquadRepository squadRepository;
    private final UserRepository userRepository;

    // Cria uma nova squad
    @PostMapping
    public ResponseEntity<?> createSquad(
            Authentication authentication,
            @Validated(SquadRequest.CreateGroup.class) @RequestBody SquadRequest request) {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.isGerente()) {
            return buildForbiddenResponse("Apenas gerentes podem criar squads");
        }

        if (squadRepository.existsByName(request.getName())) {
            return buildBadRequestResponse("Nome da squad já existe");
        }

        Squad squad = new Squad();
        squad.setName(request.getName());

        try {
            for (String memberId : request.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + memberId));
                squad.addMember(member);
            }
            squad.validateComposition();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return buildBadRequestResponse(e.getMessage());
        }

        Squad savedSquad = squadRepository.save(squad);
        return buildSuccessResponse("Squad criada com sucesso", savedSquad.toResponseMap());
    }

    // Atualiza os membros de uma squad
    @PutMapping("/members")
    public ResponseEntity<?> updateMembers(
            @Validated(SquadRequest.UpdateGroup.class) @RequestBody SquadRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.isGerente()) {
            return buildForbiddenResponse("Acesso negado");
        }

        Squad squad = squadRepository.findById(request.getSquadId())
                .orElseThrow(() -> new IllegalArgumentException("Squad não encontrada"));

        try {
            // Operação de remoção
            if (request.getMembersToRemove() != null) {
                for (String memberId : request.getMembersToRemove()) {
                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + memberId));

                    if (!squad.getMembers().contains(member)) {
                        throw new IllegalArgumentException("Usuário não é membro desta squad: " + memberId);
                    }

                    squad.getMembers().remove(member);
                    member.getSquads().remove(squad);
                }
            }

            // Operação de adição
            if (request.getMemberIds() != null) {
                for (String memberId : request.getMemberIds()) {
                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + memberId));
                    squad.addMember(member);
                }
            }

            squad.validateComposition();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return buildBadRequestResponse(e.getMessage());
        }

        Squad updated = squadRepository.save(squad);
        return buildSuccessResponse("Membros atualizados", updated.toResponseMap());
    }


    // Remove uma squad
    @DeleteMapping("/{squadId}")
    public ResponseEntity<?> deleteSquad(
            Authentication authentication,
            @PathVariable String squadId) {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.isGerente()) {
            return buildForbiddenResponse("Acesso negado");
        }

        Squad squad = squadRepository.findById(squadId)
                .orElse(null);

        if (squad == null) {
            return buildNotFoundResponse("Squad não existe");
        }

        squad.getMembers().forEach(m -> m.getSquads().remove(squad));
        squadRepository.delete(squad);

        return buildSuccessResponse("Squad removida",
                Map.of("id", squadId, "data", LocalDateTime.now()));
    }

    // Lista todas as squads
    @GetMapping
    public ResponseEntity<?> getAllSquads(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.isGerente()) {
            return buildForbiddenResponse("Apenas gerentes podem listar todas as squads");
        }

        List<Squad> squads = squadRepository.findAll();

        if (squads.isEmpty()) {
            return buildSuccessResponse("Nenhuma squad encontrada",
                    Map.of("squads", List.of(), "total", 0));
        }

        List<Map<String, Object>> response = squads.stream()
                .map(Squad::toResponseMap)
                .collect(Collectors.toList());

        return buildSuccessResponse("Squads listadas com sucesso",
                Map.of("squads", response, "total", response.size()));
    }

    // Lista a squad de um colaborador específico
    @GetMapping("/colaborador/{userId}")
    public ResponseEntity<?> getSquadsByCollaborator(
            @PathVariable String userId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getId().equals(userId) && !currentUser.isGerente()) {
            return buildForbiddenResponse("Acesso não autorizado");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!user.isColaborador()) {
            return buildBadRequestResponse("O usuário não é um colaborador");
        }

        List<Squad> squads = squadRepository.findByCollaboratorId(userId);

        if (squads.isEmpty()) {
            return buildSuccessResponse("Nenhuma squad encontrada para este colaborador",
                    Map.of("squads", List.of(), "total", 0));
        }

        List<Map<String, Object>> response = squads.stream()
                .map(Squad::toResponseMap)
                .collect(Collectors.toList());

        return buildSuccessResponse("Squads do colaborador",
                Map.of("squads", response, "total", response.size()));
    }


    // Lista todas as squads que um gerente participa
    @GetMapping("/gerente/{userId}")
    public ResponseEntity<?> getSquadsByManager(
            @PathVariable String userId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getId().equals(userId) && !currentUser.isGerente()) {
            return buildForbiddenResponse("Acesso não autorizado");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!user.isGerente()) {
            return buildBadRequestResponse("O usuário não é um gerente");
        }

        List<Squad> squads = squadRepository.findByManagerId(userId);

        if (squads.isEmpty()) {
            return buildSuccessResponse("Nenhuma squad encontrada para este gerente",
                    Map.of("squads", List.of(), "total", 0));
        }

        List<Map<String, Object>> response = squads.stream()
                .map(squad -> {
                    Map<String, Object> squadMap = squad.toResponseMap();
                    squadMap.put("papel", "Gerente");
                    return squadMap;
                })
                .collect(Collectors.toList());

        return buildSuccessResponse("Squads do gerente",
                Map.of("squads", response, "total", response.size()));
    }

    // Métodos auxiliares para respostas padronizadas
    private Map<String, Object> convertSquadToMap(Squad squad) {
        Map<String, Object> squadMap = new HashMap<>();
        squadMap.put("id", squad.getId());
        squadMap.put("nome", squad.getName());
        squadMap.put("dataCriacao", squad.getCreatedAt());

        // Composição completa do time
        Map<String, Object> composicao = new HashMap<>();
        composicao.put("totalMembros", squad.getMembers().size());
        composicao.put("totalGerentes", squad.countManagers());
        composicao.put("totalColaboradores", squad.countCollaborators());

        // Lista detalhada de membros
        List<Map<String, Object>> membros = squad.getMembers().stream()
                .map(membro -> {
                    Map<String, Object> membroMap = new HashMap<>();
                    membroMap.put("id", membro.getId());
                    membroMap.put("nome", membro.getUsername());
                    membroMap.put("email", membro.getEmail());
                    membroMap.put("tipo", membro.getUserType().toString());
                    membroMap.put("papel", membro.isGerente() ? "Gerente" : "Colaborador");
                    return membroMap;
                })
                .collect(Collectors.toList());

        composicao.put("membros", membros);
        squadMap.put("composicao", composicao);

        return squadMap;
    }


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
                "error", errorMessage
        ));
    }


    private ResponseEntity<Map<String, Object>> buildForbiddenResponse(String errorMessage) {
        return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "error", errorMessage
        ));
    }


    private ResponseEntity<Map<String, Object>> buildNotFoundResponse(String errorMessage) {
        return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "error", errorMessage
        ));
    }
}