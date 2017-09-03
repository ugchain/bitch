package com.bi7.bitch.chain;

import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/25.
 */
public class InputData {
    private String from;
    private String to;
    private BigInteger value;
    private BigInteger blockNumber;
    private String txid;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    private boolean isSuccess;

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

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String toString() {
        return String.format("from : %s, to : %s, value : %s, blockNumber: %d , txid: %s", from, to, value, blockNumber, txid);
    }
}
