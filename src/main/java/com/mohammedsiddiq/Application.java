package com.mohammedsiddiq;

import com.mohammedsiddiq.Configs.Configuration;
import com.mohammedsiddiq.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        if (Boolean.parseBoolean(args[1])) {
            service = new GameService();
            service.setRetrofit(new Retrofit.Builder().baseUrl("http://" + args[2]).addConverterFactory(GsonConverterFactory.create()).build());
            service.autoPlay();


        }


    }
}