package com.jo.api.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketResponse {

    private Long ticketId;

    private String username;

    private String offerTitle;

    private String eventName;

    private BigDecimal ticketPrice;

    private String numberOfGuests;

    private String purchaseDate;

    private String used;
}
