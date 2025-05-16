package com.jo.api.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Schema(name = "Ticket", description = "Représente un ticket d'entrée pour un événement associé à une réservation")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du ticket", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long ticketId;

    @Schema(description = "Nom d'utilisateur(email) du détenteur du ticket", example = "john.doe@example.com")
    private String username;

    @Schema(description = "Titre de l'offre de réservation associée au ticket", example = "Offre Duo")
    private String offerTitle;

    @Schema(description = "Nom de l'événement pour lequel le ticket est valide", example = "Jeux Olympiques de Paris 2024")
    private String eventName;

    @Schema(description = "Prix du ticket", example = "99.99")
    private BigDecimal ticketPrice;

    @Schema(description = "Nombre de personnes autorisées avec ce ticket", example = "2")
    private String numberOfGuests;

    @Schema(description = "Clé de paiement associée à l'achat du ticket", example = "1N2j3K4L5M6N7O8P9Q0R")
    private String paymentKey;

    @Schema(description = "Clé unique pour le QR code du ticket, utilisée pour la validation à l'entrée", example = "a1b2c3d4-e5f6-g7h8-i9j01N2j3K4L5M6N7O8P9Q0R")
    private String qrCodeKey;

    @Schema(description = "Date d'achat du ticket au format texte", example = "2024-05-15 14:30:00")
    private String purchaseDate;

    @Schema(description = "Indique si le ticket a déjà été utilisé ('Ticket utilisé' ou 'Ticket non utilisé')", example = "Ticket non utilisé", allowableValues = {"Ticket utilisé", "Ticket non utilisé"})
    private String used;

}
