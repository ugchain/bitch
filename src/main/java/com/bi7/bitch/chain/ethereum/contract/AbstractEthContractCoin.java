package com.bi7.bitch.chain.ethereum.contract;

import com.bi7.bitch.Logs;
import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.conf.GethConfig;
import com.bi7.web3j.tx.AsyncTransfer;
import com.bi7.web3j.tx.LocalTransaction;
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
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by foxer on 2017/8/28.
 */
public abstract class AbstractEthContractCoin extends AsyncTransfer implements ICoin {

    private final static Log log = Logs.getLogger(AbstractEthContractCoin.class);

    protected String contractAddress;

    protected Token token;

    protected Web3j web3;
    protected Credentials credentials;
    protected GethConfig gethConfig = SpringBeanFactoryUtils.getBean(GethConfig.class);

    public AbstractEthContractCoin(Web3j web3, Credentials credentials, String contractAddress, byte chainId) {
        super(web3, credentials, chainId);
        this.web3 = web3;
        this.credentials = credentials;
        this.contractAddress = contractAddress;
        token = Token.load(contractAddress, web3, new RawTransactionManager(web3, credentials, chainId), gethConfig.getWithdrawGasPrice(), gethConfig.getWithdrawGasLimit());
    }

    protected boolean deserizeInput(String data, InputData inputData) {
        if (data != null && data.length() > 10 && data.substring(0, 10).equals("0xa9059cbb")) {
            String to = data.substring(34, 74);
            String hexTo = Numeric.prependHexPrefix(to);
            String value = data.substring(74);
            BigInteger valueBigInteger = Numeric.toBigInt(value);
            inputData.setTo(hexTo);
            inputData.setValue(valueBigInteger);
            return true;
        } else {
            return false;
        }
    }

    public Optional<InputData> getTransactionById(String transactionHash) {
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
        return Optional.empty();
    }

    public Optional<InputData> deserizeTransaction(Transaction transaction) {
        String from = transaction.getFrom();
        BigInteger blockNumber;
        if (transaction.getBlockNumberRaw() == null) {
            blockNumber = BigInteger.ZERO;
        } else {
            blockNumber = transaction.getBlockNumber();
        }
        String data = transaction.getInput();
        ContractInputData inputData = new ContractInputData();
        if (!deserizeInput(data, inputData)) {
            return Optional.empty();
        }
        inputData.setFrom(from);
        inputData.setBlockNumber(blockNumber);
        inputData.setTxid(transaction.getHash());
        inputData.setGasPrice(Convert.fromWei(new BigDecimal(transaction.getGasPrice()), Convert.Unit.GWEI).toBigInteger());
        inputData.setGasUsed(transaction.getGas());
        return Optional.of(inputData);
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


    public LocalTransaction buildTx(String toAddress, BigInteger value) throws IOException {
        Address to = new Address(toAddress);
        Uint256 valueUint256 = new Uint256(value);
        Function function = new Function("transfer", Arrays.<Type>asList(to, valueUint256), Collections.<TypeReference<?>>emptyList());
        return new LocalTransaction(this, gethConfig.getWithdrawGasPrice(), gethConfig.getWithdrawGasLimit(), this.getFromAddress(), contractAddress, FunctionEncoder.encode(function), BigInteger.ZERO);
    }
}
