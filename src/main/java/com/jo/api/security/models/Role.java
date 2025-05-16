package com.jo.api.security.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Entity
@Table(name="roles")
@Getter
@Setter
@Schema(name = "Role", description = "Représente un rôle d'utilisateur dans le système qui détermine les autorisations d'accès")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du rôle", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Schema(description = "Nom du rôle, défini par l'énumération ERole (ROLE_ADMIN, ROLE_CUSTOMER, etc.)",
            example = "ROLE_ADMIN",
            required = true)
    private ERole name;

    @ManyToMany(mappedBy = "roles")
    @Schema(description = "Utilisateurs ayant ce rôle",
            accessMode = Schema.AccessMode.READ_ONLY,
            hidden = true)
    private Collection<User> users;

    public Role(ERole name) {
        this.name = name;
    }

    public Role() {

    }
}
