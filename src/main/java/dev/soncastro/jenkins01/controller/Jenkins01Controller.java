package dev.soncastro.jenkins01.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jenkins01")
public class Jenkins01Controller {

    @GetMapping
    public ResponseEntity<String> helloJenkins() {
        return ResponseEntity.ok("Hello Jenkins!");
    }

}
