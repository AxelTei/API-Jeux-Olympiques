package com.jo.api.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketRequest {

    private String username;

    private String offerTitle;

    private BigDecimal ticketPrice;

    private String numberOfGuests;

    private String userKey;
}
