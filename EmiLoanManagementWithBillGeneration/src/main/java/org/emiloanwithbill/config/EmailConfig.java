package org.emiloanwithbill.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {
    private static final Properties PROPS = new Properties();
   public static final Logger LOG= LoggerFactory.getLogger(EmailConfig.class);

    static {
        try(InputStream in = EmailConfig.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if(in == null) {
                throw new IllegalStateException("config.properties resource not found");
            }
            PROPS.load(in);
        }catch (Exception e){
            LOG.error("Error loading config.properties", e);
        }
    }

    public static String get(String key) {
        return PROPS.getProperty(key);
    }
}
