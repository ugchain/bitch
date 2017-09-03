package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.Logs;
import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.conf.GethConfig;
import org.apache.commons.logging.Log;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by foxer on 2017/8/28.
 */
public abstract class AbstractEthCoin extends Transfer implements ICoin {
    private final static Log log = Logs.getLogger(AbstractEthCoin.class);
    protected Web3j web3;
    protected Credentials credentials;
    protected GethConfig gethConfig = SpringBeanFactoryUtils.getBean(GethConfig.class);

    public AbstractEthCoin(Web3j web3, Credentials credentials,byte chainId) {
        super(web3, new RawTransactionManager(web3, credentials, chainId));
        this.web3 = web3;
        this.credentials = credentials;
    }

    @Override
    public TransactionReceipt transfer(String toAddress, BigInteger value) throws IOException, InterruptedException, TransactionTimeoutException {
        return this.send(toAddress, "", value, gethConfig.getWithdrawGasPrice(), gethConfig.getWithdrawGasLimit());
    }

    public InputData getTransactionById(String transactionHash) {
        return null;
    }

    protected abstract boolean isSuccess(String txid);
}
