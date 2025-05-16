package com.jo.api.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Schema(name = "SellsByOffer", description = "Représente les statistiques de ventes pour une offre de réservation spécifique")
public class SellsByOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique des statistiques de ventes", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Titre de l'offre de réservation associée", example = "Offre Duo")
    private String offerTitle;

    @Schema(description = "Prix de l'offre de réservation", example = "99.99")
    private BigDecimal offerPrice;

    @Schema(description = "Nombre total de ventes réalisées pour cette offre", example = "42", minimum = "0")
    private Integer sells;

    @OneToOne
    @JoinColumn(name = "booking_offer_id")
    @JsonBackReference
    @Schema(description = "Référence à l'offre de réservation associée à ces statistiques", hidden = true)
    private BookingOffer bookingOffer;
}
