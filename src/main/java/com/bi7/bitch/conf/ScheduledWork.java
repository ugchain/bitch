package com.bi7.bitch.conf;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.Chains;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.service.CoinService;
import com.bi7.bitch.service.TxService;
import com.bi7.bitch.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * Created by foxer on 2017/8/29.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ScheduledWork {

    @Autowired
    private GethConfig gethConfig;

    @Autowired
    AppConfig config;


    @Autowired
    private Web3j web3;

    @Autowired
    private Chains chains;

    @Autowired
    private WalletService walletService;


    @Autowired
    private CoinService coinService;

    @Autowired
    private TxService txService;

//
//    /**
//     * 定时扫描bitch-tx表，查看blockNumber=0(未打包的)的tx，进行处理
//     */
//    @Scheduled(fixedRate = 5000)
//    public void scanTableToUpdate() {
//        txService.scanTableToUpdate();
//    }
//
//    /**
//     * 定时扫描bitch-wallet表，没有eth币的账户打币
//     */
//    @Scheduled(fixedRate = 5000)
//    public void scanTableToAddEth() {
//        txService.scanTableToAddEth();
//    }

    /*
    scan bitch_coin
    update blockNumer and status  where txid
     */
    @Scheduled(fixedRate = 5000)
    public void updateStatus() {

        try {
            coinService.scanChargeCoin();
        } catch (Exception ee) {
            Logs.scheduledLogger.error("", ee);
        }

        try {
            coinService.scanWithdrawCoin();
        } catch (Exception ee) {
            Logs.scheduledLogger.error("", ee);
        }

    }

    /*
    watch blockchain
    scan bitch_wallet
    insert
     */
    @Scheduled(fixedRate = 5000)
    public void charge() {
        Request<?, EthBlockNumber> ethBlockNumberRequest = web3.ethBlockNumber();
        int blockNumber = 0;
        try {
            EthBlockNumber ethBlockNumber = ethBlockNumberRequest.send();
            blockNumber = ethBlockNumber.getBlockNumber().intValue();
        } catch (IOException e) {
            Logs.scheduledLogger.error("ethBlockNumberRequest error ", e);
            return;
        } catch (Exception e) {
            Logs.scheduledLogger.error("", e);
            return;
        }
        //每次回退12个块开始扫描
        //导致大量重复
        int currentBlocknumber = Math.max(gethConfig.getStartBlockNumber() - 12, 1);
        try {
            while (blockNumber > currentBlocknumber) {
                scanBlock(BigInteger.valueOf(currentBlocknumber), transaction -> {
                    com.bi7.bitch.chain.ITransaction tx = chains.getTransaction(transaction);
                    BitchWallet bitchWallet;
                    try {
                        bitchWallet = walletService.getBitchWalletByAddress(tx.getTo());
                        if (bitchWallet == null) {
                            return;
                        }
                    } catch (Exception e) {
                        Logs.scheduledLogger.error("", e);
                        return;
                    }

                    if (coinService.saveCharge(bitchWallet, tx)) {
                        Logs.scheduledLogger.info(String.format("%s charge: %s", tx.getCoinAttr().getName(), tx.toString()));
                    }

                });
                currentBlocknumber++;
            }
        } catch (Exception ee) {
            Logs.scheduledLogger.error("", ee);
            //一旦发生异常，立即回退
            return;
        }

         /*
        save currentBlockNumber to blocknumber.data
         */
        gethConfig.saveLastedBlockNumber(currentBlocknumber);
    }

    private void scanBlock(BigInteger blockNumber, Consumer<Transaction> consumer) throws IOException {
        Request<?, EthBlock> ethBlockRequest = web3.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), true);
        EthBlock ethBlock = ethBlockRequest.send();
        EthBlock.Block block = ethBlock.getBlock();
        block.getTransactions().forEach(transactionResult -> {
            Transaction tx = ((EthBlock.TransactionObject) transactionResult).get();
            consumer.accept(tx);
        });
    }
}
