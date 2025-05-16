package com.jo.api.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Schema(name = "BookingOffer", description = "Représente une offre de réservation disponible pour les clients")
public class BookingOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de l'offre de réservation", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long bookingOfferId;

    @Column(nullable = false, unique = true)
    @Schema(description = "Titre de l'offre de réservation (doit être unique)", example = "Offre solo", required = true)
    private String title;

    @Column(nullable = false)
    @Schema(description = "Prix unitaire de l'offre de réservation", example = "99.99", required = true)
    private BigDecimal price;

    @Column(nullable = false)
    @Schema(description = "Nombre maximum de clients pouvant bénéficier de cette offre", example = "2", required = true, minimum = "1")
    private Integer numberOfCustomers;

    @OneToOne(mappedBy = "bookingOffer", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Schema(description = "Statistiques des ventes associées à cette offre de réservation")
    private SellsByOffer sellsByOffer;
}
