package com.jo.api.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Booking {

    private Long bookingId;
    private String bookingOffer;
    private String userKey;
    private List<String> guestNames;
}
