package com.redislabs.sa.ot;
import redis.clients.jedis.Jedis;

import java.util.Properties;

public class Main {
    static Jedis connection = JedisConnectionFactory.getInstance().getJedisConnectionFromPool();

    public static void main(String[] args) {
        Properties pargs = PropertyFileFetcher.loadProps("arguments.properties");
        long tStart = System.currentTimeMillis();
        populateRedis(pargs);
        long tEnd = System.currentTimeMillis();
        System.out.println("\nMSET write took "+((tEnd-tStart)/1000.0)+" seconds...");
        tStart = System.currentTimeMillis();
        optimizedReadFromRedis(pargs);
        tEnd = System.currentTimeMillis();
        System.out.println("Optimized read took "+((tEnd-tStart)/1000.0)+" seconds...");
        //read again without pipeline:
        tStart = System.currentTimeMillis();
        readFromRedis(pargs);
        tEnd = System.currentTimeMillis();
        System.out.println("Regular Read took "+((tEnd-tStart)/1000.0)+" seconds...");
    }

    static void populateRedis(Properties pargs){
        int numberOfKeys = Integer.parseInt(
                pargs.getProperty("numberOfKeys")
        );
        int sizeOfStringKB = Integer.parseInt(
                        pargs.getProperty("sizeOfStringKB")
        );
        PopulateHelper populateHelper = new PopulateHelper();
        populateHelper.generateKeys(numberOfKeys);
        populateHelper.loadSetOfKeysIntoRedis("VV:prefix:setSize:",connection);
        populateHelper.setStringSizeInKB(sizeOfStringKB);
        populateHelper.loadManyStringsIntoRedisUsingKeys(connection); // is this slower than below?
        //populateHelper.loadMultiStringsIntoRedisUsingKeys(connection);
    }

    static void readFromRedis(Properties pargs){

        int sizeOfBatch = Integer.parseInt(
                pargs.getProperty("sizeOfReadBatch")
        );

        FetchTestDataStringsHelper fetcher = new FetchTestDataStringsHelper();
        fetcher.setMainKeyPrefix("VV:prefix:setSize:");
        fetcher.fetchSetKeyFromRedisUsingSetPrefix(connection);
        fetcher.fetchAllStringKeysWithSetKeyContentsFromRedisUsingScan(connection);
        fetcher.fetchAllValuesForAllKeysInTest(sizeOfBatch,connection);
    }
    static void optimizedReadFromRedis(Properties pargs){

        int sizeOfBatch = Integer.parseInt(
                pargs.getProperty("sizeOfReadBatch")
        );

        PipelineFetcher fetcher = new PipelineFetcher();
        fetcher.setMainKeyPrefix("VV:prefix:setSize:");
        fetcher.fetchSetKeyFromRedisUsingSetPrefix(connection);
        fetcher.fetchAllStringKeysWithSetKeyContentsFromRedisUsingScan(connection);
        fetcher.fetchAllValuesForAllKeysInTest(sizeOfBatch,connection);
    }

}
