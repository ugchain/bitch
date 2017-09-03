package com.bi7.bitch.dao.model;

/**
 * Created by foxer on 2017/8/26.
 */
public enum CoinTypeEnum {

    WITHDRAW(0), CHARGE(1);
    private int id;

    CoinTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
