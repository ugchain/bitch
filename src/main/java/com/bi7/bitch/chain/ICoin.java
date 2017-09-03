package com.bi7.bitch.chain;

import com.bi7.bitch.conf.CoinName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/28.
 */
public interface ICoin {

    BigInteger getBalance(String address);

    TransactionReceipt transfer(String toAddress, BigInteger value) throws IOException, InterruptedException, TransactionTimeoutException;

    InputData getTransactionById(String transactionHash);

    CoinName getCoinName();

}
