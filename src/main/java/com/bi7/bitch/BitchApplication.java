package com.bi7.bitch;

import com.bi7.bitch.conf.CoinConfig;
import com.bi7.bitch.conf.GethConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by foxer on 2017/8/22.
 */
@SpringBootApplication
public class BitchApplication {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(BitchApplication.class, args);
        context.getBean(GethConfig.class).init();
        context.getBean(CoinConfig.class).init();
    }
}
