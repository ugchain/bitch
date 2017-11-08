package com.bi7.bitch.dao.model;

import java.util.Date;

/**
 * Created by foxer on 2017/8/24.
 */
public class BitchCoin {
    private int rid;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    private int userid;
    private String coinname;
    private int type;//提币 or 充币  0 / 1
    private String from;
    private String to;
    private String value;
    private String fee;//手续费 在 value 之外
    private int blockNumber;
    private String txid;
    private int gasUsed;
    private int gasPrice;
    private int status;
    private int chainStatus;//链上状态 for withdraw 1：审核通过，创建本条记录   2：创建nonce，以及生成rawTx，并send,且更新 from
    private int nonce;//
    private Date addtime;
    private Date updatetime;

    public int getGasUsed() {
        return gasUsed;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCoinname() {
        return coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String form) {
        this.from = form;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public int getChainStatus() {
        return chainStatus;
    }

    public void setChainStatus(int chainStatus) {
        this.chainStatus = chainStatus;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "BitchCoin{" +
                "rid=" + rid +
                ", userid=" + userid +
                ", coinname='" + coinname + '\'' +
                ", type=" + type +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", value='" + value + '\'' +
                ", fee='" + fee + '\'' +
                ", blockNumber=" + blockNumber +
                ", txid='" + txid + '\'' +
                ", gasUsed=" + gasUsed +
                ", gasPrice=" + gasPrice +
                ", status=" + status +
                ", chainStatus=" + chainStatus +
                ", nonce=" + nonce +
                ", addtime=" + addtime +
                ", updatetime=" + updatetime +
                '}';
    }
}
