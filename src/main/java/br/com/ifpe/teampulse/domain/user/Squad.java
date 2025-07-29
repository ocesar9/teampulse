package br.com.ifpe.teampulse.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity representing a Squad (team) in the system.
 * Contains validation logic for squad composition rules.
 */
@Entity
@Table(name = "squads")
@Getter
@Setter
@NoArgsConstructor
public class Squad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "O nome da squad não pode estar vazio")
    @Column(unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "squad_members",
            joinColumns = @JoinColumn(name = "squad_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addMember(User user) {
        validateMemberAddition(user);
        members.add(user);
        user.getSquads().add(this);
    }

    private void validateMemberAddition(User user) {
        if (members.size() >= 8) {
            throw new IllegalStateException("Squad já possui 8 membros");
        }

        if (user.isColaborador()) {
            validateCollaboratorAddition(user);
        } else if (user.isGerente()) {
            validateManagerAddition();
        } else {
            throw new IllegalStateException("ADMIN não pode ser membro de squad");
        }
    }

    private void validateManagerAddition() {
        long managerCount = countManagers();

        if (managerCount >= 2) {
            throw new IllegalStateException("Squad pode ter no máximo 2 gerentes");
        }

        if (managerCount == 1 && countCollaborators() < 6) {
            throw new IllegalStateException("Segundo gerente só pode ser adicionado com 6+ colaboradores");
        }
    }

    private void validateCollaboratorAddition(User user) {
        if (user.getSquads().stream().anyMatch(s -> !s.getId().equals(this.id))) {
            throw new IllegalStateException("Colaborador já está em outra squad");
        }

        if (countCollaborators() >= 7) {
            throw new IllegalStateException("Squad pode ter no máximo 7 colaboradores");
        }

        if (countManagers() == 2 && countCollaborators() >= 6) {
            throw new IllegalStateException("Com 2 gerentes, squad pode ter no máximo 6 colaboradores");
        }
    }


    public void validateComposition() {
        if (members.size() < 3) {
            throw new IllegalStateException("Uma squad deve ter no mínimo 3 membros");
        }

        long managerCount = countManagers();
        long collaboratorCount = countCollaborators();

        if (managerCount < 1) {
            throw new IllegalStateException("Uma squad deve ter pelo menos 1 gerente");
        }

        if (managerCount > 2) {
            throw new IllegalStateException("Uma squad pode ter no máximo 2 gerentes");
        }

        if (collaboratorCount > 7) {
            throw new IllegalStateException("Uma squad pode ter no máximo 7 colaboradores");
        }

        if (managerCount == 2 && collaboratorCount < 6) {
            throw new IllegalStateException("2 gerentes só são permitidos com 6 ou mais colaboradores");
        }

        members.stream()
                .filter(User::isColaborador)
                .forEach(colab -> {
                    if (colab.getSquads().size() > 1) {
                        throw new IllegalStateException(
                                "Colaborador " + colab.getUsername() + " já está em outra squad");
                    }
                });
    }

    public long countManagers() {
        return members.stream()
                .filter(User::isGerente)
                .count();
    }

    public long countCollaborators() {
        return members.stream()
                .filter(User::isColaborador)
                .count();
    }

    public void removeMember(User user) {
        if (!this.members.contains(user)) {
            throw new IllegalArgumentException("Usuário não é membro desta squad");
        }

        // Validações específicas para remoção (se necessário)
        if (user.isGerente() && this.countManagers() <= 1) {
            throw new IllegalStateException("Não pode remover o único gerente da squad");
        }

        this.members.remove(user);
        user.getSquads().remove(this);
    }

    public Map<String, Object> toResponseMap() {
        Map<String, Object> squadMap = new HashMap<>();
        squadMap.put("id", this.id);
        squadMap.put("nome", this.name);
        squadMap.put("dataCriacao", this.createdAt);

        Map<String, Object> composicao = new HashMap<>();
        composicao.put("totalMembros", this.members.size());
        composicao.put("totalGerentes", this.countManagers());
        composicao.put("totalColaboradores", this.countCollaborators());

        List<Map<String, Object>> membros = this.members.stream()
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
}