package com.bi7.bitch.dao.model;

/**
 * Created by fanjl on 2017/10/11.
 */
public enum DistributeWalletTypeEnum {
    NOT_USED(0), USED(1);
    private int id;

    DistributeWalletTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
