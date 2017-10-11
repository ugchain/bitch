package com.bi7.bitch.dao.model;

import java.util.Date;

/**
 * Created by foxer on 2017/8/24.
 */
public class BitchWalletAddress {
    private int userid;
    private String coinname;
    private String address;
    private String addressCheck;
    private Date addtime;

    public String getAddressCheck() {
        return this.addressCheck;
    }

    public void setAddressCheck(String addressCheck) {
        this.addressCheck = addressCheck;
    }

    public String getCoinname() {
        return coinname;
    }

    public String getAddress() {
        return address;
    }

    public Date getAddtime() {
        return addtime;
    }

    public int getUserid() {

        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    @Override
    public String toString() {
        return "BitchWalletAddress{" +
                "userid=" + userid +
                ", coinname='" + coinname + '\'' +
                ", address='" + address + '\'' +
                ", addressCheck='" + addressCheck + '\'' +
                ", addtime=" + addtime +
                '}';
    }
}
