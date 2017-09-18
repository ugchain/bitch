package com.bi7.bitch.conf;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.ethereum.ETH;
import com.bi7.bitch.chain.ethereum.contract.impl.*;
import com.bi7.bitch.util.DecimalsUtil;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by foxer on 2017/8/21.
 */
public enum CoinName {
    UGT("ugt", "eths", 50000, 18, 4, new UGT(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    )),
    OMG("omg", "eths", 10000, 18, 4, new OMG(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    )),
    EOS("eos", "eths", 10000, 18, 4, new EOS(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    )),
    QUTM("qutm", "eths", 10000, 18, 4, new QUTM(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    )),
    SNT("snt", "eths", 50000, 18, 4, new QUTM(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    )),
    PAY("pay", "eths", 50000, 18, 4, new PAY(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    )),
    ETH("eth", "eths", 1000, 18, 6, new ETH(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ));

    private static Map<String, CoinName> map = new HashMap<>();

    static {
        map.put(CoinName.ETH.coinname, CoinName.ETH);
        map.put(CoinName.UGT.coinname, CoinName.UGT);
        map.put(CoinName.OMG.coinname, CoinName.OMG);
        map.put(CoinName.EOS.coinname, CoinName.EOS);
        map.put(CoinName.QUTM.coinname, CoinName.QUTM);
        map.put(CoinName.SNT.coinname, CoinName.SNT);
        map.put(CoinName.PAY.coinname, CoinName.PAY);
    }


    CoinName(String coinname, String realCoinName, int withdrawLimit, int decimals, int localDecimals, ICoin coin) {
        DecimalsUtil decimalsUtil = SpringBeanFactoryUtils.getBean(DecimalsUtil.class);
        this.coinname = coinname;
        this.realCoinName = realCoinName;
        this.withdrawLimit = decimalsUtil.decode(String.valueOf(withdrawLimit), decimals);
        this.decimals = decimals;
        this.localDecimals = localDecimals;
        this.coin = coin;


    }

    private String coinname;
    private String realCoinName;
    private BigInteger withdrawLimit;
    private int decimals;
    private int localDecimals;

    public ICoin getCoin() {
        return coin;
    }

    private ICoin coin;

    public int getDecimals() {
        return decimals;
    }

    public BigInteger getWithdrawLimit() {
        return withdrawLimit;
    }

    public String getCoinName() {
        return this.coinname;
    }

    public String getRealCoinName() {
        return this.realCoinName;
    }

    public static CoinName get(String coinname) {
        return map.get(coinname);
    }

    public int getLocalDecimals() {
        return localDecimals;
    }
}
