package com.bi7.bitch.conf;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.ethereum.ETH;
import com.bi7.bitch.chain.ethereum.contract.impl.*;
import com.bi7.bitch.util.DecimalsUtil;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by foxer on 2017/8/21.
 */
//TODO 1表示每种币种的转移最小限额（没有定义）
public enum CoinName {
	
    UGT("ugt", "eths", 50000, 18, 4, new UGT(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1")),
    OMG("omg", "eths", 10000, 18, 4, new OMG(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1")),
    EOS("eos", "eths", 10000, 18, 4, new EOS(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1")),
    QUTM("qutm", "eths", 10000, 18, 4, new QUTM(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1")),
    SNT("snt", "eths", 50000, 18, 4, new QUTM(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1")),
    PAY("pay", "eths", 50000, 18, 4, new PAY(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1")),
    ETH("eth", "eths", 1000, 18, 6, new ETH(
            SpringBeanFactoryUtils.getBean(Web3j.class),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getCredentials(),
            SpringBeanFactoryUtils.getBean(GethConfig.class).getChainId()
    ), new BigInteger("1"));

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
    
    //遍历map的公共方法
    public static void itor(Consumer<CoinName> consumer) {
    	map.values().forEach(consumer);
    }


    CoinName(String coinname, String realCoinName, int withdrawLimit, int decimals, int localDecimals, ICoin coin, BigInteger minTransValue) {
        DecimalsUtil decimalsUtil = SpringBeanFactoryUtils.getBean(DecimalsUtil.class);
        this.coinname = coinname;
        this.realCoinName = realCoinName;
        this.withdrawLimit = decimalsUtil.decode(String.valueOf(withdrawLimit), decimals);
        this.decimals = decimals;
        this.localDecimals = localDecimals;
        this.coin = coin;
        this.minTransValue = minTransValue;
    }

    private String coinname;
    private String realCoinName;
    private BigInteger withdrawLimit;
    private int decimals;
    private int localDecimals;
    private BigInteger minTransValue = new BigInteger("1");
    
    
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

	public BigInteger getMinTransValue() {
		return minTransValue;
	}
    
}
