package com.bi7.bitch.response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by foxer on 2017/8/21.
 */
public enum Status {
    OK(0),

    EMPTY(1),

    ERROR(2),

    PARAM_ERROR(3);

    private int id;

    private static Map<Integer, Status> map = new HashMap<>();

    static {
        map.put(OK.getId(), OK);
        map.put(EMPTY.getId(), EMPTY);
        map.put(ERROR.getId(), ERROR);
        map.put(PARAM_ERROR.getId(), PARAM_ERROR);
    }

    Status(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Status get(int id) {
        return map.get(id);
    }

}
