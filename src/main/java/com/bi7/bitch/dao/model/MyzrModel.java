package com.bi7.bitch.dao.model;

/**
 * Created by foxer on 2017/8/24.
 */
public class MyzrModel {

    private int id;
    private int userid;
    private String username;
    private String coinname;
    private String txid;
    private String num;
    private String fee = "0";
    private String mum;
    private int sort = 0;
    private int blocknumber;
    private long addtime;
    private long endtime = 0;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoinname() {
        return coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getMum() {
        return mum;
    }

    public void setMum(String mum) {
        this.mum = mum;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getBlocknumber() {
        return blocknumber;
    }

    public void setBlocknumber(int blocknumber) {
        this.blocknumber = blocknumber;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MyzrModel{" +
                "id=" + id +
                ", userid=" + userid +
                ", username='" + username + '\'' +
                ", coinname='" + coinname + '\'' +
                ", txid='" + txid + '\'' +
                ", num=" + num +
                ", fee=" + fee +
                ", mum=" + mum +
                ", sort=" + sort +
                ", blocknumber=" + blocknumber +
                ", addtime=" + addtime +
                ", endtime=" + endtime +
                ", status=" + status +
                '}';
    }
}
