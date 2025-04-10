package com.jo.api.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingOffer {

    private Long bookingOfferId;
    private String title;
    private BigDecimal price;
    private Integer numberOfCustomers;
}
