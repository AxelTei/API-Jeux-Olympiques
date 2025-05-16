package com.jo.api.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Schema(name = "Booking", description = "Représente une réservation effectuée par un utilisateur")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de la réservation", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long bookingId;

    @Column(nullable = false)
    @Schema(description = "Titre de l'offre de réservation", example = "Offre Solo", required = true)
    private String bookingOfferTitle;

    @Column(nullable = false)
    @Schema(description = "Prix total de la réservation", example = "149.99", required = true)
    private BigDecimal price;

    @Column(nullable = false)
    @Schema(description = "Clé unique de l'utilisateur qui a effectué la réservation", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890", required = true)
    private String userKey;

    @Column(nullable = false)
    @Schema(description = "Nombre de personnes incluses dans la réservation", example = "2", required = true, minimum = "1")
    private Integer numberOfGuests;
}
