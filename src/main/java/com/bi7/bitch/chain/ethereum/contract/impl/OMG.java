package com.bi7.bitch.chain.ethereum.contract.impl;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.ethereum.ContractAddress;
import com.bi7.bitch.chain.ethereum.contract.AbstractEthContractCoin;
import com.bi7.bitch.conf.CoinName;
import org.apache.commons.logging.Log;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

/**
 * Created by foxer on 2017/9/18.
 */
public class OMG extends AbstractEthContractCoin {
    private final static Log log = Logs.getLogger(OMG.class);

    public OMG(Web3j web3, Credentials credentials, byte chainId) {
        super(web3, credentials, ContractAddress.findAddress("omg"), chainId);

    }

    @Override
    public CoinName getCoinName() {
        return CoinName.OMG;
    }
}
