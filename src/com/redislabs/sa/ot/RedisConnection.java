package com.redislabs.sa.ot;

import redis.clients.jedis.Jedis;

import java.util.Properties;

public class RedisConnection {
    private static String REDIS_PASSWORD = "";

    public static Jedis getConnection() {
        JedisConnectionFactory factory = JedisConnectionFactory.getInstance();
        Properties config = PropertyFileFetcher.loadProps("jedisconnectionfactory.properties");
        String pass =  (String) config.get("REDIS_PASSWORD");
        if(null != pass && pass.length()>0){
            REDIS_PASSWORD=pass;
        }
        Jedis jedis = factory.getJedisConnectionFromPool();
        if (REDIS_PASSWORD.length() > 0) {
            jedis.auth(REDIS_PASSWORD);
        }
        return jedis;
    }
}
