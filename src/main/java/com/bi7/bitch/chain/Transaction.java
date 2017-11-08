package com.bi7.bitch.chain;

import com.bi7.bitch.Logs;
import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.conf.CoinAttribute;
import com.bi7.bitch.conf.CoinConfig;
import com.bi7.bitch.conf.GethConfig;
import com.bi7.web3j.tx.AsyncTransfer;
import org.apache.commons.logging.Log;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by foxer on 2017/11/4.
 */
public class Transaction extends AsyncTransfer implements ITransaction {
    public final static String coinName = "eth";
    private final static Log log = Logs.getLogger(Transaction.class);

    protected String from;
    protected String to;
    protected BigInteger value;
    protected BigInteger blockNumber;
    protected String txId;
    protected BigInteger gasPrice;
    protected BigInteger gasUsed;
    protected String data;

    protected String rawTx;

    public Transaction(Credentials credentials) {
        this(null, credentials);
    }

    public Transaction(org.web3j.protocol.core.methods.response.Transaction transaction) {
        this(transaction, null);
    }

    private Transaction(org.web3j.protocol.core.methods.response.Transaction transaction, Credentials credentials) {
        super(credentials);
        if (transaction == null) {
            this.from = credentials.getAddress();
            return;
        }
        this.from = transaction.getFrom();
        this.data = transaction.getInput();
        this.txId = transaction.getHash();
        this.value = transaction.getValue();
        this.gasPrice = Convert.fromWei(new BigDecimal(transaction.getGasPrice()), Convert.Unit.GWEI).toBigInteger();
        this.to = transaction.getTo();
        this.gasUsed = BigInteger.ZERO;
        if (transaction.getBlockNumberRaw() == null) {
            this.blockNumber = BigInteger.ZERO;
        } else {
            this.blockNumber = transaction.getBlockNumber();
        }
    }

    @Override
    public BigInteger getValue() {
        return value;
    }

    @Override
    public CoinAttribute getCoinAttr() {
        return SpringBeanFactoryUtils.getBean(CoinConfig.class).getContractAttrByName(coinName).orElseThrow(() -> new RuntimeException(""));
    }

    @Override
    public synchronized String buildTxId() throws IOException {
        if (rawTx == null) {
            //set rawTx
            GethConfig gethConfig = SpringBeanFactoryUtils.getBean(GethConfig.class);
            rawTx = buildRawTx(gethConfig.getWithdrawGasPrice(),
                    gethConfig.getWithdrawGasLimit(),
                    to, "", value);
            txId = Hash.sha3(rawTx);
        }

        return txId;
    }

    @Override
    public String getTxId() {
        return txId;
    }

    @Override
    public void send() throws IOException {
        if (rawTx == null) {
            throw new NullPointerException("raw transaction is null,please call buildTxId()");
        }
        send(rawTx);
    }

    @Override
    public boolean isConfirmed() {
        return blockNumber.compareTo(BigInteger.ZERO) != 0 && SpringBeanFactoryUtils.getBean(GethConfig.class).isConfirmed(blockNumber);
    }

    public String getFrom() {
        return from;
    }


    public String getTo() {
        return to;
    }


    public BigInteger getBlockNumber() {
        return blockNumber;
    }


    public BigInteger getGasPrice() {
        return gasPrice;
    }


    public synchronized BigInteger getGasUsed() {
        if (BigInteger.ZERO.equals(gasUsed) && isConfirmed()) {
            try {
                return getTransactionReceipt(this.txId).map(tx -> {
                    this.gasUsed = tx.getGasUsed();
                    return this.gasUsed;
                }).orElse(BigInteger.ZERO);
            } catch (IOException e) {
                log.error("", e);
                return BigInteger.ZERO;
            }
        }
        return gasUsed;
    }

    private Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) throws IOException {
        EthGetTransactionReceipt transactionReceipt = SpringBeanFactoryUtils.getBean(Web3j.class).ethGetTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            throw new RuntimeException("Error processing request: " + transactionReceipt.getError().getMessage());
        } else {
            return transactionReceipt.getTransactionReceipt();
        }
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", value=" + value +
                ", blockNumber=" + blockNumber +
                ", txId='" + txId + '\'' +
                ", gasPrice=" + gasPrice +
                ", gasUsed=" + gasUsed +
                ", data='" + data + '\'' +
                '}';
    }
}
