package com.jo.api.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false)
    private String bookingOfferTitle;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String userKey;

    @Column(nullable = false)
    private Integer numberOfGuests;
}
