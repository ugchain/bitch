package com.bi7.bitch.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Files;

import java.io.*;
import java.util.Date;

/**
 * Created by foxer on 2017/8/22.
 */
@Configuration
@ConfigurationProperties(prefix = "bitch")
public class AppConfig {



    private String secondaryDbPrefix;
    private String prikey;
    private boolean testing;

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    public String getSecondaryDbPrefix() {
        return secondaryDbPrefix;
    }

    public void setSecondaryDbPrefix(String secondaryDbPrefix) {
        this.secondaryDbPrefix = secondaryDbPrefix;
    }


}
