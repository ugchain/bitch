package com.bi7.bitch.conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Files;

import java.io.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by foxer on 2017/8/23.
 */
@Configuration
@ConfigurationProperties(prefix = "geth")
public class GethConfig {

    private final static Log log = LogFactory.getLog(GethConfig.class);

    @Autowired
    AppConfig config;

    private String ethHttpUrl;
    private String keystoreFilesDirectory;
    private String keystorePassPhrase;
    private String blockNumberFilePath;
    private int startBlockNumber;
    private String etcHttpUrl;
    private BigInteger withdrawGasPrice;
    private BigInteger withdrawGasLimit;

    private byte chainId = ChainId.MAIN_NET;

    public byte getChainId() {
        return chainId;
    }

    private Credentials credentials;


    public void init() throws Exception {
        System.out.println(String.format("GethConfig.init    gasPrice: %s, gasLimit: %s", getWithdrawGasPrice().toString(), getWithdrawGasLimit().toString()));

        String blockNumberStr = null;
        try {
            FileReader fr = new FileReader(blockNumberFilePath);
            BufferedReader br = new BufferedReader(fr);
            String _str = null;
            while ((_str = br.readLine()) != null) {
                blockNumberStr = _str;
            }
            br.close();
            fr.close();
            ;
            String[] arr = blockNumberStr.split("\t");
            if (arr.length == 2) {
                startBlockNumber = Integer.parseInt(arr[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("START scan blockchain from block:" + startBlockNumber);

        //2017.08.26 afternoon
        //TODO for DEBUG
//        if (startBlockNumber < 4204540) {
//            System.out.println("SYSTEM exit , startBlockNumber < 4204540");
//            System.exit(1);
//        }

    }

    public String getEthHttpUrl() {
        return ethHttpUrl;
    }

    public void setEthHttpUrl(String ethHttpUrl) {
        this.ethHttpUrl = ethHttpUrl;
    }

    public String getEtcHttpUrl() {
        return etcHttpUrl;
    }

    public void setEtcHttpUrl(String etcHttpUrl) {
        this.etcHttpUrl = etcHttpUrl;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentialsPath) {
        try {
            credentials = WalletUtils.loadCredentials(this.getKeystorePassPhrase(), credentialsPath);
        } catch (IOException e) {
            log.error("", e);
            e.printStackTrace();
            System.exit(1);
        } catch (CipherException e) {
            e.printStackTrace();
            log.error("", e);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            System.exit(1);
        }
    }

    public BigInteger getWithdrawGasPrice() {
        return withdrawGasPrice;
    }

    public void setWithdrawGasPrice(String withdrawGasPrice) {
        this.withdrawGasPrice = Convert.toWei(withdrawGasPrice, Convert.Unit.GWEI).toBigIntegerExact();
    }

    public BigInteger getWithdrawGasLimit() {
        return withdrawGasLimit;
    }

    public void setWithdrawGasLimit(String withdrawGasLimit) {
        this.withdrawGasLimit = new BigInteger(withdrawGasLimit);
    }

    @Bean
    public Web3j getWeb3() {
        return org.web3j.protocol.Web3j.build(new HttpService(getEthHttpUrl()));
    }

    public int getStartBlockNumber() {
        return startBlockNumber;
    }

    public String getKeystorePassPhrase() {
        return keystorePassPhrase;
    }

    public void setKeystorePassPhrase(String keystorePassPhrase) {
        String ret = null;
        try {
            ret = Files.readString(new File(keystorePassPhrase));
            if (ret == null) {
                throw new Exception(String.format("fileRead error keystorePassPhrasePath: %s", keystorePassPhrase));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.keystorePassPhrase = ret.trim();
    }

    public void saveLastedBlockNumber(int blockNumber) {
        if (startBlockNumber == blockNumber) {
            return;
        }
        startBlockNumber = blockNumber;
        if (config.isTesting()) {
            return;
        }
        try {
            FileWriter fw = new FileWriter(blockNumberFilePath, true);
            fw.write(String.format("%s\t%d\n", new Date().toString(), blockNumber));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getKeystoreFilesDirectory() {
        return keystoreFilesDirectory;
    }

    public void setKeystoreFilesDirectory(String keystoreFilesDirectory) {
        this.keystoreFilesDirectory = keystoreFilesDirectory;
    }

    public String getBlockNumberFilePath() {
        return blockNumberFilePath;
    }

    public void setBlockNumberFilePath(String blockNumberFilePath) {
        this.blockNumberFilePath = blockNumberFilePath;
    }


}
