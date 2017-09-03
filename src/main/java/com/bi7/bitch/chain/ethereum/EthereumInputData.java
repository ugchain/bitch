package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.chain.InputData;

import java.math.BigInteger;

/**
 * Created by foxer on 2017/8/28.
 */
public class EthereumInputData extends InputData {
    private BigInteger gasPrice;
    private BigInteger gasUsed;

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String toString() {
        return String.format("%s, gasPrice : %s, gasUsed : %s", super.toString(), gasPrice, gasUsed);
    }

}
