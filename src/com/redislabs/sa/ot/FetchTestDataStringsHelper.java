package com.redislabs.sa.ot;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FetchTestDataStringsHelper {
    String mainKey = "";
    String mainKeyPrefix = "VV:prefix:setSize:";
    ArrayList<String> allKeys = new ArrayList<String>();

    private void setMainKey(String val){
        this.mainKey = val;
    }

    public void setMainKeyPrefix(String val){
        this.mainKeyPrefix = val;
    }

    public void fetchSetKeyFromRedisUsingSetPrefix(Jedis r){
        Set<String> keyCandidates = r.keys(this.mainKeyPrefix+"*");
        this.mainKey = (String)keyCandidates.toArray()[0];
        System.out.println("Found this many main key candidates: "+keyCandidates.size());
        System.out.println("MainKey name is: "+this.mainKey);
    }

    public void fetchAllStringKeysWithSetKeyContentsFromRedisUsingScan(Jedis r){
        String cursor = "0";
        do {
            ScanResult<String> sscan = r.sscan(this.mainKey, cursor);
            cursor = sscan.getCursor();
            loadListIntoAllKeys(sscan.getResult());
            if(allKeys.size()%1000==0) {
                System.out.println("Fetched "+allKeys.size()+" keys for our String values");
            }
        }while(Integer.parseInt(cursor)>0);
        System.out.println("AllKeys have been fetched -- total fetched is 1 [mainKey] plus "+allKeys.size());
    }

    void loadListIntoAllKeys(List<String> results){
        for(String l: results){
            allKeys.add(l);
        }
    }

    public void fetchAllValuesForAllKeysInTest(int batchSize,Jedis r){
        ArrayList<String> subset = new ArrayList<String>();
        int counter = 0;
        for(String k:allKeys){
            subset.add(allKeys.get(counter));
            counter++;
            if(subset.size()%batchSize==0){
                String [] args = new String[batchSize];
                int innerCounter =0;
                for(String s:subset){
                    args[innerCounter] = s;
                    innerCounter++;
                }
                List<String> results = r.mget(args);
                System.out.println("Total count of fetched values is "+counter+" --> just fetched values for "+results.size()+" string keys");
                subset.clear();
            }
        }
    }

}
