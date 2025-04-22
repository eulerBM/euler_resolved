package org.erbr;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class config {
    private static final Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
