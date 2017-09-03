package com.bi7.bitch.response;

/**
 * Created by foxer on 2017/8/24.
 */
public final class Msgs {

    public static Msg buildObjectResMsg(Status status, Object data, String sign) {
        ObjectResMsg res = new ObjectResMsg();
        res.setData(data);
        res.setStatus(status);
        res.setSign(sign);
        return res;
    }
}
