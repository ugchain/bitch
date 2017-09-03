package com.bi7.bitch.mapper.secondary.provider;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.dao.model.MyzrModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by foxer on 2017/8/24.
 */
public class MyzrProvider {

    AppConfig config;

    public MyzrProvider() {
        config = SpringBeanFactoryUtils.getBean(AppConfig.class);
    }

    public String insert() {
        return "insert into " + config.getSecondaryDbPrefix() + "_myzr ("
                + "userid, username, coinname, txid, num, fee, mum, sort, blocknumber, addtime, endtime, status"
                + ") values ("
                + "#{userid}, #{username}, #{coinname},#{txid},#{num},#{fee},#{mum},#{sort},#{blocknumber},#{addtime},#{endtime},#{status})";
    }

    public String findZrid() {
        return "select id from " + config.getSecondaryDbPrefix() + "_myzr where txid = #{txid}";
    }

    public String updateStatus() {
        return "update " + config.getSecondaryDbPrefix() + "_myzr set status = #{status} where id = #{id}";
    }
}
