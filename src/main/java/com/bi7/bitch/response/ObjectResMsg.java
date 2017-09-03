package com.bi7.bitch.response;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Created by foxer on 2017/8/21.
 */
public class ObjectResMsg extends Msg {
    private final static Object objPresent = new Object();

    private Object data = objPresent;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
