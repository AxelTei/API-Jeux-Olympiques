package com.jo.api.pojo;

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
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    private String username;

    private String offerTitle;

    private String eventName;

    private BigDecimal ticketPrice;

    private String numberOfGuests;

    private String paymentKey;

    private String qrCodeKey;

    private String purchaseDate;

    private String used;

}
