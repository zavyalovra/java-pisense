package com.github.zavyalovra.pisense.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class HomeController {
    private final String homePage;

    public HomeController(@Value("classpath:static/index.html") Resource page) throws IOException {
        this.homePage = page.getContentAsString(StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")
    public String homePage() {
        return homePage;
    }
}
