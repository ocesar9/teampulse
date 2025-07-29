package br.com.ifpe.teampulse.repository;

import br.com.ifpe.teampulse.domain.user.Squad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SquadRepository extends JpaRepository<Squad, String> {

    // Consulta básica por nome
    Optional<Squad> findByName(String name);
    boolean existsByName(String name);

    // Busca squads onde o usuário é membro
    @Query("SELECT s FROM Squad s JOIN s.members m WHERE m.id = :userId")
    List<Squad> findByMemberId(@Param("userId") String userId);

    // Busca squads onde o usuário é membro colaborador
    @Query("SELECT s FROM Squad s JOIN s.members m WHERE m.id = :userId AND m.userType = 'COLABORADOR'")
    List<Squad> findByCollaboratorId(@Param("userId") String userId);

    // Busca squads onde o usuário é membro gerente
    @Query("SELECT DISTINCT s FROM Squad s JOIN s.members m WHERE m.id = :userId AND m.userType = 'GERENTE'")
    List<Squad> findByManagerId(@Param("userId") String userId);

}
