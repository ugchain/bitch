package com.bi7.bitch.conf;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.chain.ethereum.ContractAddress;
import com.bi7.bitch.chain.ethereum.EthereumInputData;
import com.bi7.bitch.chain.ethereum.contract.AbstractEthContractCoin;
import com.bi7.bitch.chain.ethereum.contract.ContractInputData;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.service.CoinService;
import com.bi7.bitch.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by foxer on 2017/8/29.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ScheduledWork {

    @Autowired
    GethConfig gethConfig;

    @Autowired
    AppConfig config;


    @Autowired
    private Web3j web3;

    @Autowired
    private WalletService walletService;


    @Autowired
    private CoinService coinService;

    @Scheduled(fixedRate = 5000)
    public void updateCurrentBlockNumber() {
        Request<?, EthBlockNumber> ethBlockNumberRequest = web3.ethBlockNumber();
        int blockNumber = 0;
        try {
            EthBlockNumber ethBlockNumber = ethBlockNumberRequest.send();
            blockNumber = ethBlockNumber.getBlockNumber().intValue();
        } catch (IOException e) {
            e.printStackTrace();
            Logs.scheduledLogger.error("ethBlockNumberRequest error ", e);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Logs.scheduledLogger.error("", e);
            return;
        }
        coinService.setCurrentBlockNumber(blockNumber);
    }


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
                    //是内部合约
                    if (ContractAddress.isExistContractAddress(transaction.getTo())) {
                        ethContractCharge(transaction);
                        return;
                    }
                    //eth charge
                    ethCharge(transaction);

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
//        System.out.println(String.format("scanBlock : %d",blockNumber));
        Request<?, EthBlock> ethBlockRequest = web3.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), true);
        EthBlock ethBlock = ethBlockRequest.send();
        EthBlock.Block block = ethBlock.getBlock();
        block.getTransactions().forEach(transactionResult -> {
            Transaction tx = ((EthBlock.TransactionObject) transactionResult).get();
//            System.out.println(String.format("txid: %s , to : %s",tx.getHash(),tx.getTo()));
            consumer.accept(tx);
        });
    }

    private void ethContractCharge(Transaction transaction) {
        AbstractEthContractCoin coin = ContractAddress.findCoinInstance(transaction.getTo());

        try {
            Optional<InputData> optInputData = coin.deserizeTransaction(transaction);
            if (!optInputData.isPresent()) {
                Logs.scheduledLogger.debug("not transfer fun call");
                return;
            }
            ContractInputData idata = (ContractInputData) optInputData.get();

            String chargeAddress = idata.getTo();

            BitchWallet bitchWallet = walletService.getBitchWalletByAddress(chargeAddress);

            if (bitchWallet != null) {
                //为了真实的 gasUsed
                Optional<TransactionReceipt> transactionTreceiptOpt = getTransactionReceipt(transaction.getHash());
                if (!transactionTreceiptOpt.isPresent()) {
                    //正常情况下不会发生
                    return;
                }
                idata.setGasUsed(transactionTreceiptOpt.get().getGasUsed());
                coinService.saveCharge(bitchWallet, idata, coin.getCoinName());
                Logs.scheduledLogger.info(String.format("%s charge: %s", coin.toString(), idata.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logs.scheduledLogger.error("", e);
        }
    }

    private void ethCharge(Transaction transaction) {
        try {
            BitchWallet bitchWallet = walletService.getBitchWalletByAddress(transaction.getTo());
            if (bitchWallet == null) {
                return;
            }

            System.out.println(String.format("ethCharge: userid: %d ; address: %s",bitchWallet.getUserid(),bitchWallet.getAddress()));

            //为了真实的 gasUsed
            Optional<TransactionReceipt> transactionTreceiptOpt = getTransactionReceipt(transaction.getHash());
            if (!transactionTreceiptOpt.isPresent()) {
                //正常情况下不会发生
                return;
            }

            EthereumInputData idata = new EthereumInputData();

            idata.setTxid(transaction.getHash());
            idata.setGasPrice(Convert.fromWei(new BigDecimal(transaction.getGasPrice()), Convert.Unit.GWEI).toBigInteger());
            idata.setGasUsed(transactionTreceiptOpt.get().getGasUsed());
            idata.setBlockNumber(transactionTreceiptOpt.get().getBlockNumber());
            idata.setFrom(transaction.getFrom());
            idata.setTo(transaction.getTo());
            idata.setValue(transaction.getValue());

            coinService.saveCharge(bitchWallet, idata, CoinName.ETH);
            Logs.scheduledLogger.info(String.format("ETH charge: %s", idata.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            Logs.scheduledLogger.error("", e);
        }
    }


    //TODO WARNING 重复 在 CoinService 里也有
    private Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) throws IOException {
        EthGetTransactionReceipt transactionReceipt = (EthGetTransactionReceipt) this.web3.ethGetTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            throw new RuntimeException("Error processing request: " + transactionReceipt.getError().getMessage());
        } else {
            return transactionReceipt.getTransactionReceipt();
        }
    }

}
