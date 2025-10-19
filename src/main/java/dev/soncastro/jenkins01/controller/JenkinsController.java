package dev.soncastro.jenkins01.controller;

import dev.soncastro.jenkins01.service.JenkinsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/jenkins")
public class JenkinsController {

    private final JenkinsService jenkinsService;

    public JenkinsController(JenkinsService jenkinsService) {
        this.jenkinsService = jenkinsService;
    }

    @GetMapping
    public ResponseEntity<String> helloJenkins() {
        String str = this.jenkinsService.saida();
        return ResponseEntity.ok("versao 2");
    }

}
