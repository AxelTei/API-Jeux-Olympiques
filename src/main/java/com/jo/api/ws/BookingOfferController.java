package com.jo.api.ws;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER)
public class BookingOfferController {

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }
}
