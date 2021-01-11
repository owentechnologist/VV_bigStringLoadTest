package com.redislabs.sa.ot;

import redis.clients.jedis.Jedis;

public class RedisConnection {
    private final static String REDIS_HOST = "35.184.29.138"; //"redis-12000.ot2020.demo.redislabs.com";// "localhost";
    private final static int REDIS_PORT = 12000; //6379;
    private final static String REDIS_PASSWORD = "";
    private final static int TIME_OUT = 10000;

    public static Jedis getConnection() {
        Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT,TIME_OUT);
        if (REDIS_PASSWORD.length() > 0) {
            jedis.auth(REDIS_PASSWORD);
        }

        return jedis;
    }
}
