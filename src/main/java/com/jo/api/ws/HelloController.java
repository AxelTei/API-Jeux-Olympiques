package com.jo.api.ws;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( value = "/api/toto")
public class HelloController {

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }
}
