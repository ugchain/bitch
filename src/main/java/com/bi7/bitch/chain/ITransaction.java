package com.bi7.bitch.chain;

import com.bi7.bitch.conf.CoinAttribute;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/11/4.
 */
public interface ITransaction {

    BigInteger getValue();

    CoinAttribute getCoinAttr();

    String getTxId();

    String buildTxId() throws IOException;

    void send() throws IOException;

    boolean isConfirmed();

    String getTo();

    String getFrom();

    BigInteger getGasPrice();

    BigInteger getGasUsed();

    BigInteger getBlockNumber();


}
