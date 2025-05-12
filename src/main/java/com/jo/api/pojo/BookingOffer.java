package com.jo.api.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class BookingOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingOfferId;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer numberOfCustomers;

    @OneToOne(mappedBy = "bookingOffer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private SellsByOffer sellsByOffer;
}
