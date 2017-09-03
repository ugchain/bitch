package com.bi7.bitch.chain.ethereum.contract.impl;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.ethereum.ContractAddress;
import com.bi7.bitch.chain.ethereum.contract.AbstractEthContractCoin;
import com.bi7.bitch.conf.CoinName;
import org.apache.commons.logging.Log;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/30.
 */
public class UGT extends AbstractEthContractCoin {

    private final static Log log = Logs.getLogger(UGT.class);

    public UGT(Web3j web3, Credentials credentials, byte chainId) {
        super(web3, credentials, ContractAddress.findAddress("ugt"), chainId);

    }

    @Override
    public CoinName getCoinName() {
        return CoinName.UGT;
    }

    @Override
    public TransactionReceipt transfer(String toAddress, BigInteger value) throws IOException, InterruptedException, TransactionTimeoutException {
        return null;
    }
}
