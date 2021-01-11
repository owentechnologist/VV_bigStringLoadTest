package com.redislabs.sa.ot;
import redis.clients.jedis.Jedis;

public class Main {
    static Jedis r = RedisConnection.getConnection();
    public static void main(String[] args) {
        //populateRedis();
        long tStart = System.currentTimeMillis();
        optimizedReadFromRedis();
        long tEnd = System.currentTimeMillis();
        System.out.println("Optimized read took "+((tEnd-tStart)/1000.0)+" seconds...");
        //read again without pipeline:
        tStart = System.currentTimeMillis();
        readFromRedis();
        tEnd = System.currentTimeMillis();
        System.out.println("Regular Read took "+((tEnd-tStart)/1000.0)+" seconds...");
    }

    static void populateRedis(){
        PopulateHelper populateHelper = new PopulateHelper();
        populateHelper.generateKeys(50000);
        populateHelper.loadSetOfKeysIntoRedis("VV:prefix:setSize:",r);
        populateHelper.setStringSizeInKB(20);
        populateHelper.loadManyStringsIntoRedisUsingKeys(r);
    }
    static void readFromRedis(){
        FetchTestDataStringsHelper fetcher = new FetchTestDataStringsHelper();
        fetcher.setMainKeyPrefix("VV:prefix:setSize:");
        fetcher.fetchSetKeyFromRedisUsingSetPrefix(r);
        fetcher.fetchAllStringKeysWithSetKeyContentsFromRedisUsingScan(r);
        fetcher.fetchAllValuesForAllKeysInTest(1000,r);
    }
    static void optimizedReadFromRedis(){
        PipelineFetcher fetcher = new PipelineFetcher();
        fetcher.setMainKeyPrefix("VV:prefix:setSize:");
        fetcher.fetchSetKeyFromRedisUsingSetPrefix(r);
        fetcher.fetchAllStringKeysWithSetKeyContentsFromRedisUsingScan(r);
        fetcher.fetchAllValuesForAllKeysInTest(1000,r);
    }

}
