package com.bi7.bitch.service;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.ethereum.ContractAddress;
import com.bi7.bitch.chain.ethereum.EthereumInputData;
import com.bi7.bitch.chain.ethereum.contract.AbstractEthContractCoin;
import com.bi7.bitch.chain.ethereum.contract.ContractInputData;
import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.dao.CoinDao;
import com.bi7.bitch.dao.model.*;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.response.Status;
import com.bi7.bitch.util.DecimalsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


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
    private Web3jService web3;

    private final static int SAFETY_ETH_BLOCKNUMBER = 10;

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
/*
        udpate bitch_coin
        update myzc
         */
//        BigInteger val = decimalsUtil.decode(value, coinname.getDecimals());
        BigInteger feeExact = decimalsUtil.decode(fee, coinname.getDecimals());
        EthereumInputData idata = null;
        //TODO ERROR : 先创建 tx ，插入成功后，再send

        try {
            idata = web3.sendTransaction(address, coinname, val);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            return Msg.ERROR;
        }
        /*
        create tx
        send tx to p2p
         */

        BitchCoin bitchCoin = new BitchCoin();
        bitchCoin.setRid(zcId);
        bitchCoin.setUserid(userid);
        bitchCoin.setCoinname(coinname.getCoinName());
        bitchCoin.setType(CoinTypeEnum.WITHDRAW.getId());
        bitchCoin.setFrom(idata.getFrom());
        bitchCoin.setTo(address);
        bitchCoin.setValue(val.toString());
        bitchCoin.setFee(feeExact.toString());
        bitchCoin.setBlockNumber(0);
        bitchCoin.setGasUsed(idata.getGasUsed().intValue());
        bitchCoin.setGasPrice(idata.getGasPrice().intValue());
        bitchCoin.setStatus(WithdrawStatusEnum.PENDING.getId());
        bitchCoin.setTxid(idata.getTxid());
        Date date = new Date();
        bitchCoin.setAddtime(date);
        bitchCoin.setUpdatetime(date);


        try {
            coinDao.saveWithdraw(bitchCoin);
        } catch (Exception e) {
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
        //TODO 类型强转
        bitchCoin.setBlockNumber(idata.getBlockNumber().intValue());
        bitchCoin.setTxid(idata.getTxid());
        bitchCoin.setGasPrice(idata.getGasPrice().intValue());
        bitchCoin.setGasUsed(idata.getGasUsed().intValue());
        bitchCoin.setStatus(ChargeStatusEnum.PENDING.getId());
        bitchCoin.setAddtime(new Date());

        coinDao.saveCharge(bitchCoin, coinName);
    }


    /*
    扫描充值表，并且根据区块状态修改数据库状态
     */
    public void scanChargeCoin(Function<String, Optional<Transaction>> func) {
        List<BitchCoin> bitchCoins = coinDao.findAll(CoinTypeEnum.CHARGE, ChargeStatusEnum.PENDING.getId());
        bitchCoins.forEach(bitchCoin -> {

            Optional<Transaction> optTrans = func.apply(bitchCoin.getTxid());
            if (!optTrans.isPresent()) {
                //交易消失，设置为 失败
                coinDao.chargeFailure(bitchCoin.getRid(), ChargeStatusEnum.FAILURE.getId());
                return;
            }
            Transaction tx = optTrans.get();

            /*
            if idata == null
                update status to failure
            if blockNumber > bitchCoin.blockNumber + 10
                update status to success
             */

            //正常情况下不可能发生，若监听区块的程序正常，那么处于pendding 状态的tx 是不会进入数据库中的
            if (tx.getBlockNumberRaw() == null) {
                return;
            }
            if (bitchCoin.getBlockNumber() != tx.getBlockNumber().intValue()) {
                Logs.scheduledLogger.error(String.format("blockNumber error,bitchCoin.getBlockNumber: %d, tx.getBlockNumber: %d, txid: %s",
                        bitchCoin.getBlockNumber(), tx.getBlockNumber().intValue(), bitchCoin.getTxid()));
                return;
            }
            if (this.currentBlockNumber > bitchCoin.getBlockNumber() + SAFETY_ETH_BLOCKNUMBER) {
                EthereumInputData idata = null;
                CoinName coinName = CoinName.get(bitchCoin.getCoinname());
                if (ContractAddress.isExistContractAddress(tx.getTo())) {
                    //eth contract token
                    AbstractEthContractCoin coin = ContractAddress.findCoinInstance(tx.getTo());
                    if (!coin.getCoinName().equals(coinName)) {
                        Logs.scheduledLogger.error(String.format("coinname error,txid: %s", bitchCoin.getTxid()));
                        return;
                    }
                    idata = (EthereumInputData) coin.deserizeTransaction(tx);
                } else {
                    idata = new EthereumInputData();
                    //eth transaction
//                    idata.setTxid(tx.getHash());
//                    idata.setGasPrice(Convert.fromWei(new BigDecimal(tx.getGasPrice()), Convert.Unit.GWEI).toBigInteger());
//                    idata.setGasUsed(tx.getGas());
//                    idata.setBlockNumber(tx.getBlockNumber());
//                    idata.setFrom(tx.getFrom());
//                    idata.setTo(tx.getTo());
                    idata.setValue(tx.getValue());
                }

                String val = decimalsUtil.encode(idata.getValue().toString(), coinName.getDecimals(), coinName.getLocalDecimals());
                coinDao.chargeSuccess(bitchCoin.getUserid(), bitchCoin.getRid(), ChargeStatusEnum.SUCCESS.getId(), bitchCoin.getCoinname(), val);
            }

        });
    }

    public void scanWithdrawCoin(Function<String, Optional<Transaction>> func) {
        List<BitchCoin> bitchCoins = coinDao.findAll(CoinTypeEnum.WITHDRAW, ChargeStatusEnum.PENDING.getId());
        bitchCoins.forEach(bitchCoin -> {
            Optional<Transaction> optTrans = func.apply(bitchCoin.getTxid());
            if (!optTrans.isPresent()) {
                coinDao.updateWithdrawStatus(bitchCoin.getRid(), 0, WithdrawStatusEnum.FAILURE.getId());
                return;
            }
            Transaction tx = optTrans.get();
            //TODO getBlockNumber 应该根据 Raw 判断是否为 null，否则会抛异常，由于pedding状态的 tx 没有 blockNumber 数据
            if (tx.getBlockNumberRaw() != null) {
                if (this.currentBlockNumber > tx.getBlockNumber().intValue() + SAFETY_ETH_BLOCKNUMBER) {
                    coinDao.updateWithdrawStatus(bitchCoin.getRid(), tx.getBlockNumber().intValue(), WithdrawStatusEnum.SUCCESS.getId());
                }
            }
        });
    }
}
