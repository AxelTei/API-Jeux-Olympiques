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

    private String title;
    private BigDecimal price;
    private Integer numberOfCustomers;

    @OneToOne(mappedBy = "bookingOffer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private SellsByOffer sellsByOffer;
}
