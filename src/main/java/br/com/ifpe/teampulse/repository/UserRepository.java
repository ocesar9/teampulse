package br.com.ifpe.teampulse.repository;


import br.com.ifpe.teampulse.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
