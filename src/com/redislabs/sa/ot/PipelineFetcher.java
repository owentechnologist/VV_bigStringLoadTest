package com.redislabs.sa.ot;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PipelineFetcher {
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
        ScanParams scanParams = new ScanParams().count(500);
        do {
            ScanResult<String> sscan = r.sscan(this.mainKey, cursor,scanParams);
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
        Pipeline pipeline = r.pipelined();
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
                Response<List<String>> resultPipe =  pipeline.mget(args);
                pipeline.sync();
                List<String> results = resultPipe.get();
                System.out.println("Total count of fetched values is "+counter+" --> just fetched values for "+results.size()+" string keys");
                subset.clear();
            }
        }
    }

}
