package com.bi7.web3j.tx;

import org.bitcoinj.core.Transaction;
import org.web3j.crypto.Hash;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/9/14.
 */
public class LocalTransaction {

    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String from;
    private String to;
    private String data;
    private BigInteger value;
    private AsyncTransfer coin;
    private AsyncTransferBtc coinBtc;
    private Transaction btcTx;
    private String btcTxId;
    private String rawTx;

    public Transaction getBtcTx() {
        return this.btcTx;
    }

    public void setBtcTx(Transaction btcTx) {
        this.btcTx = btcTx;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public LocalTransaction(AsyncTransferBtc coin, String to, BigInteger value, Transaction tx, String btcTxId,String rawTx) {
        this.coinBtc = coin;
        this.rawTx = rawTx;
        this.to = to;
        this.value = value;
        this.btcTx = tx;
        this.btcTxId = btcTxId;
    }


    public LocalTransaction(AsyncTransfer coin, BigInteger gasPrice, BigInteger gasLimit, String from, String to, String data, BigInteger value) {
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.to = to;
        this.data = data;
        this.value = value;
        this.coin = coin;
        this.from = from;

    }

    public String getRawTx() {
        return this.rawTx;
    }

    private void checkRawTx() throws IOException {
        rawTx = coin.buildRawTx(gasPrice, gasLimit, to, data, value);
    }

    public String getBtcTxId() {
        return this.btcTxId;
    }

    public String getTxId() throws IOException {
        checkRawTx();
        return Hash.sha3(rawTx);
    }

    public void send() throws IOException {
        checkRawTx();
        coin.send(rawTx);
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public int getGasPriceGWei() {
        return Convert.fromWei(new BigDecimal(this.getGasPrice()), Convert.Unit.GWEI).intValue();
    }

    @Override
    public String toString() {
        return "LocalTransaction{" +
                "gasPrice=" + gasPrice +
                ", gasLimit=" + gasLimit +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", data='" + data + '\'' +
                ", value=" + value +
                ", coin=" + coin +
                ", rawTx='" + rawTx + '\'' +
                '}';
    }
}
