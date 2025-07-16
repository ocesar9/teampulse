package br.com.ifpe.teampulse.repository;

import br.com.ifpe.teampulse.domain.user.User;
import br.com.ifpe.teampulse.domain.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuários por tipo
     */
    List<User> findByUserType(UserType userType);

    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);

}
