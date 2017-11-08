package com.bi7.bitch.service;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.Chains;
import com.bi7.bitch.chain.ITransaction;
import com.bi7.bitch.chain.Transaction;
import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.conf.CoinAttribute;
import com.bi7.bitch.conf.GethConfig;
import com.bi7.bitch.dao.CoinDao;
import com.bi7.bitch.dao.model.*;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.response.Status;
import com.bi7.bitch.util.DecimalsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * Created by foxer on 2017/8/24.
 */
@Service
public class CoinService {
    private final static Log log = LogFactory.getLog(CoinService.class);

    @Autowired
    private AppConfig config;

    @Autowired
    private GethConfig gethConfig;

    @Autowired
    private CoinDao coinDao;

    @Autowired
    private DecimalsUtil decimalsUtil;

    @Autowired
    private Chains chains;

    private final static long TX_EXPIRED_TIME = 60 * 60 * 1000;// 1 hour

    public Msg withdraw(int zcId, int userid, String address, CoinAttribute coinAttr, String value, String fee) {
        BigInteger val;
        try {
            val = decimalsUtil.decode(value, coinAttr.getDecimal());
        } catch (Exception e) {
            log.error("", e);
            return Msg.ERROR;
        }

        BitchCoin bitchCoin = new BitchCoin();
        bitchCoin.setRid(zcId);
        bitchCoin.setUserid(userid);
        bitchCoin.setCoinname(coinAttr.getName());
        bitchCoin.setType(CoinTypeEnum.WITHDRAW.getId());
        bitchCoin.setTo(address);
        bitchCoin.setValue(val.toString());
        bitchCoin.setFee(decimalsUtil.decode(fee, coinAttr.getDecimal()).toString());
        bitchCoin.setBlockNumber(0);
        Date date = new Date();
        bitchCoin.setAddtime(date);
        bitchCoin.setUpdatetime(date);
        if (val.compareTo(coinAttr.getWithdrawLimit()) > 0) {
            //入库，但是状态为0
            bitchCoin.setGasPrice(gethConfig.getWithdrawGasPrice().intValue());
            bitchCoin.setFrom("");
            bitchCoin.setStatus(WithdrawStatusEnum.AUDITING.getId());

            try {
                coinDao.saveWithdrawAudit(bitchCoin);
                log.info(String.format("withdraw value > limit,value: %s, fee: %s, limit: %d", val, fee, coinAttr.getWithdrawLimit()));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("", e);
                return Msg.ERROR;
            }

            return new Msg(Status.OK);
        } else {
            Transaction transaction = chains.newInstance(coinAttr, gethConfig.getCredentials());

            bitchCoin.setFrom(transaction.getFrom());
            bitchCoin.setStatus(WithdrawStatusEnum.PENDING.getId());
            bitchCoin.setGasPrice(transaction.getGasPrice().intValue());
            bitchCoin.setGasUsed(0);
            try {
                bitchCoin.setTxid(transaction.buildTxId());
            } catch (IOException e) {
                e.printStackTrace();
                log.error("", e);
                return Msg.ERROR;
            }

            try {
                coinDao.saveWithdraw(bitchCoin);
            } catch (Exception e) {
                log.error("", e);
                return Msg.ERROR;
            }

            try {
                transaction.send();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("", e);
                return Msg.ERROR;
            }
            return new Msg(Status.OK);
        }
    }

    //for scheduled work
    public synchronized boolean saveCharge(BitchWallet bitchWallet, ITransaction tx) {

        if (coinDao.exist(tx.getTxId(), CoinTypeEnum.CHARGE.getId())) {
            return false;
        }
        BitchCoin bitchCoin = new BitchCoin();

        bitchCoin.setUserid(bitchWallet.getUserid());
        bitchCoin.setCoinname(tx.getCoinAttr().getName());
        bitchCoin.setType(CoinTypeEnum.CHARGE.getId());
        bitchCoin.setFrom(tx.getFrom());
        bitchCoin.setTo(tx.getTo());
        bitchCoin.setValue(tx.getValue().toString());
        bitchCoin.setFee("0");
        bitchCoin.setBlockNumber(tx.getBlockNumber().intValue());
        bitchCoin.setTxid(tx.getTxId());
        bitchCoin.setGasPrice(tx.getGasPrice().intValue());
        bitchCoin.setGasUsed(tx.getGasUsed().intValue());
        bitchCoin.setStatus(ChargeStatusEnum.PENDING.getId());
        bitchCoin.setAddtime(new Date());

        coinDao.saveCharge(bitchCoin, tx.getCoinAttr());
        return true;
    }

    //数据库中存在的，链上肯定存在，否则立即设为 FAILURE
    public void scanChargeCoin() {
        List<BitchCoin> bitchCoins = coinDao.findAll(CoinTypeEnum.CHARGE, ChargeStatusEnum.PENDING.getId());
        bitchCoins.forEach(bitchCoin -> {
            chains.getTransactionById(bitchCoin.getTxid()).filter(tx -> {
                if (bitchCoin.getBlockNumber() != tx.getBlockNumber().intValue()) {
                    Logs.scheduledLogger.error(String.format("blockNumber error,bitchCoin.getBlockNumber: %d, tx.getBlockNumber: %d, txid: %s",
                            bitchCoin.getBlockNumber(), tx.getBlockNumber().intValue(), bitchCoin.getTxid()));
                    return false;
                }
                return true;
            }).filter(ITransaction::isConfirmed).map(tx -> {
                String val = decimalsUtil.encode(tx.getValue().toString(), tx.getCoinAttr().getDecimal(), tx.getCoinAttr().getLocalDecimal());
                coinDao.chargeSuccess(bitchCoin.getUserid(), bitchCoin.getRid(), ChargeStatusEnum.SUCCESS.getId(), bitchCoin.getCoinname(), val);
                return 1;
            }).orElseGet(() -> {
                coinDao.chargeFailure(bitchCoin.getRid(), ChargeStatusEnum.FAILURE.getId());
                return 1;
            });
        });
    }

    //数据库存在的，可能 并未上链，所以，不能 仓促 设为FAILURE，
    public void scanWithdrawCoin() {
        List<BitchCoin> bitchCoins = coinDao.findAll(CoinTypeEnum.WITHDRAW, ChargeStatusEnum.PENDING.getId());
        bitchCoins.forEach(bitchCoin -> {
            chains.getTransactionById(bitchCoin.getTxid()).filter(ITransaction::isConfirmed).map(tx -> {
                coinDao.updateWithdrawStatus(bitchCoin.getRid(), WithdrawStatusEnum.SUCCESS, tx.getBlockNumber().intValue(), tx.getGasUsed().intValue());
                return 1;
            }).orElseGet(() -> {
                if (isTxExpired(bitchCoin.getAddtime())) {
                    coinDao.updateWithdrawStatus(bitchCoin.getRid(), WithdrawStatusEnum.FAILURE, 0, 0);
                }
                return 1;
            });
        });
    }

    private boolean isTxExpired(Date addTime) {
        Date now = new Date();
        return (now.getTime() - addTime.getTime()) > TX_EXPIRED_TIME;
    }
}
