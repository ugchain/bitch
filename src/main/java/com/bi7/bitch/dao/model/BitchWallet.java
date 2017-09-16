package com.bi7.bitch.dao.model;

import java.util.Date;

/**
 * Created by foxer on 2017/8/24.
 */
public class BitchWallet {
    private int userid;
    private String coinname;
    private String address;
    private String sha3;
    private Date addtime;
    private String keystore;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getSha3() {

        return sha3;
    }

    public void setSha3(String sha3) {
        this.sha3 = sha3;
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
        return "BitchWallet{" +
                "userid=" + userid +
                ", coinname='" + coinname + '\'' +
                ", address='" + address + '\'' +
                ", sha3='" + sha3 + '\'' +
                ", addtime=" + addtime +
                ", keystore='" + keystore + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
