package com.bi7.bitch.service;

import com.bi7.bitch.Logs;
import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.conf.GethConfig;
import com.bi7.bitch.dao.model.BitchWallet;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by foxer on 2017/8/22.
 */
@Service
public class Web3jService {

    private final static Log log = Logs.getLogger(Web3jService.class);

    @Autowired
    private AppConfig config;

    @Autowired
    private GethConfig gethConfig;

    @Autowired
    private Web3j web3;

    public BitchWallet createAddress(int userId, CoinName coinname) {
        String fileName = null;
        try {
            fileName = WalletUtils.generateFullNewWalletFile(gethConfig.getKeystorePassPhrase(), new File(gethConfig.getKeystoreFilesDirectory()));
        } catch (Exception e) {
            log.error("generateFullNewWalletFile error ", e);
            return null;
        }
        String address = null;
        File keystroreFile = new File(gethConfig.getKeystoreFilesDirectory(), fileName);
        try {
            Credentials credentials = WalletUtils.loadCredentials(gethConfig.getKeystorePassPhrase(), keystroreFile);
            address = credentials.getAddress();
        } catch (Exception e) {
            log.error("loadCredentials error", e);
            return null;
        }

        BitchWallet bitchWallet = new BitchWallet();
        bitchWallet.setAddtime(new Date());
        bitchWallet.setCoinname(coinname.getRealCoinName());
        bitchWallet.setUserid(userId);
        bitchWallet.setAddress(address);
        bitchWallet.setFilename(keystroreFile.getName());
        try {
            String content = getFileContent(keystroreFile);
            bitchWallet.setSha3(Hash.sha3(content));
            bitchWallet.setKeystore(content);
            Logs.addressCreateLogger.info(String.format("createAddress| userid: %s|coinname: %s| address: %s| sha3: %s",
                    userId, coinname.getCoinName(), address, bitchWallet.getSha3()));
        } catch (Exception e) {
            log.error("", e);
            return null;
        }

        return bitchWallet;
    }

//    public EthereumInputData sendTransaction(String to, CoinName coinName, BigInteger value) throws Exception {
//        TransactionReceipt ret = coinName.getCoin().transfer(to, value);
//        EthereumInputData idata = new EthereumInputData();
//        idata.setFrom(ret.getFrom());
//        idata.setTo(to);
//        idata.setBlockNumber(ret.getBlockNumber());//unknown
//        idata.setTxid(ret.getTransactionHash());
//        idata.setValue(value);
//        idata.setGasUsed(ret.getGasUsed());
//        idata.setGasPrice(gethConfig.getWithdrawGasPrice());
//        return idata;
//    }

    private String getFileContent(File file) throws Exception {
        try {
            return String.join("", Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            throw new Exception("Failed to read content from file '" + file.getAbsolutePath() + "'", e);
        }
    }
}
