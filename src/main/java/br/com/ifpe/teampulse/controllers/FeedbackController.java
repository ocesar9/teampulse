package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.Feedback;
import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import br.com.ifpe.teampulse.dto.FeedbackRequest;
import br.com.ifpe.teampulse.dto.FeedbackUpdateRequest;
import br.com.ifpe.teampulse.repository.FeedbackRepository;
import br.com.ifpe.teampulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    // Enviar feedback (apenas gerente)
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Verifica se o usuário atual pode enviar feedback
        if (!canSendFeedback(currentUser.getUserType())) {
            return buildForbiddenResponse("Apenas gerentes podem enviar feedbacks");
        }

        Optional<User> targetUserOpt = userRepository.findById(feedbackRequest.getUserId());
        if (targetUserOpt.isEmpty()) {
            return buildNotFoundResponse("Usuário não encontrado");
        }

        User targetUser = targetUserOpt.get();

        // Verifica se o usuário alvo pode receber feedback
        if (!canReceiveFeedback(targetUser.getUserType())) {
            return buildForbiddenResponse("Feedback só pode ser enviado para colaboradores");
        }

        // Valida o rating
        if (feedbackRequest.getRating() < 1 || feedbackRequest.getRating() > 5) {
            return buildBadRequestResponse("Rating deve ser entre 1 e 5");
        }

        // Cria e salva o feedback
        Feedback feedback = new Feedback();
        feedback.setComment(feedbackRequest.getComment());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setUser(targetUser);
        feedback.setAuthor(currentUser);
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return buildSuccessResponse(
                "Feedback enviado com sucesso",
                Map.of(
                        "feedbackId", savedFeedback.getId(),
                        "userId", targetUser.getId(),
                        "authorId", currentUser.getId()
                )
        );
    }

    // Listar feedbacks de um usuário (Gerente + Quem Recebe)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFeedbacks(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<User> targetUserOpt = userRepository.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return buildNotFoundResponse("Usuário não encontrado");
        }

        User targetUser = targetUserOpt.get();

        if (!canViewFeedbacks(currentUser, targetUser)) {
            return buildForbiddenResponse("Você não tem permissão para visualizar estes feedbacks");
        }

        List<Feedback> feedbacks = feedbackRepository.findByUser(targetUser);

        if (feedbacks.isEmpty()) {
            return buildSuccessResponse(
                    "Nenhum feedback encontrado para este usuário",
                    Map.of(
                            "userId", userId,
                            "username", targetUser.getUsername(),
                            "feedbacks", List.of()
                    )
            );
        }

        List<Map<String, Object>> feedbackList = feedbacks.stream()
                .map(this::convertFeedbackToMap)
                .collect(Collectors.toList());

        return buildSuccessResponse(
                "Feedbacks encontrados",
                Map.of(
                        "userId", userId,
                        "username", targetUser.getUsername(),
                        "feedbacks", feedbackList
                )
        );
    }


    // Métodos auxiliares de verificação de permissão
    private boolean canSendFeedback(UserType userType) {
        return userType == UserType.GERENTE;
    }

    private boolean canReceiveFeedback(UserType userType) {
        return userType == UserType.COLABORADOR;
    }

    private boolean canViewFeedbacks(User currentUser, User targetUser) {
        // O próprio usuário pode ver seus feedbacks
        if (currentUser.getId().equals(targetUser.getId())) {
            return true;
        }


        // Gerente pode ver feedbacks de seus colaboradores
        return currentUser.getUserType() == UserType.GERENTE &&
                targetUser.getUserType() == UserType.COLABORADOR;
    }

    // Métodos auxiliares para construção de respostas
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

    private Map<String, Object> convertFeedbackToMap(Feedback feedback) {
        Map<String, Object> feedbackMap = new HashMap<>();
        feedbackMap.put("id", feedback.getId());
        feedbackMap.put("comment", feedback.getComment());
        feedbackMap.put("rating", feedback.getRating());
        feedbackMap.put("createdAt", feedback.getCreatedAt());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", feedback.getUser().getId());
        userMap.put("username", feedback.getUser().getUsername());
        feedbackMap.put("user", userMap);

        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("id", feedback.getAuthor().getId());
        authorMap.put("username", feedback.getAuthor().getUsername());
        feedbackMap.put("author", authorMap);

        return feedbackMap;
    }
}