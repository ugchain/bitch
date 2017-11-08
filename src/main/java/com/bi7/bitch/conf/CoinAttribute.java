package com.bi7.bitch.conf;

import com.bi7.bitch.util.DecimalsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

/**
 * Created by foxer on 2017/11/4.
 */
public class CoinAttribute {

    private String name;
    private String contractAddress;
    private BigInteger withdrawLimit;
    private int decimal;
    private int localDecimal;
    private BigInteger minTransVal;

    @Autowired
    private DecimalsUtil decimalsUtil;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public int getLocalDecimal() {
        return localDecimal;
    }

    public void setLocalDecimal(int localDecimal) {
        this.localDecimal = localDecimal;
    }

    public BigInteger getWithdrawLimit() {
        return withdrawLimit;
    }

    public void setWithdrawLimit(BigInteger withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public BigInteger getMinTransVal() {
        return minTransVal;
    }

    public void setMinTransVal(BigInteger minTransVal) {
        this.minTransVal = minTransVal;
    }

    public String getRealCoinName() {
        return "eths";
    }

    @Override
    public String toString() {
        return "CoinAttribute{" +
                "name='" + name + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", withdrawLimit=" + withdrawLimit +
                ", decimal=" + decimal +
                ", localDecimal=" + localDecimal +
                ", minTransVal=" + minTransVal +
                ", decimalsUtil=" + decimalsUtil +
                '}';
    }
}
