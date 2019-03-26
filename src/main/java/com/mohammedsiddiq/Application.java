package com.mohammedsiddiq.ChessGame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.mohammedsiddiq.ChessGame.DTOs.Move;
import com.mohammedsiddiq.ChessGame.DTOs.StartGameResponse;
import com.mohammedsiddiq.ChessGame.RestClient.GameClient;
import com.mohammedsiddiq.ChessGame.Service.GameService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    GameService service;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //based on the configuration run as client player (who initiates first request for game) or server(who responds to the initial request
        //Loading Configs

    }
}