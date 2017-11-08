package com.bi7.bitch.chain;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.conf.CoinAttribute;
import com.bi7.bitch.conf.GethConfig;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by foxer on 2017/11/4.
 */
class Erc20Transaction extends Transaction {

    private CoinAttribute contractAttr;

    public Erc20Transaction(Credentials credentials, CoinAttribute contractAttr) {
        super(credentials);
        this.contractAttr = contractAttr;
    }

    public Erc20Transaction(org.web3j.protocol.core.methods.response.Transaction transaction, CoinAttribute contractAttr) {
        super(transaction);
        this.contractAttr = contractAttr;
        deserizeInput(this.data);
    }

    private void deserizeInput(String data) {
        String to = data.substring(34, 74);
        String hexTo = Numeric.prependHexPrefix(to);
        String value = data.substring(74);
        BigInteger valueBigInteger = Numeric.toBigInt(value);
        this.to = hexTo;
        this.value = valueBigInteger;
    }

    public static boolean isVaildTransfer(String data) {
        return data != null && data.length() > 10 && data.substring(0, 10).equals("0xa9059cbb");
    }

    @Override
    public synchronized String buildTxId() throws IOException {
        //set rawTx
        GethConfig gethConfig = SpringBeanFactoryUtils.getBean(GethConfig.class);
        if (rawTx == null) {
            Address address = new Address(to);
            Uint256 valueUint256 = new Uint256(value);
            Function function = new Function("transfer", Arrays.<Type>asList(address, valueUint256), Collections.<TypeReference<?>>emptyList());
            rawTx = buildRawTx(gethConfig.getWithdrawGasPrice(),
                    gethConfig.getWithdrawGasLimit(),
                    contractAttr.getContractAddress(),
                    FunctionEncoder.encode(function),
                    BigInteger.ZERO
            );
            txId = Hash.sha3(rawTx);
        }
        return txId;
    }

}
