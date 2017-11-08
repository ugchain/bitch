package com.bi7.bitch.service;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.Chains;
import com.bi7.bitch.chain.Transaction;
import com.bi7.bitch.conf.CoinAttribute;
import com.bi7.bitch.conf.CoinConfig;
import com.bi7.bitch.conf.GethConfig;
import com.bi7.bitch.dao.CoinDao;
import com.bi7.bitch.dao.TxDao;
import com.bi7.bitch.dao.model.BitchTx;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.util.DecimalsUtil;
import com.bi7.bitch.util.WalletUtil;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by foxer on 2017/10/31.
 */

@Service
public class TxService {

    private final static Log log = Logs.getLogger(TxService.class);

    //TODO eth系列的汇总地址应该由配置文件决定
    private final static String ETHS_TOTAL_ADDRESS = "";

    //TODO 合约币账户上可转移的最少的eth余额
    private final static BigInteger ETH_MIN_USABLE_BALANCE = new BigInteger("1");

    //TODO eth系列的充值地址
    private static final String ETHS_ADD_ACCOUNT_ADDRESS = "";
    //TODO MIN_TRANS_ETH
    private static final String MIN_TRANS_ETH = null;

    @Autowired
    private TxDao txDao;

    @Autowired
    private Web3j web3;

    @Autowired
    private DecimalsUtil decimalsUtil;

    @Autowired
    private CoinDao coinDao;

    @Autowired
    private GethConfig gethConfig;

    @Autowired
    private CoinConfig coinConfig;

    @Autowired
    private Chains chains;

//    /**
//     * 扫描bitchWallet
//     */
//    public void scanBitchWallet(String coinname) {
//        List<BitchWallet> bitchWallets;
//        try {
//            bitchWallets = txDao.selectBitchWallet(coinname);
//        } catch (Exception e) {
//            log.error("", e);
//            throw new RuntimeException();
//        }
//
//        bitchWallets.forEach(bitchWallet -> {
//            coinConfig.itorByName((coinName, coinAttr) -> {
//                //先处理合约币
//                if ("eth".equals(coinName)) {
//                    return;
//                }
//                collectCoin(bitchWallet, coinAttr);
//            });
//
//            collectCoin(bitchWallet, coinConfig.getETH());
//        });
//    }

//    private void collectCoin(BitchWallet bitchWallet, CoinAttribute coinAttr) {
//        BigInteger balance = chains.getBalance(bitchWallet.getAddress(), coinAttr);
//        if (balance.compareTo(coinAttr.getMinTransVal()) < 0) {
//            return;
//        }
//        if (!hasEthUsableBalances(bitchWallet.getAddress())) {
//            log.debug(String.format("hasEthUsableBalances return false, addrss: %s", bitchWallet.getAddress()));
//            return;
//        }
//        try {
//            if (hasUnconfirmTrans(bitchWallet.getAddress())) {
//                return;
//            }
//        } catch (Exception e) {
//            log.error("", e);
//            return;
//        }
//        //将代币转入汇总地址
//        Credentials credentials;
//        try {
//            credentials = WalletUtil.loadCredentials(gethConfig.getKeystorePassPhrase(), bitchWallet.getKeystore().getBytes());
//        } catch (IOException | CipherException ioe) {
//            log.error("", ioe);
//            return;
//        }
//
//        Transaction transaction = chains.newInstance(coinAttr, credentials);
//        transaction.setTo(ETHS_TOTAL_ADDRESS);
//        transaction.setGasPrice(gethConfig.getWithdrawGasPrice());
//        transaction.setBlockNumber(new BigInteger("0"));
//        transaction.setValue(balance);
//
//        try {
//            transaction.buildTxId();
//
//            saveBitchTx(transaction);
//
//            transaction.send();
//
//        } catch (Exception e) {
//            log.error(e);
//            throw new RuntimeException();
//        }
//
//    }

    private void saveBitchTx(Transaction transaction) throws Exception {
        BitchTx tx = new BitchTx();
        tx.setBlockNumber(0);
        tx.setCoinname(transaction.getCoinAttr().getName());
        tx.setFrom(transaction.getFrom());
        tx.setTo(transaction.getTo());
        tx.setGasPrice(transaction.getGasPrice().intValue());
        tx.setTxid(transaction.getTxId());
        tx.setValue(transaction.getValue().toString());
        tx.setTime(new Date());
        txDao.saveTxToBitchTx(tx);
    }

//    /**
//     * 判断和汇总地址是否有未确认的交易
//     * //TODO why ETHS_TOTAL_ADDRESS ?
//     */
//    private boolean hasUnconfirmTrans(String address) throws Exception {
//        List<BitchTx> txInfoList = txDao.txInfoByFromAddress(address);
//
//        for (BitchTx tx : txInfoList) {
//            if (!isBlockConfirmed(tx.getBlockNumber())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 查看合约币账户上是否有可用的eth
//     *
//     * @return true:usable false:unusable
//     */
//    private boolean hasEthUsableBalances(String address) {
//        BigInteger balance = chains.getBalance(address);
//        return balance.compareTo(TxService.ETH_MIN_USABLE_BALANCE) > 0;
//    }
//
//    public void scanTableToUpdate() {
//        List<BitchTx> selectBitchTx = null;
//        try {
//            selectBitchTx = txDao.selectUnPackageTx();
//        } catch (Exception e) {
//            log.error(e);
//            e.printStackTrace();
//            return;
//        }
//
//        selectBitchTx.forEach(tx -> {
//            chains.getTransactionById(tx.getTxid()).filter(transaction ->
//                    gethConfig.isConfirmed(transaction.getBlockNumber())
//            ).ifPresent(transaction -> {
//                tx.setBlockNumber(transaction.getBlockNumber().intValue());
//                tx.setGasUsed(transaction.getGasUsed().intValue());
//                try {
//                    txDao.upTxStatus(tx);
//                } catch (Exception e) {
//                    log.error("upTxStatus error " + e);
//                }
//            });
//        });
//
//    }
//
//    /**
//     * 扫描bitch-wallet，给eth不足的账户充值eth，用于转移合约币
//     */
//    public void scanTableToAddEth() {
//        List<BitchWallet> bitchWallets = null;
//        try {
//            bitchWallets = txDao.selectAllBitchWallet();
//        } catch (Exception e) {
//            log.error(e);
//            throw new RuntimeException();
//        }
//
//        bitchWallets.forEach(bitchWallet -> {
//            coinConfig.itorByName((coinName, coinAttr) -> {
//                if (coinName == CoinName.ETH) {
//                    return;
//                }
//                BigInteger balance = coinName.getCoin().getBalance(bitchWallet.getAddress());
//
//                //如果balance > 设置的最小值
//                if (balance.compareTo(coinName.getMinTransValue()) >= 0) {
//                    //检查eth数量够不够
//                    if (!hasEthUsableBalances(bitchWallet.getAddress())) {
//                        boolean hasUnconfirmTrans;
//                        try {
//                            hasUnconfirmTrans = hasUnconfirmTransPreAdd(bitchWallet.getAddress());
//                        } catch (Exception e) {
//                            log.error(e);
//                            throw new RuntimeException();
//                        }
//
//                        if (hasUnconfirmTrans) {
//                            return;
//                        }
//
//                        //执行转入eth操作
//                        String privateKey = "";
//                        int gasPrice = 0;
//                        int gasLimit = 0;
//                        Credentials credentials = Credentials.create(privateKey);
//
//                        try {
//                            innerTrans(ETHS_ADD_ACCOUNT_ADDRESS, bitchWallet.getAddress(), MIN_TRANS_ETH, gasPrice, gasLimit, coinName, credentials);
//                        } catch (Exception e) {
//                            log.error(e);
//                            throw new RuntimeException();
//                        }
//                    }
//                }
//            });
//        });
//    }


}
