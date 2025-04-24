package com.jo.api.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private String bookingOffer;

    private BigDecimal price;

    @Column(unique = true, nullable = false)
    private String userKey;

    private String guestNames;
}
