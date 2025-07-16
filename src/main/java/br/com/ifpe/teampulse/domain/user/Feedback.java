package br.com.ifpe.teampulse.domain.user;



import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "feedback_id", updatable = false, nullable = false, length = 36)
    private String id;

    @NotBlank(message = "Comentário não pode ser vazio")
    @Column(name = "feedback_comment", nullable = false, length = 500)
    private String comment;

    @Min(value = 1, message = "Rating deve ser no mínimo 1")
    @Max(value = 5, message = "Rating deve ser no máximo 5")
    @Column(name = "feedback_rating", nullable = false)
    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_status", nullable = false, length = 20)
    private FeedbackStatus status = FeedbackStatus.SENT;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_user"))
    private User user; // Avaliado (colaborador)

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_author"))
    private User author; // Gerente que enviou o feedback

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}