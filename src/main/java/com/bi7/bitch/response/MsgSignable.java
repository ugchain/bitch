package com.bi7.bitch.response;

import com.bi7.bitch.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by foxer on 2017/8/26.
 */
public class MsgSignable {

    @Autowired
    SignUtil signUtil;

    protected Msg getMsg(Status status, Map<String, Object> map) {
        String sign = signUtil.buildSign(map);
        return Msgs.buildObjectResMsg(status, map, sign);
    }

}
