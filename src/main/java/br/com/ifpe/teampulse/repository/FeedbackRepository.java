package br.com.ifpe.teampulse.repository;

import br.com.ifpe.teampulse.domain.user.Feedback;
import br.com.ifpe.teampulse.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    List<Feedback> findByUser(User user);
    List<Feedback> findByAuthor(User author);
}