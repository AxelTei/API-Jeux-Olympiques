package com.jo.api.security.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
@Getter
@Setter
@Schema(name = "User", description = "Représente un utilisateur du système avec ses informations d'authentification et ses rôles")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false,unique = true)
    @Schema(description = "Clé unique générée pour l'utilisateur, utilisée pour les références externes",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String userKey;

    @NotBlank
    @Email
    @Size(min = 3, max=50)
    @Column(nullable = false, unique = true)
    @Schema(description = "Adresse email de l'utilisateur, utilisée comme identifiant de connexion",
            example = "john.doe@example.com",
            required = true,
            minLength = 3,
            maxLength = 50)
    private String username;

    @NotBlank
    @Column(nullable = false,unique = true)
    @Schema(description = "Nom d'affichage unique de l'utilisateur",
            example = "JohnD",
            required = true)
    private String alias;

    @NotBlank
    @Size(max=120)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,}", message = "Le mot de passe doit contenir 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.")
    @Schema(description = "Mot de passe de l'utilisateur (haché en base de données). " +
            "Doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial (@, #, $, %).",
            example = "SecureP@ss123",
            required = true,
            maxLength = 120,
            accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    @Schema(description = "Ensemble des rôles attribués à l'utilisateur",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password, String alias, String userKey) {
        this.username = username;
        this.password = password;
        this.alias = alias;
        this.userKey = userKey;
    }

    public User() {

    }
}
