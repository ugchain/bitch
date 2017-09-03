package com.bi7.bitch.mapper.secondary.provider;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.dao.model.MyzcModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by foxer on 2017/8/24.
 */
public class MyzcProvider {


    AppConfig config;

    public MyzcProvider(){
        config = SpringBeanFactoryUtils.getBean(AppConfig.class);
    }
    public String updateStatus() {
        return "update " + config.getSecondaryDbPrefix() + "_myzc zc set zc.status = #{status} where zc.id = #{id}";
    }

    public String updateTxidAndStatus() {
        return "update " + config.getSecondaryDbPrefix() + "_myzc zc set zc.txid = #{txid},zc.status = #{status} where zc.id = #{id}";
    }

    public String findOne() {
        return "select * from " + config.getSecondaryDbPrefix() + "_myzc where id = #{id}";
    }

}
