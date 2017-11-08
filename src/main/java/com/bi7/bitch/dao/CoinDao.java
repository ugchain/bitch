package com.bi7.bitch.dao;

import com.bi7.bitch.conf.CoinAttribute;
import com.bi7.bitch.dao.model.*;
import com.bi7.bitch.mapper.primary.CoinMapper;
import com.bi7.bitch.util.DecimalsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by foxer on 2017/8/26.
 */
@Service
public class CoinDao {

    @Autowired
    CoinMapper coinMapper;

    @Autowired
    MyzcDao myzcDao;

    @Autowired
    MyzrDao myzrDao;

    @Autowired
    DecimalsUtil decimalsUtil;
    /*
    status must be 0
    send tx
    get txid
    insert bitchCoin
    update myzc.txid
     */

    @Transactional(value = "bitchDataTransactionManager")
    public synchronized void saveWithdraw(BitchCoin bitchCoin) {
        if (bitchCoin.getType() != CoinTypeEnum.WITHDRAW.getId()) {
            throw new RuntimeException();
        }

        if (bitchCoin.getStatus() != WithdrawStatusEnum.PENDING.getId()) {
            throw new RuntimeException();
        }

        coinMapper.insert(bitchCoin);
        try {
            myzcDao.saveWithdraw(bitchCoin.getRid(), bitchCoin.getTxid(), bitchCoin.getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 因为额度超支，所以要先储存审核状态，随后再真正体现
     */
    @Transactional(value = "bitchDataTransactionManager")
    public synchronized void saveWithdrawAudit(BitchCoin bitchCoin) {
        if (bitchCoin.getType() != CoinTypeEnum.WITHDRAW.getId()) {
            throw new RuntimeException();
        }

        if (bitchCoin.getStatus() != WithdrawStatusEnum.AUDITING.getId()) {
            throw new RuntimeException();
        }

        coinMapper.insert(bitchCoin);
    }

    /*
    检测过程中 可以取得 rid/ txid / id
    检测 txid & status ，监听到 符合条件的 （blocknumber 满足要求），那么更新 status
     */
    @Transactional(value = "bitchDataTransactionManager")
    public synchronized void updateWithdrawStatus(int rid, WithdrawStatusEnum status, int blockNumber, int gasUsed) {

        //即将要设置的
        if ((status != WithdrawStatusEnum.SUCCESS) && (status != WithdrawStatusEnum.FAILURE)) {
            throw new RuntimeException();
        }

        /*
        update bitch_coin
        update myzc
         */
        coinMapper.updateBlockNumberAndStatus(rid, blockNumber, status.getId(), gasUsed, CoinTypeEnum.WITHDRAW.getId());
        try {
            myzcDao.updateStatus(rid, status.getId());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


    //for scheduled work
    public boolean exist(String txid, int type) {
        BitchCoin bitchCoin = coinMapper.findOne(txid, type);
        return bitchCoin != null;
    }

    //for scheduled work
    public List<BitchCoin> findAll(CoinTypeEnum type, int status) {
        return coinMapper.findAll(type.getId(), status);
    }

    /*
    insert myzr
    get rid
    insert bitch_coin
     */
    @Transactional(value = "bitchDataTransactionManager")
    public synchronized void saveCharge(BitchCoin bitchCoin, CoinAttribute coinAttr) {
        if (bitchCoin.getType() != CoinTypeEnum.CHARGE.getId()) {
            throw new RuntimeException();
        }
        if (bitchCoin.getStatus() != ChargeStatusEnum.PENDING.getId()) {
            throw new RuntimeException();
        }
        MyzrModel zr = new MyzrModel();

        zr.setUserid(bitchCoin.getUserid());
        zr.setUsername(bitchCoin.getTo());
        zr.setCoinname(bitchCoin.getCoinname());
        zr.setTxid(bitchCoin.getTxid());

        String val = decimalsUtil.encode(bitchCoin.getValue(), coinAttr.getDecimal(), coinAttr.getLocalDecimal());
        zr.setNum(val);
//        zr.setFee();
        zr.setMum(val);
        zr.setBlocknumber(bitchCoin.getBlockNumber());
        zr.setAddtime(bitchCoin.getAddtime().getTime() / 1000);
        zr.setStatus(bitchCoin.getStatus());

        int rid = 0;
        try {
            rid = myzrDao.insert(zr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bitchCoin.setRid(rid);
        coinMapper.insert(bitchCoin);
    }

    @Transactional(value = "bitchDataTransactionManager")
    public synchronized void chargeFailure(int rid, int status) {
        /*
        update bitch_coin
        update myzr_coin where
        update user_coin
         */
        if (status != ChargeStatusEnum.FAILURE.getId()) {
            throw new RuntimeException();
        }
        coinMapper.updateStatus(rid, status, CoinTypeEnum.CHARGE.getId());
        try {
            myzrDao.updateStatus(rid, status);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Transactional(value = "bitchDataTransactionManager")
    public synchronized void chargeSuccess(int userid, int rid, int status, String coinname, String incVal) {
        if (status != ChargeStatusEnum.SUCCESS.getId()) {
            throw new RuntimeException();
        }
        coinMapper.updateStatus(rid, status, CoinTypeEnum.CHARGE.getId());
        try {
            myzrDao.chargeSuccess(userid, rid, status, coinname, incVal);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
