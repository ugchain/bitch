package com.bi7.bitch.chain;

import com.bi7.bitch.conf.CoinAttribute;
import com.bi7.bitch.conf.CoinConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by foxer on 2017/8/30.
 */
@Component
public class Chains {

    private final static Log log = LogFactory.getLog(Chains.class);
    @Autowired
    private Web3j web3j;

    @Autowired
    private CoinConfig contractConfig;

    public com.bi7.bitch.chain.Transaction newInstance(CoinAttribute coinAttr, Credentials credentials) {
        if (com.bi7.bitch.chain.Transaction.coinName.equals(coinAttr.getName())) {
            return new com.bi7.bitch.chain.Transaction(credentials);
        } else {
            return new Erc20Transaction(credentials, coinAttr);
        }
    }

    public BigInteger getBalance(String address) {
        Request<?, EthGetBalance> ethGetBalanceRequest = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST);
        try {
            EthGetBalance ethGetBalance = ethGetBalanceRequest.send();
            return ethGetBalance.getBalance();
        } catch (IOException e) {
            return BigInteger.ZERO;
        }
    }

    public BigInteger getBalance(String address, CoinAttribute contractAttr) {
        Address addressA = new Address(address);
        Function function = new Function("balanceOf", Arrays.<Type>asList(addressA), Collections.<TypeReference<?>>emptyList());
        String dataHex = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.request.Transaction transaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address, contractAttr.getContractAddress(), dataHex);
        try {
            org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            String value = ethCall.getValue();
            if (value.equals("0x")) {
                value = "0x0";
            }
            return Numeric.toBigInt(value);
        } catch (IOException e) {
            log.error("IOException while transfer ", e);
        }
        return BigInteger.ZERO;
    }


    public Optional<ITransaction> getTransactionById(String transactionHash) {
        Request<?, EthTransaction> ethTransactionRequest = web3j.ethGetTransactionByHash(transactionHash);
        try {
            EthTransaction ethTransaction = ethTransactionRequest.send();
            Optional<Transaction> transaction = ethTransaction.getTransaction();
            return transaction.map(this::getTransaction);
        } catch (IOException e) {
            log.info("IOException", e);
        }
        return Optional.empty();
    }

    public ITransaction getTransaction(Transaction transaction) {
        return contractConfig.getContractAttrByAddress(transaction.getTo())
                .filter(attr -> Erc20Transaction.isVaildTransfer(transaction.getInput()))
                .map(attr -> (ITransaction) new Erc20Transaction(transaction, attr))
                .orElseGet(() -> new com.bi7.bitch.chain.Transaction(transaction));

    }
}
