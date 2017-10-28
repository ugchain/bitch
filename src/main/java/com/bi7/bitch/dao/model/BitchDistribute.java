package com.bi7.bitch.dao.model;

import java.util.Date;

/**
 * Created by fanjl on 2017/10/11.
 */
public class BitchDistribute {
    private int id;
    private String coinname;
    private String address;
    private String addressCheck;
    private int status;
    private Date addtime;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoinname() {
        return this.coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressCheck() {
        return this.addressCheck;
    }

    public void setAddressCheck(String addressCheck) {
        this.addressCheck = addressCheck;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getAddtime() {
        return this.addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    @Override
    public String toString() {
        return "BitchDistribute{" +
                "id=" + id +
                ", coinname='" + coinname + '\'' +
                ", address='" + address + '\'' +
                ", addressCheck='" + addressCheck + '\'' +
                ", status=" + status +
                ", addtime=" + addtime +
                '}';
    }
}
