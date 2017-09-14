package com.bi7.web3j.tx;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Async;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by foxer on 2017/9/3.
 * 获取txid 与 send 分开
 */
public class AsyncTransfer {
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    protected Web3j web3j;
    final Credentials credentials;
    private final byte chainId;


    protected AsyncTransfer(Web3j web3j, Credentials credentials, byte chainId) {
        this.web3j = web3j;
        this.credentials = credentials;
        this.chainId = chainId;
    }

    protected String buildRawTx(BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value) throws IOException {
        BigInteger nonce = this.getNonce();
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        return this.sign(rawTransaction);
    }

    protected String getFromAddress(){
        return this.credentials.getAddress();
    }
//    public String send(BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value) throws IOException {
//        BigInteger nonce = this.getNonce();
//        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
//        String hexValue = this.sign(rawTransaction);
//        Async.run(() -> this.send(hexValue));
//        return Hash.sha3(hexValue);
//    }

    protected void send(String rawTx) {
        Async.run(() -> this.sendRawTx(rawTx));
    }

    private String sign(RawTransaction rawTransaction) {
        byte[] signedMessage;
        if (this.chainId > -1) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, this.chainId, this.credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, this.credentials);
        }
        return Numeric.toHexString(signedMessage);
    }

    private EthSendTransaction sendRawTx(String hexValue) throws IOException {
        return (EthSendTransaction) this.web3j.ethSendRawTransaction(hexValue).send();
    }

    private BigInteger getNonce() throws IOException {
        EthGetTransactionCount ethGetTransactionCount = (EthGetTransactionCount) this.web3j.ethGetTransactionCount(this.credentials.getAddress(), DefaultBlockParameterName.PENDING).send();
        return ethGetTransactionCount.getTransactionCount();
    }

}
