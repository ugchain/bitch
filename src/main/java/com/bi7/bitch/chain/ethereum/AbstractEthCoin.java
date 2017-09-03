package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.Logs;
import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.chain.ethereum.contract.ContractInputData;
import com.bi7.bitch.conf.GethConfig;
import org.apache.commons.logging.Log;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by foxer on 2017/8/28.
 */
public abstract class AbstractEthCoin extends Transfer implements ICoin {
    private final static Log log = Logs.getLogger(AbstractEthCoin.class);
    protected GethConfig gethConfig = SpringBeanFactoryUtils.getBean(GethConfig.class);
    protected Web3j web3;

    public AbstractEthCoin(Web3j web3, Credentials credentials, byte chainId) {
        super(web3, new RawTransactionManager(web3, credentials, chainId));
        this.web3 = web3;
    }

    @Override
    public TransactionReceipt transfer(String toAddress, BigInteger value) throws IOException, InterruptedException, TransactionTimeoutException {
        return this.send(toAddress, "", value, gethConfig.getWithdrawGasPrice(), gethConfig.getWithdrawGasLimit());
    }

    public InputData getTransactionById(String transactionHash) {
        Request<?, EthTransaction> ethTransactionRequest = web3.ethGetTransactionByHash(transactionHash);
        try {
            EthTransaction ethTransaction = ethTransactionRequest.send();
            Optional<Transaction> optional = ethTransaction.getTransaction();
            if (optional.isPresent()) {
                Transaction transaction = optional.get();
                return deserizeTransaction(transaction);
            }
        } catch (IOException e) {
            log.info("IOException", e);
        }
        return new InputData();
    }

    public BigInteger getBalance(String address) {

        Request<?, EthGetBalance> ethGetBalanceRequest = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST);
        try {
            EthGetBalance ethGetBalance = ethGetBalanceRequest.send();
            return ethGetBalance.getBalance();
        } catch (IOException e) {
            return BigInteger.ZERO;
        }
    }

    public InputData deserizeTransaction(Transaction transaction) {
        EthereumInputData inputData = new EthereumInputData();
        inputData.setFrom(transaction.getFrom());
        inputData.setTo(transaction.getTo());
        inputData.setValue(transaction.getValue());
        System.out.println(transaction.getBlockNumberRaw());
        if (transaction.getBlockNumberRaw() != null) {
            inputData.setBlockNumber(transaction.getBlockNumber());
        } else {
            inputData.setBlockNumber(BigInteger.ZERO);
        }
        inputData.setSuccess(isSuccess(transaction.getHash()));
        inputData.setTxid(transaction.getHash());
        inputData.setGasPrice(Convert.fromWei(new BigDecimal(transaction.getGasPrice()), Convert.Unit.GWEI).toBigInteger());
        inputData.setGasUsed(transaction.getGas());
        return inputData;
    }

    protected boolean isSuccess(String txid) {
        return true;
    }
}
