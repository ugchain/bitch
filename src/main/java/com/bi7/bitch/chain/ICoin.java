package com.bi7.bitch.chain;

import com.bi7.bitch.conf.CoinName;
import com.bi7.web3j.tx.LocalTransaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by foxer on 2017/8/28.
 */
public interface ICoin {

    BigInteger getBalance(String address);

    /*
    return txHashId
     */
    LocalTransaction buildTx(String toAddress, BigInteger value) throws IOException;

    Optional<InputData> getTransactionById(String transactionHash);

    CoinName getCoinName();

}
