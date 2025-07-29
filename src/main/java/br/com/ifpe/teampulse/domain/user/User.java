package br.com.ifpe.teampulse.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 12)
    private UserType userType;

    @ManyToMany(mappedBy = "members")
    private Set<Squad> squads = new HashSet<>();

    public boolean isGerente() {
        return userType == UserType.GERENTE;
    }

    public boolean isColaborador() {
        return userType == UserType.COLABORADOR;
    }
}
