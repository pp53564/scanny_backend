package project.scanny.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {

    @GetMapping("/protected")
    public String getProtectedResource() {
        return "This is a protected resource";
    }
}
