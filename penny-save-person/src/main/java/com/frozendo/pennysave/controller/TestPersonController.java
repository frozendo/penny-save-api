package com.frozendo.pennysave.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class TestPersonController {

    @GetMapping
    public String test() {
        return "Person";
    }

}
