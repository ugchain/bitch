package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.conf.CoinName;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ChainId;

import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/28.
 */
public class ETC extends AbstractEthCoin {
    public ETC(Web3j web3, Credentials credentials) {
        super(null, null, ChainId.NONE);
    }

    @Override
    public String getBalance(String address) {
        return null;
    }

    @Override
    public CoinName getCoinName() {
        return null;
    }

    @Override
    protected boolean isSuccess(String txid) {
        return false;
    }
}
