package com.bi7.bitch.response;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by foxer on 2017/8/21.
 */
public class Msg {

    public static final Msg ERROR = new Msg(Status.ERROR);
    public static final Msg PARAM_ERROR = new Msg(Status.PARAM_ERROR);

    private int status = Status.OK.getId();

    private String msg;

    public String getMsg() {
        if (msg != null) {
            return msg;
        }
        return Status.get(status).toString();
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Msg() {

    }

    public Msg(Status status) {
        this.status = status.getId();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status.getId();
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
