package com.jo.api.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
