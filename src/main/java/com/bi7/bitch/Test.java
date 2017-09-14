package com.bi7.bitch;

import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.chain.ethereum.ETH;
import com.bi7.bitch.chain.ethereum.ETH2;
import com.bi7.bitch.chain.ethereum.contract.impl.UGT;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.conf.GethConfig;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.service.CoinService;
import com.bi7.bitch.util.DecimalsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    private CoinService coinService;

    @Autowired
    private Web3j web3j;

    @Autowired
    private DecimalsUtil decimalsUtil;

    public void init() throws Exception {
        UGT token = (UGT) CoinName.UGT.getCoin();
        BigInteger b = token.getBalance("0xb549dc36c1035b1d9e5e000602c3800df5a6930f");
//        InputData idata = token.getTransactionById("0xc72bd26962f71b0174714875ab7ae073c5bd9bea9a62897df253e3ab90f0aeb2");

        ETH eth = (ETH) CoinName.ETH.getCoin();
        BigInteger b2 = eth.getBalance("0x2b8bd9aa8c1d4dc69edb24b9b1e7f4bf37f68674");
//        InputData idata2 = eth.getTransactionById("0xaad4949b326b195663b641b99084d8cb5b48d2afba24712ac84bf39c90b10597");


        ETH2 eth2 = new ETH2(web3j, config.getCredentials(), config.getChainId());

//        String rawTx = eth2.send(config.getWithdrawGasPrice(), config.getWithdrawGasLimit(), "0x2b8bd9aa8c1d4dc69edb24b9b1e7f4bf37f68674", "", decimalsUtil.decode("0.863", 18));

//        System.out.println(String.format("txHashId: %s", Hash.sha3(rawTx)));

//        EthSendTransaction ethSendTransaction = eth2.send(rawTx);
//        System.out.println(Convert.fromWei(new BigDecimal((b)), Convert.Unit.ETHER));
//        System.out.println(Convert.fromWei(new BigDecimal(token.getBalance("0x2b8bd9aa8c1d4dc69edb24b9b1e7f4bf37f68674")), Convert.Unit.ETHER));
//        System.out.println(idata.toString());


//        Msg msg = coinService.withdraw(29, 96, "0x2b8bd9aa8c1d4dc69edb24b9b1e7f4bf37f68674", CoinName.UGT, "1.123456", "0.0045");
//        System.out.println(msg.toString());

    }
}
