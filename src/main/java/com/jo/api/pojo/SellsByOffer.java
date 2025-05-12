package com.jo.api.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class SellsByOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String offerTitle;
    private BigDecimal offerPrice;
    private Integer sells;

    @OneToOne
    @JoinColumn(name = "booking_offer_id")
    @JsonBackReference
    private BookingOffer bookingOffer;
}
