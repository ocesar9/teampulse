package br.com.ifpe.teampulse.controllers;

import br.com.ifpe.teampulse.domain.user.Feedback;
import br.com.ifpe.teampulse.domain.user.FeedbackStatus;
import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import br.com.ifpe.teampulse.dto.FeedbackRequest;
import br.com.ifpe.teampulse.dto.FeedbackSendRequest;
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

    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> createDraftFeedback(
            @Valid @RequestBody FeedbackRequest feedbackRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!canSendFeedback(currentUser.getUserType())) {
            return buildForbiddenResponse("Apenas gerentes podem criar rascunhos de feedback");
        }

        Optional<User> targetUserOpt = userRepository.findById(feedbackRequest.getUserId());
        if (targetUserOpt.isEmpty()) {
            return buildNotFoundResponse("Usuário não encontrado");
        }

        User targetUser = targetUserOpt.get();

        if (!canReceiveFeedback(targetUser.getUserType())) {
            return buildForbiddenResponse("Rascunhos só podem ser criados para colaboradores");
        }

        Feedback draft = new Feedback();
        draft.setComment(feedbackRequest.getComment());
        draft.setRating(feedbackRequest.getRating());
        draft.setUser(targetUser);
        draft.setAuthor(currentUser);
        draft.setCreatedAt(LocalDateTime.now());
        draft.setStatus(FeedbackStatus.DRAFT);

        Feedback savedDraft = feedbackRepository.save(draft);

        return buildSuccessResponse(
                "Rascunho de feedback salvo com sucesso",
                Map.of(
                        "draftId", savedDraft.getId(),
                        "userId", targetUser.getId(),
                        "authorId", currentUser.getId(),
                        "status", "DRAFT"));
    }

    @PutMapping("/draft/{feedbackId}")
    public ResponseEntity<Map<String, Object>> updateDraft(@PathVariable String feedbackId,
                                                           @Valid @RequestBody FeedbackRequest feedbackRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            return buildNotFoundResponse("Rascunho não encontrado");
        }

        Feedback draft = feedbackOpt.get();

        if (!draft.getAuthor().getId().equals(currentUser.getId())) {
            return buildForbiddenResponse("Apenas o autor pode editar o rascunho");
        }

        if (draft.getStatus() != FeedbackStatus.DRAFT) {
            return buildBadRequestResponse("Apenas rascunhos podem ser editados");
        }

        draft.setComment(feedbackRequest.getComment());
        draft.setRating(feedbackRequest.getRating());
        draft.setUpdatedAt(LocalDateTime.now());

        Feedback updatedDraft = feedbackRepository.save(draft);

        return buildSuccessResponse(
                "Rascunho atualizado com sucesso",
                Map.of(
                        "draftId", updatedDraft.getId(),
                        "comment", updatedDraft.getComment(),
                        "rating", updatedDraft.getRating(),
                        "updatedAt", updatedDraft.getUpdatedAt()));
    }

    @DeleteMapping("/draft/{feedbackId}")
    public ResponseEntity<Map<String, Object>> deleteDraft(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            return buildNotFoundResponse("Rascunho não encontrado");
        }

        Feedback draft = feedbackOpt.get();

        if (!draft.getAuthor().getId().equals(currentUser.getId())) {
            return buildForbiddenResponse("Apenas o autor pode deletar o rascunho");
        }

        if (draft.getStatus() != FeedbackStatus.DRAFT) {
            return buildBadRequestResponse("Apenas rascunhos podem ser deletados");
        }

        feedbackRepository.delete(draft);

        return buildSuccessResponse(
                "Rascunho deletado com sucesso",
                Map.of(
                        "deletedId", feedbackId,
                        "timestamp", LocalDateTime.now()));
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendFeedback(@Valid @RequestBody FeedbackSendRequest sendRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Feedback> feedbackOpt = feedbackRepository.findById(sendRequest.getFeedbackId());
        if (feedbackOpt.isEmpty()) {
            return buildNotFoundResponse("Feedback não encontrado");
        }

        Feedback feedback = feedbackOpt.get();

        if (!feedback.getAuthor().getId().equals(currentUser.getId())) {
            return buildForbiddenResponse("Apenas o autor pode enviar o feedback");
        }

        if (feedback.getStatus() != FeedbackStatus.DRAFT) {
            return buildBadRequestResponse("Apenas rascunhos podem ser enviados");
        }

        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            return buildBadRequestResponse("Rating deve ser entre 1 e 5");
        }

        feedback.setStatus(FeedbackStatus.SENT);
        feedback.setSentAt(LocalDateTime.now());

        Feedback sentFeedback = feedbackRepository.save(feedback);

        return buildSuccessResponse(
                "Feedback enviado com sucesso",
                Map.of(
                        "feedbackId", sentFeedback.getId(),
                        "status", "SENT",
                        "sentAt", sentFeedback.getSentAt()));
    }

    @GetMapping("/drafts")
    public ResponseEntity<Map<String, Object>> getUserDrafts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<Feedback> drafts = feedbackRepository.findByAuthorAndStatus(
                currentUser,
                FeedbackStatus.DRAFT);

        List<Map<String, Object>> draftList = drafts.stream()
                .map(this::convertFeedbackToMap)
                .collect(Collectors.toList());

        return buildSuccessResponse(
                "Rascunhos encontrados",
                Map.of(
                        "drafts", draftList,
                        "count", draftList.size()));
    }

    @GetMapping("/received")
    public ResponseEntity<Map<String, Object>> getReceivedFeedbacks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<Feedback> feedbacks = feedbackRepository.findByUserAndStatus(
                currentUser,
                FeedbackStatus.SENT);

        return buildFeedbackListResponse(feedbacks, "Feedbacks recebidos");
    }

    @GetMapping("/sent")
    public ResponseEntity<Map<String, Object>> getSentFeedbacks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<Feedback> feedbacks = feedbackRepository.findByAuthorAndStatus(
                currentUser,
                FeedbackStatus.SENT);

        return buildFeedbackListResponse(feedbacks, "Feedbacks enviados");
    }

    private boolean canSendFeedback(UserType userType) {
        return userType == UserType.GERENTE;
    }

    private boolean canReceiveFeedback(UserType userType) {
        return userType == UserType.COLABORADOR;
    }

    private ResponseEntity<Map<String, Object>> buildFeedbackListResponse(
            List<Feedback> feedbacks, String message) {

        if (feedbacks.isEmpty()) {
            return buildSuccessResponse(
                    "Nenhum feedback encontrado",
                    Map.of("feedbacks", List.of()));
        }

        List<Map<String, Object>> feedbackList = feedbacks.stream()
                .map(this::convertFeedbackToMap)
                .collect(Collectors.toList());

        return buildSuccessResponse(
                message,
                Map.of("feedbacks", feedbackList));
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
                "error", errorMessage));
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

    private Map<String, Object> convertFeedbackToMap(Feedback feedback) {
        Map<String, Object> feedbackMap = new HashMap<>();
        feedbackMap.put("id", feedback.getId());
        feedbackMap.put("comment", feedback.getComment());
        feedbackMap.put("rating", feedback.getRating());
        feedbackMap.put("createdAt", feedback.getCreatedAt());
        feedbackMap.put("status", feedback.getStatus().toString());

        if (feedback.getSentAt() != null) {
            feedbackMap.put("sentAt", feedback.getSentAt());
        }

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