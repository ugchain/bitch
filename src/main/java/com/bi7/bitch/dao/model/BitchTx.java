package com.bi7.bitch.dao.model;

import java.util.Date;

/**
 * Created by foxer on 2017/10/31.
 * 内部账目调动
 */
public class BitchTx {

    private int id;
    private String coinname;
    private String from;
    private String to;
    private int blockNumber;
    private String txid;
    private String value;
    private String fee;
    private int gasUsed;
    private int gasPrice;
    private Date time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoinname() {
        return coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blocknumber) {
        this.blockNumber = blocknumber;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(int gasUsed) {
        this.gasUsed = gasUsed;
    }

    public int getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BitchTx{" +
                "id=" + id +
                ", coinname='" + coinname + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", blocknumber=" + blockNumber +
                ", txid='" + txid + '\'' +
                ", value='" + value + '\'' +
                ", fee='" + fee + '\'' +
                ", gasUsed=" + gasUsed +
                ", gasPrice=" + gasPrice +
                ", time=" + time +
                '}';
    }
}
