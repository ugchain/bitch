package com.bi7.bitch;

import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.chain.ethereum.contract.impl.UGT;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.conf.GethConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;

/**
 * Created by foxer on 2017/8/22.
 */
@Service
public class Test {
    public static void main(String[] args) throws Exception {

        String fileName = null;
        try {
            fileName = WalletUtils.generateFullNewWalletFile("123", new File("/Users/foxer/bi7-apps/bitch/logs/"));
        } catch (Exception e) {
            return;
        }
        String address = null;
        File keystroreFile = new File("/Users/foxer/bi7-apps/bitch/logs/", fileName);
        try {
            Credentials credentials = WalletUtils.loadCredentials("123", keystroreFile);
            address = credentials.getAddress();
        } catch (Exception e) {
            return;
        }

        try {
            String content = getFileContent(keystroreFile);
            String hashValue = Hash.sha3(content);
            Logs.addressCreateLogger.info(String.format("createAddress| address: %s  sha3: %s", hashValue, address));
        } catch (Exception e) {
        }
    }

    private static String getFileContent(File file) throws Exception {
        try {
            return String.join("", Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            throw new Exception("Failed to read content from file '" + file.getAbsolutePath() + "'", e);
        }
    }

    @Autowired
    private GethConfig config;

    @Autowired
    private Web3j web3j;

    public void init() {
        UGT token = (UGT) CoinName.UGT.getCoin();
        String b = token.getBalance("0x45326b4c3D46B621aCc432ebE3B30583eB48CAb7");
        InputData idata = token.getTransactionById("0x464501bb15d00d2a9e904cd5d9d6c9fc9536e84e10f2bc434fae83f222119245");

        System.out.println(Convert.fromWei(new BigDecimal(Numeric.toBigInt(b)), Convert.Unit.ETHER));
        System.out.println(idata.toString());
    }
}
