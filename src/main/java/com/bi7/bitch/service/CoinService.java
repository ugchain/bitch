package com.bi7.bitch.service;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.chain.ethereum.ContractAddress;
import com.bi7.bitch.chain.ethereum.EthereumInputData;
import com.bi7.bitch.chain.ethereum.contract.AbstractEthContractCoin;
import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.dao.CoinDao;
import com.bi7.bitch.dao.model.*;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.response.Status;
import com.bi7.bitch.util.DecimalsUtil;
import com.bi7.web3j.tx.LocalTransaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * Created by foxer on 2017/8/24.
 */
@Service
public class CoinService {
    private final static Log log = LogFactory.getLog(CoinService.class);

    @Autowired
    private AppConfig config;

    @Autowired
    private CoinDao coinDao;

    @Autowired
    private DecimalsUtil decimalsUtil;

    @Autowired
    private Web3j web3;

    private final static int SAFETY_ETH_BLOCKNUMBER = 10;

    private final static long TX_EXPIRED_TIME = 60 * 60 * 1000;// 1 hour

    //最新块高
    private int currentBlockNumber;

    public void setCurrentBlockNumber(int currentBlockNumber) {
        this.currentBlockNumber = currentBlockNumber;
    }

    public int getCurrentBlockNumber() {
        return currentBlockNumber;
    }

    public Msg withdraw(int zcId, int userid, String address, CoinName coinname, String value, String fee) {
        BigInteger val = null;
        try {
            val = decimalsUtil.decode(value, coinname.getDecimals());
            if (val.compareTo(coinname.getWithdrawLimit()) > 0) {
                throw new Exception(String.format("withdraw value > limit,value: %s, fee: %s, limit: %d", val, fee, coinname.getWithdrawLimit()));
            }
        } catch (Exception e) {
            log.error("", e);
            return Msg.ERROR;
        }
        BigInteger feeExact = decimalsUtil.decode(fee, coinname.getDecimals());

        LocalTransaction localTx = null;

        try {
            localTx = coinname.getCoin().buildTx(address, val);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("", e);
            return Msg.ERROR;
        }

        BitchCoin bitchCoin = new BitchCoin();
        bitchCoin.setRid(zcId);
        bitchCoin.setUserid(userid);
        bitchCoin.setCoinname(coinname.getCoinName());
        bitchCoin.setType(CoinTypeEnum.WITHDRAW.getId());
        bitchCoin.setFrom(localTx.getFrom());
        bitchCoin.setTo(address);
        bitchCoin.setValue(val.toString());
        bitchCoin.setFee(feeExact.toString());
        bitchCoin.setBlockNumber(0);
//        bitchCoin.setGasUsed(idata.getGasUsed().intValue());
        bitchCoin.setGasPrice(localTx.getGasPriceGWei());
        bitchCoin.setStatus(WithdrawStatusEnum.PENDING.getId());
        try {
            bitchCoin.setTxid(localTx.getTxId());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("", e);
            return Msg.ERROR;
        }
        Date date = new Date();
        bitchCoin.setAddtime(date);
        bitchCoin.setUpdatetime(date);

        try {
            coinDao.saveWithdraw(bitchCoin);
        } catch (Exception e) {
            log.error("", e);
            return Msg.ERROR;
        }

        try {
            localTx.send();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("", e);
            return Msg.ERROR;
        }
        return new Msg(Status.OK);
    }

    //for scheduled work
    public void saveCharge(BitchWallet bitchWallet, EthereumInputData idata, CoinName coinName) {

        if (coinDao.exist(idata.getTxid())) {
            return;
        }
        BitchCoin bitchCoin = new BitchCoin();

        bitchCoin.setUserid(bitchWallet.getUserid());
        bitchCoin.setCoinname(coinName.getCoinName());
        bitchCoin.setType(CoinTypeEnum.CHARGE.getId());
        bitchCoin.setFrom(idata.getFrom());
        bitchCoin.setTo(idata.getTo());
        bitchCoin.setValue(idata.getValue().toString());
        bitchCoin.setFee("0");
        bitchCoin.setBlockNumber(idata.getBlockNumber().intValue());
        bitchCoin.setTxid(idata.getTxid());
        bitchCoin.setGasPrice(idata.getGasPrice().intValue());
        bitchCoin.setGasUsed(idata.getGasUsed().intValue());
        bitchCoin.setStatus(ChargeStatusEnum.PENDING.getId());
        bitchCoin.setAddtime(new Date());

        coinDao.saveCharge(bitchCoin, coinName);
    }


    //数据库中存在的，链上肯定存在，否则立即设为 FAILURE
    public void scanChargeCoin() {
        List<BitchCoin> bitchCoins = coinDao.findAll(CoinTypeEnum.CHARGE, ChargeStatusEnum.PENDING.getId());
        bitchCoins.forEach(bitchCoin -> {

            Optional<TransactionReceipt> optTrans = getTransactionReceipt(bitchCoin.getTxid());
            if (!optTrans.isPresent()) {
                //交易消失，设置为 失败
                coinDao.chargeFailure(bitchCoin.getRid(), ChargeStatusEnum.FAILURE.getId());
                return;
            }
            //TODO WARNING 获取TransactionReceipt几乎没有意义，充值表入库的时候已经有 真实 gas消耗数据了
            TransactionReceipt tx = optTrans.get();

            if (bitchCoin.getBlockNumber() != tx.getBlockNumber().intValue()) {
                Logs.scheduledLogger.error(String.format("blockNumber error,bitchCoin.getBlockNumber: %d, tx.getBlockNumber: %d, txid: %s",
                        bitchCoin.getBlockNumber(), tx.getBlockNumber().intValue(), bitchCoin.getTxid()));
                return;
            }
            if (this.currentBlockNumber > bitchCoin.getBlockNumber() + SAFETY_ETH_BLOCKNUMBER) {
                Optional<InputData> optEid = null;
                CoinName coinName = CoinName.get(bitchCoin.getCoinname());
                //double check
                if (ContractAddress.isExistContractAddress(tx.getTo())) {
                    //eth contract token
                    AbstractEthContractCoin coin = ContractAddress.findCoinInstance(tx.getTo());
                    if (!coin.getCoinName().equals(coinName)) {
                        Logs.scheduledLogger.error(String.format("coinname error,txid: %s", bitchCoin.getTxid()));
                        return;
                    }
                    optEid = coin.getTransactionById(tx.getTransactionHash());
                } else {
                    optEid = coinName.getCoin().getTransactionById(tx.getTransactionHash());
                }
                if (!optEid.isPresent()) {
                    Logs.scheduledLogger.debug("getTransactionById return nothing");
                    return;
                }
                EthereumInputData idata = (EthereumInputData) optEid.get();
                //repair gasUsed,set real gasUsed
                idata.setGasUsed(tx.getGasUsed());

                //for bq_db
                String val = decimalsUtil.encode(idata.getValue().toString(), coinName.getDecimals(), coinName.getLocalDecimals());
                coinDao.chargeSuccess(bitchCoin.getUserid(), bitchCoin.getRid(), ChargeStatusEnum.SUCCESS.getId(), bitchCoin.getCoinname(), val);
            }

        });
    }

    //数据库存在的，可能 并未上链，所以，不能 仓促 设为FAILURE，
    public void scanWithdrawCoin() {
        List<BitchCoin> bitchCoins = coinDao.findAll(CoinTypeEnum.WITHDRAW, ChargeStatusEnum.PENDING.getId());
        bitchCoins.forEach(bitchCoin -> {
            Optional<TransactionReceipt> optTrans = getTransactionReceipt(bitchCoin.getTxid());
            if (!optTrans.isPresent()) {
                if (isTxExpired(bitchCoin.getAddtime())) {
                    coinDao.updateWithdrawStatus(bitchCoin.getRid(), WithdrawStatusEnum.FAILURE, 0, 0);
                }
                return;
            }
            TransactionReceipt tx = optTrans.get();
            if (this.currentBlockNumber > tx.getBlockNumber().intValue() + SAFETY_ETH_BLOCKNUMBER) {
                coinDao.updateWithdrawStatus(bitchCoin.getRid(), WithdrawStatusEnum.SUCCESS, tx.getBlockNumber().intValue(), tx.getGasUsed().intValue());
            }
        });
    }

    private boolean isTxExpired(Date addTime) {
        Date now = new Date();
        return (now.getTime() - addTime.getTime()) > TX_EXPIRED_TIME;
    }

    //for scheduledWork
    //TODO WARNING 重复 在 ScheduledWork 里也有
    private Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) {
        try {
            EthGetTransactionReceipt transactionReceipt = this.web3.ethGetTransactionReceipt(transactionHash).send();
            if (transactionReceipt.hasError()) {
                Logs.scheduledLogger.error(transactionReceipt.getError().getMessage());
                return Optional.empty();
            } else {
                return transactionReceipt.getTransactionReceipt();
            }
        } catch (IOException e) {
            Logs.scheduledLogger.error("", e);
            return Optional.empty();
        }
    }

}
