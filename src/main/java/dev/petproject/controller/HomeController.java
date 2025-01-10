package dev.petproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/home")
    public String homePage() {
        log.info("Accessing home page");
        return "index";
    }
}
