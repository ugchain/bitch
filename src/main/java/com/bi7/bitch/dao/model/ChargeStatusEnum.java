package com.bi7.bitch.dao.model;

/**
 * Created by foxer on 2017/8/26.
 */
public enum ChargeStatusEnum {

    PENDING(1), SUCCESS(2), FAILURE(3);
    private int id;

    ChargeStatusEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
