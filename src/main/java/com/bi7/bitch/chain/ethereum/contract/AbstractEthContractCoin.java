package com.bi7.bitch.chain.ethereum.contract;

import com.bi7.bitch.Logs;
import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.conf.GethConfig;
import org.apache.commons.logging.Log;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * Created by foxer on 2017/8/28.
 */
public abstract class AbstractEthContractCoin implements ICoin {

    private final static Log log = Logs.getLogger(AbstractEthContractCoin.class);

    protected String contractAddress;

    protected Token token;

    protected Web3j web3;
    protected Credentials credentials;
    protected GethConfig gethConfig = SpringBeanFactoryUtils.getBean(GethConfig.class);

    public AbstractEthContractCoin(Web3j web3, Credentials credentials, String contractAddress, byte chainId) {
        this.web3 = web3;
        this.credentials = credentials;
        this.contractAddress = contractAddress;
        token = Token.load(contractAddress, web3, new RawTransactionManager(web3, credentials, chainId), gethConfig.getWithdrawGasPrice(), gethConfig.getWithdrawGasLimit());
    }

    protected void deserizeInput(String data, InputData inputData) {
        if (data.substring(0, 10).equals("0xa9059cbb")) {
            String to = data.substring(34, 74);
            String hexTo = Numeric.prependHexPrefix(to);
            String value = data.substring(74);
            BigInteger valueBigInteger = Numeric.toBigInt(value);
            inputData.setTo(hexTo);
            inputData.setValue(valueBigInteger);
        } else {
            log.info("not transfer tx");
        }
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

    public InputData deserizeTransaction(Transaction transaction) {
        String from = transaction.getFrom();
        BigInteger blockNumber;
        if (transaction.getBlockNumberRaw() == null) {
            blockNumber = BigInteger.ZERO;
        } else {
            blockNumber = transaction.getBlockNumber();
        }
        String data = transaction.getInput();
        ContractInputData inputData = new ContractInputData();
        deserizeInput(data, inputData);
        inputData.setFrom(from);
        inputData.setBlockNumber(blockNumber);
        inputData.setSuccess(isSuccess(transaction.getHash()));
        inputData.setTxid(transaction.getHash());
        inputData.setGasPrice(Convert.fromWei(new BigDecimal(transaction.getGasPrice()), Convert.Unit.GWEI).toBigInteger());
        inputData.setGasUsed(transaction.getGas());
        return inputData;
    }


    public BigInteger getBalance(String address) {
        Address addressA = new Address(address);
        Function function = new Function("balanceOf", Arrays.<Type>asList(addressA), Collections.<TypeReference<?>>emptyList());
        String dataHex = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.request.Transaction transaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address, contractAddress, dataHex);
        try {
            org.web3j.protocol.core.methods.response.EthCall ethCall = web3.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
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

//    public String transfer(String fromAddress, String toAddress, BigInteger value) {
//        Address to = new Address(toAddress);
//        Uint256 valueUint256 = new Uint256(value);
//        Function function = new Function("transfer", Arrays.<Type>asList(to, valueUint256), Collections.<TypeReference<?>>emptyList());
//        String dataHex = FunctionEncoder.encode(function);
//        org.web3j.protocol.core.methods.request.Transaction transaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(fromAddress, contractAddress, dataHex);
//        try {
//            org.web3j.protocol.core.methods.response.EthSendTransaction ethSendTransaction = web3.ethSendTransaction(transaction).send();
//            String txHash = ethSendTransaction.getTransactionHash();
//            return txHash;
//        } catch (IOException e) {
//            log.error("IOException while transfer ugt ", e);
//        }
//
//        return "";
//    }

    @Override
    public TransactionReceipt transfer(String toAddress, BigInteger value) throws IOException, InterruptedException, TransactionTimeoutException {
        Future<TransactionReceipt> transactionReceiptFuture = token.transfer(new Address(toAddress), new Uint256(value));
        try {
            return transactionReceiptFuture.get();
        } catch (ExecutionException e) {
            log.error("", e);
            throw new IOException("ExecutionException ", e);
        }
    }

    protected boolean isSuccess(String txid) {
        Request<?, EthGetTransactionReceipt> receiptRequest = web3.ethGetTransactionReceipt(txid);
        try {
            EthGetTransactionReceipt receipt = receiptRequest.send();
            Optional<TransactionReceipt> optional = receipt.getTransactionReceipt();
            if (optional.isPresent()) {
                List<Token.TransferEventResponse> responses = token.getTransferEvents(optional.get());
                return responses.size() > 0;
            }
        } catch (IOException e) {
            log.info("IOExcetion", e);
        }
        return false;
    }

}
