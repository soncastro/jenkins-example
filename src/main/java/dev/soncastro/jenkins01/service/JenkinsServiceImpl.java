package dev.soncastro.jenkins01.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JenkinsServiceImpl implements JenkinsService {

    @Override
    public String saida() {
        String str = "Hello Jenkins! " + LocalDateTime.now();
        IO.println(str);
        return str;
    }

}
