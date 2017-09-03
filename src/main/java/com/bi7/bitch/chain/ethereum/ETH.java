package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.conf.CoinName;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ChainId;

import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/28.
 */
public class ETH extends AbstractEthCoin {
    public ETH(Web3j web3, Credentials credentials, byte chainId) {
        super(web3, credentials, chainId);
    }



    @Override
    public CoinName getCoinName() {
        return CoinName.ETH;
    }

}
