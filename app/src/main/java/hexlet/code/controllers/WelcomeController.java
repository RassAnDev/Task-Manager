package hexlet.code.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping("")
    public String root() {
        return "Welcome to Spring";
    }
}
