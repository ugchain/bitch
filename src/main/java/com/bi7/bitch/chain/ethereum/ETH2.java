package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.conf.CoinName;
import com.bi7.web3j.tx.AsyncTransfer;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/9/3.
 */
public class ETH2 extends AsyncTransfer {
    public ETH2(Web3j web3j, Credentials credentials, byte chainId) {
        super(web3j, credentials, chainId);
    }
}
