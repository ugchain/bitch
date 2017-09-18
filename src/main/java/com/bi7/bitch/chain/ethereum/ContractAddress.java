package com.bi7.bitch.chain.ethereum;

import com.bi7.bitch.SpringBeanFactoryUtils;
import com.bi7.bitch.chain.ethereum.contract.AbstractEthContractCoin;
import com.bi7.bitch.chain.ethereum.contract.impl.*;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ChainId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by foxer on 2017/8/26.
 */
public final class ContractAddress {
    private final static Map<String, String> addressMap = new HashMap<String, String>() {
        {
            put("ugt", "0x43eE79e379e7b78D871100ed696e803E7893b644".toLowerCase());
//            put("ugt", "0xb19e56471c4edcd9edb6fed0ca3583f5dfc09c62"); // debug local
            put("omg", "0xd26114cd6EE289AccF82350c8d8487fedB8A0C07".toLowerCase());
            put("eos", "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0".toLowerCase());
            put("qutm", "0x9a642d6b3368ddc662CA244bAdf32cDA716005BC".toLowerCase());
            put("snt", "0x744d70FDBE2Ba4CF95131626614a1763DF805B9E".toLowerCase());
            put("pay", "0xB97048628DB6B661D4C2aA833e95Dbe1A905B280".toLowerCase());

        }
    };
    private final static List<String> list = new ArrayList<>(addressMap.size());
    private final static Map<String, String> map = new HashMap<>();
    private final static Map<String, AbstractEthContractCoin> contractCoinMap = new HashMap<String, AbstractEthContractCoin>() {
        {
            put(addressMap.get("ugt"), new UGT(SpringBeanFactoryUtils.getBean(Web3j.class), null, ChainId.NONE));
            put(addressMap.get("omg"), new OMG(SpringBeanFactoryUtils.getBean(Web3j.class), null, ChainId.NONE));
            put(addressMap.get("eos"), new EOS(SpringBeanFactoryUtils.getBean(Web3j.class), null, ChainId.NONE));
            put(addressMap.get("qutm"), new QUTM(SpringBeanFactoryUtils.getBean(Web3j.class), null, ChainId.NONE));
            put(addressMap.get("snt"), new SNT(SpringBeanFactoryUtils.getBean(Web3j.class), null, ChainId.NONE));
            put(addressMap.get("pay"), new PAY(SpringBeanFactoryUtils.getBean(Web3j.class), null, ChainId.NONE));
        }
    };

    static {
        addressMap.forEach((key, val) -> {
            list.add(val);
            map.put(val, key);
        });

    }

    public static void itor(Consumer<String> consumer) {
        list.forEach(consumer);
    }

    public static boolean isExistContractAddress(String contractAddress) {
        return map.containsKey(contractAddress);
    }

    public static AbstractEthContractCoin findCoinInstance(String contractAddress) {
        return contractCoinMap.get(contractAddress);
    }

    public static String findAddress(String tokenName) {
        return addressMap.get(tokenName.toLowerCase());
    }
}
