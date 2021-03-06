package com.redislabs.sa.ot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileFetcher {

    public static Properties loadProps(String propertyFileName){
        InputStream inputStream = PropertyFileFetcher.class.getClassLoader().getResourceAsStream(propertyFileName);
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }
}
