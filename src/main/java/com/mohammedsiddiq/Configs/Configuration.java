package com.mohammedsiddiq.Configs;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configuration {
    public static Config config = ConfigFactory.load("default");
    public static final String USER_NAME = config.getString("USER_NAME");
    public static final String PLAY_AS = config.getString("PLAY_AS");
    public static final boolean AUTO_PLAY = config.getBoolean("AUTO_PLAY");

}
