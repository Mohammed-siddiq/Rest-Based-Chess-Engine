package com.mohammedsiddiq;

import com.mohammedsiddiq.Configs.Configuration;
import com.mohammedsiddiq.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    GameService service;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        // If auto play is configured then start playing with the opponent.
        if (Boolean.parseBoolean(args[1]))
            service.autoPlay();


    }
}