package com.bi7.bitch.mapper.secondary.provider;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.conf.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by foxer on 2017/8/26.
 */
public class UserCoinProvider {

    AppConfig config;

    public UserCoinProvider() {
        config = SpringBeanFactoryUtils.getBean(AppConfig.class);
    }

    public String updateCoinBalance(Map<String, Object> map) {
        String coinname = map.get("coinname").toString();
        return "update " + config.getSecondaryDbPrefix() + "_user_coin  set " + coinname + " = " + coinname + " + #{incval} where userid = #{userid}";
    }
}
