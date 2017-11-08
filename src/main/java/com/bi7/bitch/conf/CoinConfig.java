package com.bi7.bitch.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by foxer on 2017/11/4.
 */
@Component
public class CoinConfig {

    private final static int DEFAULT_DECIMAL = 18;
    private Map<String, CoinAttribute> contractMapByAddress = new HashMap<>();
    private Map<String, CoinAttribute> contractMapByName = new HashMap<>();
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private GethConfig gethConfig;

    private CoinAttribute ETH;

    //TODO localDecimal 没有实现
    public void init() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(appConfig.getContractAddressConfigPath()));
        Enumeration<Object> enumeration = properties.keys();
        while (enumeration.hasMoreElements()) {
            String coinName = enumeration.nextElement().toString();
            String value = properties.getProperty(coinName);
            int pos = value.indexOf(",");
            if (pos != -1 && pos != 42) {
                System.out.println(String.format("contract address config has error,filePath:%s", appConfig.getContractAddressConfigPath()));
                System.exit(1);
            }
            CoinAttribute attr = new CoinAttribute();
            attr.setName(coinName);
            if (pos != -1) {
                attr.setContractAddress(value.substring(0, pos));
                attr.setDecimal(Integer.parseInt(value.substring(pos)));
            } else {
                attr.setContractAddress(value);
                attr.setDecimal(DEFAULT_DECIMAL);
            }
            contractMapByName.put(coinName, attr);
            if (!coinName.equals("eth")) {
                contractMapByAddress.put(attr.getContractAddress(), attr);
            }
        }

        ETH = new CoinAttribute();
        ETH.setContractAddress("");
        ETH.setDecimal(18);
        ETH.setLocalDecimal(6);
        ETH.setMinTransVal(gethConfig.getEthMinTransVal());
        ETH.setName("eth");
        ETH.setWithdrawLimit(gethConfig.getEthWithdrawLimit());

    }

    public Optional<CoinAttribute> getContractAttrByAddress(String address) {
        return Optional.ofNullable(contractMapByAddress.get(address));
    }

    public Optional<CoinAttribute> getContractAttrByName(String coinName) {
        return Optional.ofNullable(contractMapByName.get(coinName));
    }

    public void itorByName(BiConsumer<String, CoinAttribute> consumer) {
        contractMapByName.forEach(consumer);
    }

    public CoinAttribute getETH() {
        return ETH;
    }
}
