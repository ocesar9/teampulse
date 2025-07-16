package br.com.ifpe.teampulse.repository;

import br.com.ifpe.teampulse.domain.user.Feedback;
import br.com.ifpe.teampulse.domain.user.FeedbackStatus;
import br.com.ifpe.teampulse.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    // Para /feedback/received
    List<Feedback> findByUserAndStatus(User user, FeedbackStatus status);

    // Para /feedback/sent
    List<Feedback> findByAuthorAndStatus(User author, FeedbackStatus status);

}